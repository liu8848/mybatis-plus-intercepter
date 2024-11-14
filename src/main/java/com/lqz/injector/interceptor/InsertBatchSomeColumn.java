package com.lqz.injector.interceptor;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
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
import java.util.stream.Collectors;

/**
 * 增强版的批量插入，解决mp 中批量插入null 属性不能使用默认值的问题
 */

@SuppressWarnings("serial")
@AllArgsConstructor
@NoArgsConstructor
public class InsertBatchSomeColumn extends AbstractMethod {

    /**
     * 字段筛选条件
     */
    private Predicate<TableFieldInfo> predicate;

    @SuppressWarnings("Duplicates")
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        KeyGenerator keyGenerator = new NoKeyGenerator();
        SqlMethod sqlMethod = SqlMethod.INSERT_ONE;
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        String insertSqlColumn = tableInfo.getKeyInsertSqlColumn(false) +
                this.filterTableFieldInfo(fieldList, predicate, TableFieldInfo::getInsertSqlColumn, EMPTY);
        String columnScript = LEFT_BRACKET + insertSqlColumn.substring(0, insertSqlColumn.length() - 1) + RIGHT_BRACKET;
        //重写方法，主要修改的是设置赋默认值的方法
        String insertSqlProperty = tableInfo.getKeyInsertSqlProperty(ENTITY_DOT, false) +
                this.filterTableFieldInfo(fieldList, predicate, i -> getInsertSqlProperty(i, ENTITY_DOT), EMPTY);
        insertSqlProperty =
                LEFT_BRACKET + insertSqlProperty.substring(0, insertSqlProperty.length() - 1) + RIGHT_BRACKET;
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
        String sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), columnScript, valuesScript);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this.addInsertMappedStatement(mapperClass, modelClass, getMethod(sqlMethod), sqlSource, keyGenerator,
                keyProperty, keyColumn);
    }

    public String getMethod(SqlMethod sqlMethod) {
        // 自定义 mapper 方法名
        return "insertBatchSomeColumn";
    }

    /**
     * <p>
     * 重写该方法，项目启动加载的时候会设置
     * 如果对应的参数值为空，就使用数据库的默认值
     * </p>
     *
     * @param tableFieldInfo
     * @param prefix
     * @return
     */
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
                value="NOW()";
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

}