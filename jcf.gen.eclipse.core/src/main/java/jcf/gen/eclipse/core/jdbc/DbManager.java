package jcf.gen.eclipse.core.jdbc;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import jcf.gen.eclipse.core.jdbc.dao.JdbcDataAceess;
import jcf.gen.eclipse.core.jdbc.model.TableColumns;
import jcf.gen.eclipse.core.jdbc.serivce.JdbcService;
import jcf.gen.eclipse.core.utils.FileUtils;

public class DbManager {
	
	private static JdbcService service = null;
	
	private static String driverClass;
	private static String url;
	private static String username;
	private static String password;
	
	private static String dbCategory;
	
	public void init(String propertyFilePath) {
		HashMap<String, String> dbFile = FileUtils.readPropertyFile(propertyFilePath);
		
		driverClass = dbFile.get("jdbc.driverClassName");
		url = dbFile.get("jdbc.url");
		username = dbFile.get("jdbc.username");
		password = dbFile.get("jdbc.password");
		
		dbCategory = "oracle";
		
		service = new JdbcService(driverClass);
	}
	
	public String[] getTableNames() {
		String[] retValue = null;
		
		Connection conn = service.getConnection(url, username, password);
		
		try {
			conn.setAutoCommit(false);
		
			JdbcDataAceess jdbc = new JdbcDataAceess(conn);
			
			List<String> list = jdbc.getTableNameList(dbCategory);
			
			retValue = list.toArray(new String[list.size()]);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.close(conn);
		}
		
		return retValue;
	}
	
	public List<TableColumns> getColumnList(String tableName) {
		List<TableColumns> list = null;
		
		Connection conn = service.getConnection(url, username, password);
		
		try {
			conn.setAutoCommit(false);
		
			JdbcDataAceess jdbc = new JdbcDataAceess(conn);
			
			list = jdbc.getColumnList(dbCategory, tableName);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.close(conn);
		}
		
		return list;
	}
}
