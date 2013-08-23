package jcf.gen.eclipse.core.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import jcf.gen.eclipse.core.Constants;
import jcf.gen.eclipse.core.utils.PreferenceUtil;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class DatabaseAccessor {
	
	private JdbcTemplate jdbcTemplate;
	
	public DatabaseAccessor() {
		jdbcTemplate = new JdbcTemplate(getDataSource()); 
	}
	
	private DataSource getDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		
		dataSource.setDriverClassName(PreferenceUtil.getStringValue(Constants.DB_DRIVER_CLASS));
		dataSource.setUrl(PreferenceUtil.getStringValue(Constants.DB_URL));
		dataSource.setUsername(PreferenceUtil.getStringValue(Constants.DB_USERNAME));
		dataSource.setPassword(PreferenceUtil.getStringValue(Constants.DB_PASSWORD));
		
		return dataSource;
	}
	
	@SuppressWarnings("unchecked")
	public String[] getTableNames(String objName) {
		HashMap<String, String> map = new HashMap<String, String>();
	 	map.put("OBJECT_NAME", objName);
		
		List<String> list = jdbcTemplate.queryForList(createQuery("TABLE_NAME", map), String.class);
		
		return list.toArray(new String[list.size()]);
	}
	
	@SuppressWarnings("unchecked")
	public List<TableColumns> getColumnList(String tableName) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("TABLE_NAME", tableName);
		
		List<TableColumns> list = jdbcTemplate.query(createQuery("COLUMN_NAME", map),  
				new RowMapper() {	
					@Override
					public TableColumns mapRow(ResultSet rs, int rownum) throws SQLException {
						TableColumns columns = new TableColumns();
						
						rownum = 1;
						
						columns.setTableName(rs.getString(rownum++));
						columns.setTableComment(rs.getString(rownum++));
						columns.setColumnName(rs.getString(rownum++));
						columns.setColumnCommnet(rs.getString(rownum++));
						columns.setPk(rs.getString(rownum++));
						columns.setDataType(rs.getString(rownum++));
						columns.setDataLength(rs.getString(rownum++));
						columns.setCharLength(rs.getString(rownum++));
						columns.setDataPrecision(rs.getString(rownum++));
						columns.setDataScale(rs.getString(rownum++));
						columns.setNullable(rs.getString(rownum++));
						columns.setColumnId(rs.getString(rownum++));
						columns.setDataLength(rs.getString(rownum++));
						columns.setDataDefault(rs.getString(rownum++));
						
						return columns;
					}
		});
		
		return list;
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
			
			if (StringUtils.hasText(objName)) {
				sb.append("   AND UO.OBJECT_NAME LIKE '" + objName + "%' \n");
			}
			
			sb.append("  ORDER BY 1 \n");
			
		} else if ("COLUMN_NAME".equals(type)) {
			String tableName = (String) model.get("TABLE_NAME");
			Assert.hasText(tableName, "Table name must not be null or empty");
			
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
			sb.append("       T.DEFAULT_LENGTH AS DATA_LENGTH, \n");
			sb.append("       T.DATA_DEFAULT \n");
			sb.append("  FROM USER_TAB_COLUMNS T \n");
			sb.append(" WHERE T.TABLE_NAME = '" + tableName + "' \n");
			sb.append(" ORDER BY T.COLUMN_ID \n");
		}	
		
		return sb.toString();
	}
}
