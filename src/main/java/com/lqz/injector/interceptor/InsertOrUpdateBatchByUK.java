package com.lqz.injector.interceptor;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

/**
 * @ClassName InsertOrUpdateBatchByUK
 * @Description 通过唯一键批量插入或更新
 * @Author liu.qingzong
 * @Date 2024/11/13 11:53
 */
@SuppressWarnings("serial")
@AllArgsConstructor
@NoArgsConstructor
public class InsertOrUpdateBatchByUK extends AbstractMethod {
    private Predicate<TableFieldInfo> predicate;

    private final String INSERT_OR_UPDATE_BATCH = "<script>\ninsert into %s %s values %s ON DUPLICATE KEY UPDATE %s\n</script>";

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        KeyGenerator keyGenerator = new NoKeyGenerator();
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        String insertSqlColumn = tableInfo.getKeyInsertSqlColumn(false) +
                this.filterTableFieldInfo(fieldList, predicate, TableFieldInfo::getInsertSqlColumn, EMPTY);
        //插入列SQL
        String columnScript = LEFT_BRACKET + insertSqlColumn.substring(0, insertSqlColumn.length() - 1) + RIGHT_BRACKET;
        //重写方法，主要修改的是设置赋默认值的方法
        String insertSqlProperty = tableInfo.getKeyInsertSqlProperty(ENTITY_DOT, false) +
                this.filterTableFieldInfo(fieldList, predicate, i -> getInsertSqlProperty(i, ENTITY_DOT), EMPTY);
        insertSqlProperty =
                LEFT_BRACKET + insertSqlProperty.substring(0, insertSqlProperty.length() - 1) + RIGHT_BRACKET;
        //插入值SQL
        String valuesScript = SqlScriptUtils.convertForeach(insertSqlProperty, "list", null, ENTITY, COMMA);

        String keyProperty = null;
        String keyColumn = null;
        // 表包含主键处理逻辑,如果不包含主键当普通字段处理
        if (tableInfo.getIdType() == IdType.AUTO) {
            /* 自增主键 */
            keyGenerator = new Jdbc3KeyGenerator();
            keyProperty = tableInfo.getKeyProperty();
            keyColumn = tableInfo.getKeyColumn();
        } else {
            if (null != tableInfo.getKeySequence()) {
                keyGenerator = TableInfoHelper.genKeyGenerator(tableInfo,builderAssistant ,null, languageDriver);
                keyProperty = tableInfo.getKeyProperty();
                keyColumn = tableInfo.getKeyColumn();
            }
        }

        //更新SQL
        String updateSqlProperty=this.filterTableFieldInfo(fieldList,i->!i.getColumn().equals(tableInfo.getKeyColumn()), this::getUpdateSqlProperty,EMPTY);
        String updateScript=updateSqlProperty.substring(0, updateSqlProperty.length() - 1);

        String sql = String.format(INSERT_OR_UPDATE_BATCH, tableInfo.getTableName(), columnScript, valuesScript,updateScript);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addInsertMappedStatement(mapperClass, modelClass, getMethod(), sqlSource, keyGenerator,
                keyProperty, keyColumn);
    }

    public String getMethod() {
        // 自定义 mapper 方法名
        return "insertOrUpdateBatchByUK";
    }

    private String getInsertSqlProperty(TableFieldInfo tableFieldInfo, final String prefix) {
        String newPrefix = prefix == null ? "" : prefix;
        String elPart = SqlScriptUtils.safeParam(newPrefix + tableFieldInfo.getEl());
        //设置默认值
        String value="default";
        FieldFill fieldFill = tableFieldInfo.getFieldFill();
        if(fieldFill.equals(FieldFill.INSERT)) {
            Class<?> propertyType = tableFieldInfo.getPropertyType();
            String property = tableFieldInfo.getProperty();
            if (propertyType == Date.class) {
                value = SINGLE_QUOTE + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + SINGLE_QUOTE;
            } else if (propertyType == String.class) {
                if (property.equals("createName")) {
                    value = SINGLE_QUOTE + "admin" + SINGLE_QUOTE;
                }
            }
        }
        //属性为空时使用默认值
        String result= SqlScriptUtils.convertIf(elPart, String.format("%s != null", newPrefix + tableFieldInfo.getEl()), false)
                + SqlScriptUtils.convertIf(value, String.format("%s == null", newPrefix + tableFieldInfo.getEl()), false);

        return result + ",";
    }

    private String getUpdateSqlProperty(TableFieldInfo tableFieldInfo) {
        String sql=tableFieldInfo.getColumn()+"=";
        String value="values("+tableFieldInfo.getColumn()+")";

        FieldFill fieldFill = tableFieldInfo.getFieldFill();
        if(fieldFill.equals(FieldFill.UPDATE)) {
            Class<?> propertyType = tableFieldInfo.getPropertyType();
            String property = tableFieldInfo.getProperty();
            if (propertyType == Date.class) {
                 value = SINGLE_QUOTE + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + SINGLE_QUOTE;
            } else if (propertyType == String.class) {
                if (property.equals("modifyName")) {
                    value = SINGLE_QUOTE + "admin" + SINGLE_QUOTE;
                }
            }
        }else if(fieldFill.equals(FieldFill.INSERT)) {
            return "";
        }
        return sql+value+",";
    }
}
