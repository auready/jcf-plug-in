package jcf.gen.eclipse.core.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jcf.gen.eclipse.core.jdbc.model.TableColumns;

public class JdbcDataAceess {
	
	private static final Logger logger = LoggerFactory.getLogger(JdbcDataAceess.class);
	
	private final String ORACLE_TABLE_NAME = "ORACLE_TABLE_NAME";
	private final String ORACLE_COLUMN_NAME = "ORACLE_COLUMN_NAME";
	
	private Connection conn = null;
	
	public JdbcDataAceess() {
	}
	
	public JdbcDataAceess(Connection conn) {
		this.setConn(conn);
	}
	
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	
	public Connection getConnection() {
		return this.conn;
	}
	
	public String createQuery(String type, HashMap<String, String> model) {
		StringBuffer sb = new StringBuffer();
		
		if (type.equals(ORACLE_TABLE_NAME)) {
			sb.append("SELECT T.OBJECT_NAME AS TABLE_NAME \n");
			sb.append("  FROM USER_OBJECTS T \n");
			sb.append(" WHERE T.OBJECT_TYPE = 'TABLE' \n");
			sb.append("   AND T.STATUS = 'VALID' \n");
			sb.append(" ORDER BY 1 \n");
			
		} else if (type.equals(ORACLE_COLUMN_NAME)) {
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
		
		logger.info(sb.toString());
		
		return sb.toString();
	}
	
	public List<String> getTableNameList(String type) throws Exception {
		Connection conn = this.getConnection();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		ArrayList<String> list = new ArrayList<String>();
		
		try {
			String sql = this.createQuery(this.getDbType(type, "TABLE"), null);
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				list.add(rs.getString(1));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs.close();
			pstmt.cancel();
		}
		
		return list;
	}
	
	public List<TableColumns> getColumnList(String type, String tableName) throws Exception {
		Connection conn = this.getConnection();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("TABLE_NAME", tableName);
		
		ArrayList<TableColumns> list = new ArrayList<TableColumns>();
		
		try {
			String sql = this.createQuery(this.getDbType(type, "COLUMN"), map);
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			int index;
			
			while (rs.next()) {
				TableColumns col = new TableColumns();
				
				index = 1;
				
				col.setTableName(rs.getString(index++));
				col.setTableComment(rs.getString(index++));
				col.setColumnName(rs.getString(index++));
				col.setColumnCommnet(rs.getString(index++));
				col.setPk(rs.getString(index++));
				col.setDataType(rs.getString(index++));
				col.setDataLength(String.valueOf(rs.getInt(index++)));
				col.setCharLength(String.valueOf(rs.getInt(index++)));
				col.setDataPrecision(String.valueOf(rs.getInt(index++)));
				col.setDataScale(String.valueOf(rs.getInt(index++)));
				col.setNullable(rs.getString(index++));
				col.setColumnId(String.valueOf(rs.getInt(index++)));
				col.setDataLength(String.valueOf(rs.getInt(index++)));
				col.setDataDefault(String.valueOf(rs.getLong(index++)));
				
				list.add(col);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs.close();
			pstmt.close();
		}
		
		return list;
	}
	
	private String getDbType(String dbType, String category) {
		if (dbType.equals("oracle")) {
			if (category.equals("TABLE")) return ORACLE_TABLE_NAME;
			if (category.equals("COLUMN")) return ORACLE_COLUMN_NAME;
		} 
		
		return "";
	}
}
