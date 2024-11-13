package com.lqz.injector.interceptor;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.util.List;
import java.util.function.Predicate;

/**
 * @ClassName UpdateSomeColumn
 * @Description 更新部分列
 * @Author liu.qingzong
 * @Date 2024/11/13 14:48
 */
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSomeColumnById extends AbstractMethod {

    private Predicate<TableFieldInfo> predicate;

    private final String UPDATE_SOME_COLUMN = "<script>\nUPDATE %s %s WHERE %s=%s\n</script>";

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {

        String tableName = tableInfo.getTableName();
        String keyColumn = tableInfo.getKeyColumn();
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();

        String updateValueScript = this.filterTableFieldInfo(fieldList,
                field -> !field.getColumn().equals(keyColumn) && !field.getFieldFill().equals(FieldFill.UPDATE),
                field -> getUpdatePropertySql(field, ENTITY_DOT),
                EMPTY);
        String s = SqlScriptUtils.convertSet(updateValueScript);

        String sql = String.format(UPDATE_SOME_COLUMN, tableName, s, keyColumn, SqlScriptUtils.safeParam(ENTITY_DOT + tableInfo.getKeyProperty()));
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addUpdateMappedStatement(mapperClass,modelClass,getMethod(),sqlSource);
    }

    public String getMethod() {
        // 自定义 mapper 方法名
        return "updateSomeColumnById";
    }


    private String getUpdatePropertySql(TableFieldInfo tableFieldInfo,String prefix) {
        String newPrefix = prefix==null?"":prefix;
        String setSql = tableFieldInfo.getColumn()+"="+SqlScriptUtils.safeParam(newPrefix + tableFieldInfo.getEl())+COMMA;
        return SqlScriptUtils.convertIf(setSql,String.format("%s != null",newPrefix+tableFieldInfo.getEl()),false);
    }
}
