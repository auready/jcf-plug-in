package jcf.gen.eclipse.core.jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.JcfGeneratorPlugIn;
import jcf.gen.eclipse.core.jdbc.model.TableColumns;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DatabaseService {
	
	public DatabaseService() {
		init();
	}
	
	private void init() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate();
		
		this.jdbcTemplate = jdbcTemplate;
		this.jdbcTemplate.setDataSource(getDataSource());
		
		this.dbms = this.getPreference(Constants.DB_CATEGORY_RADIO);
	}
	
	private JdbcTemplate jdbcTemplate;

	private String dbms;
	
	public DataSource getDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		
		dataSource.setDriverClassName(this.getPreference(Constants.DB_DRIVER_CLASS));
		dataSource.setUrl(this.getPreference(Constants.DB_URL));
		dataSource.setUsername(this.getPreference(Constants.DB_USERNAME));
		dataSource.setPassword(this.getPreference(Constants.DB_PASSWORD));
		
		return dataSource;
	}
	
	public String getPreference(String keyProperty) {
		return JcfGeneratorPlugIn.getDefault().getPreferenceStore().getString(keyProperty);
	}
	
	public String[] getTableNames(String objName) {
		HashMap<String, String> map = new HashMap<String, String>();
	 	map.put("OBJECT_NAME", objName);
		
		List<String> list = this.jdbcTemplate.queryForList(this.createQuery("TABLE_NAME", map), String.class);
		
		return list.toArray(new String[list.size()]);
	}
	
	public List<TableColumns> getColumnList(String tableName) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("TABLE_NAME", tableName);
		
		List<Map> rows = this.jdbcTemplate.queryForList(this.createQuery("COLUMN_NAME", map));
		
		ArrayList<TableColumns> result = new ArrayList<TableColumns>();
		
		for (Map row : rows) {
			TableColumns col = new TableColumns();
			
			col.setTableName((String) row.get("TABLE_NAME"));
			col.setTableComment((String) row.get("TABLE_COMMENT"));
			col.setColumnName((String) row.get("COLUMN_NAME"));
			col.setColumnCommnet((String) row.get("COLUMN_COMMENT"));
			col.setPk((String) row.get("PK"));
			col.setDataType((String) row.get("DATA_TYPE"));
			col.setDataLength(String.valueOf(row.get("DATA_LENGTH")));
			col.setCharLength(String.valueOf(row.get("CHAR_LENGTH")));
			col.setDataPrecision(String.valueOf(row.get("DATA_PRECISION")));
			col.setDataScale(String.valueOf(row.get("DATA_SCALE")));
			col.setNullable((String) row.get("NULLABLE"));
			col.setColumnId(String.valueOf(row.get("COLUMN_ID")));
			col.setDataLength(String.valueOf(row.get("DEFAULT_LENGTH")));
			col.setDataDefault(String.valueOf(row.get("DATA_DEFAULT")));
			
			result.add(col);
		}
		
		return result;
	}
	
	private String createQuery(String type, HashMap<String, String> model) {
		StringBuffer sb = new StringBuffer();
		
		if ("TABLE_NAME".equals(type)) {
			String objName = (String) model.get("OBJECT_NAME");
			
			sb.append("SELECT (SELECT UTC.TABLE_NAME || ' [' || UTC.COMMENTS || ']' \n");
			sb.append("         FROM USER_TAB_COMMENTS UTC \n");
		 	sb.append("        WHERE UTC.TABLE_TYPE = UO.OBJECT_TYPE \n");
		 	sb.append("           AND UTC.TABLE_NAME = UO.OBJECT_NAME) AS TABLE_NAME \n");
		 	sb.append("  FROM USER_OBJECTS UO \n");
		 	sb.append(" WHERE UO.OBJECT_TYPE IN ('TABLE', 'VIEW') \n");
		 	sb.append("   AND UO.STATUS = 'VALID' \n");
		 	
		 	if (StringUtils.isNotEmpty(objName)) {
		 		sb.append("   AND UO.OBJECT_NAME LIKE '" + objName + "%' \n");
		 	}
		 	
		 	sb.append("  ORDER BY 1 \n");
			
		} else if ("COLUMN_NAME".equals(type)) {
			String tableName = (String) model.get("TABLE_NAME");
			
			sb.append("SELECT T.TABLE_NAME, \n");
			sb.append("       (SELECT A.COMMENTS \n");
			sb.append("          FROM USER_TAB_COMMENTS A \n");
			sb.append("         WHERE A.TABLE_TYPE = 'TABLE' \n");
			sb.append("           AND A.TABLE_NAME = T.TABLE_NAME) AS TABLE_COMMENT, \n");
			sb.append("       T.COLUMN_NAME, \n");
			sb.append("       (SELECT UCC.COMMENTS \n");
			sb.append("          FROM USER_COL_COMMENTS UCC \n");
			sb.append("         WHERE UCC.TABLE_NAME = T.TABLE_NAME \n");
			sb.append("           AND UCC.COLUMN_NAME = T.COLUMN_NAME) AS COLUMN_COMMENT, \n");
			sb.append("       (SELECT DECODE(UIC.COLUMN_NAME, NULL, NULL, 'PK') \n");
			sb.append("          FROM USER_IND_COLUMNS UIC, USER_CONSTRAINTS UC \n");
			sb.append("         WHERE UIC.COLUMN_NAME = T.COLUMN_NAME \n");
			sb.append("           AND UIC.INDEX_NAME = UC.INDEX_NAME \n");
			sb.append("           AND UIC.TABLE_NAME = UC.TABLE_NAME \n");
			sb.append("           AND UC.CONSTRAINT_TYPE = 'P' \n");
			sb.append("           AND UC.TABLE_NAME = T.TABLE_NAME) AS PK, \n");
			sb.append("       T.DATA_TYPE, \n");
			sb.append("       T.DATA_LENGTH, \n");
			sb.append("       T.CHAR_LENGTH, \n");
			sb.append("       T.DATA_PRECISION, \n");
			sb.append("       T.DATA_SCALE, \n");
			sb.append("       T.NULLABLE, \n");
			sb.append("       T.COLUMN_ID, \n");
			sb.append("       T.DEFAULT_LENGTH, \n");
			sb.append("       T.DATA_DEFAULT \n");
			sb.append("  FROM USER_TAB_COLUMNS T \n");
			sb.append(" WHERE T.TABLE_NAME = '" + tableName + "' \n");
			sb.append(" ORDER BY T.COLUMN_ID \n");
		}
		
		return sb.toString();
	}
}
