package com.jfinal.plugin.activerecord;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.log.Logger;
import org.apache.commons.lang.StringUtils;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 *  解决model和table不匹配问题
 *  @author june
 * 2015年04月28日 17:15
 */
public class TableMapping {
    private final Map<Class<? extends Model<?>>, Table> modelToTableMap = new HashMap<Class<? extends Model<?>>, Table>();
    protected final Logger log = Logger.getLogger(getClass());

    private static TableMapping me = new TableMapping();

    private TableMapping() {}

    public static TableMapping me() {
        return me;
    }

    public void putTable(Table table) {
        modelToTableMap.put(table.getModelClass(), table);
    }

//    public Table getTable(Class<? extends Model> modelClass) {
//        Table table = modelToTableMap.get(modelClass);
//        if (table == null)
//            throw new RuntimeException("The Table mapping of model: " + modelClass.getName() + " not exists. Please add mapping to ActiveRecordPlugin: activeRecordPlugin.addMapping(tableName, YourModel.class).");
//
//        return table;
//    }

    @SuppressWarnings("rawtypes")
    public Table getTable(Class<? extends Model> modelClass) {
        Table table = modelToTableMap.get(modelClass);
        Object obj = modelClass;
        Class<? extends Model<?>> mClass = (Class<? extends Model<?>>) obj;
        if (table == null){
            //检测实体类上是否有TableBind注解，如果有，从数据库加载它的table
            TableBind tb = (TableBind) modelClass.getAnnotation(TableBind.class);
            if(tb == null){
                throw new RuntimeException("The Table mapping of model: " + modelClass.getName() + " not exists. Please add mapping to ActiveRecordPlugin: activeRecordPlugin.addMapping(tableName, YourModel.class).");
            }
            String tableName = tb.tableName();
            log.info(modelClass.getName()+"没有找到Table，从数据库加载，tableName："+tableName);
            //数据源名称
            String tbConfName = tb.configName();
            String pkName = tb.pkName();
            Table newTable = null;
            if(StringUtils.isEmpty(pkName)){
                newTable = new Table(tableName, mClass);
            }else{
                newTable = new Table(tableName, pkName, mClass);
            }
            Config config = null;
            if(StringUtils.isEmpty(tbConfName)){
                config = DbKit.getConfig();
            }else{
                config = DbKit.getConfig(tbConfName);
            }
            try {
                doBuild(newTable,config.dataSource.getConnection(),config);
                DbKit.addModelToConfigMapping(newTable.getModelClass(), config);
                modelToTableMap.put(mClass,newTable);
                return newTable;
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        return table;
    }

    private void doBuild(Table table, Connection conn, Config config) throws SQLException {
        table.setColumnTypeMap(config.containerFactory.getAttrsMap());
        if (table.getPrimaryKey() == null)
            table.setPrimaryKey(config.dialect.getDefaultPrimaryKey());

        String sql = config.dialect.forTableBuilderDoBuild(table.getName());
        Statement stm = conn.createStatement();
        ResultSet rs = stm.executeQuery(sql);
        ResultSetMetaData rsmd = rs.getMetaData();

        for (int i=1; i<=rsmd.getColumnCount(); i++) {
            String colName = rsmd.getColumnName(i);
            String colClassName = rsmd.getColumnClassName(i);
            if ("java.lang.String".equals(colClassName)) {
                // varchar, char, enum, set, text, tinytext, mediumtext, longtext
                table.setColumnType(colName, String.class);
            }
            else if ("java.lang.Integer".equals(colClassName)) {
                // int, integer, tinyint, smallint, mediumint
                table.setColumnType(colName, Integer.class);
            }
            else if ("java.lang.Long".equals(colClassName)) {
                // bigint
                table.setColumnType(colName, Long.class);
            }
            // else if ("java.util.Date".equals(colClassName)) {		// java.util.Data can not be returned
            // java.sql.Date, java.sql.Time, java.sql.Timestamp all extends java.util.Data so getDate can return the three types data
            // result.addInfo(colName, java.util.Date.class);
            // }
            else if ("java.sql.Date".equals(colClassName)) {
                // date, year
                table.setColumnType(colName, Date.class);
            }
            else if ("java.lang.Double".equals(colClassName)) {
                // real, double
                table.setColumnType(colName, Double.class);
            }
            else if ("java.lang.Float".equals(colClassName)) {
                // float
                table.setColumnType(colName, Float.class);
            }
            else if ("java.lang.Boolean".equals(colClassName)) {
                // bit
                table.setColumnType(colName, Boolean.class);
            }
            else if ("java.sql.Time".equals(colClassName)) {
                // time
                table.setColumnType(colName, Time.class);
            }
            else if ("java.sql.Timestamp".equals(colClassName)) {
                // timestamp, datetime
                table.setColumnType(colName, Timestamp.class);
            }
            else if ("java.math.BigDecimal".equals(colClassName)) {
                // decimal, numeric
                table.setColumnType(colName, java.math.BigDecimal.class);
            }
            else if ("[B".equals(colClassName)) {
                // binary, varbinary, tinyblob, blob, mediumblob, longblob
                // qjd project: print_info.content varbinary(61800);
                table.setColumnType(colName, byte[].class);
            }
            else {
                int type = rsmd.getColumnType(i);
                if (type == Types.BLOB) {
                    table.setColumnType(colName, byte[].class);
                }
                else if (type == Types.CLOB || type == Types.NCLOB) {
                    table.setColumnType(colName, String.class);
                }
                else {
                    table.setColumnType(colName, String.class);
                }
                // core.TypeConverter
                // throw new RuntimeException("You've got new type to mapping. Please add code in " + TableBuilder.class.getName() + ". The ColumnClassName can't be mapped: " + colClassName);
            }
        }

        rs.close();
        stm.close();
    }
}
