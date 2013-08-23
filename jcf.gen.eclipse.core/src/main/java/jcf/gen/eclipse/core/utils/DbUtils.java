package jcf.gen.eclipse.core.utils;

import java.sql.Connection;
import java.sql.DriverManager;

import jcf.gen.eclipse.core.Constants;

public class DbUtils {

	public static String convertToDataType(String colType) {
		String dataType = "String";
		
		if ("NUMBER".equals(colType)) {
//			dataType = "BigDecimal";
			dataType = "int";
		} else if ("BLOB".equals(colType)) {
			dataType = "byte[]";
		} 
		
		return dataType;
	}
	
	public static boolean hasNumberType(String colType) {
		return "NUMBER".equals(colType);
	}
	
	public static boolean isDbConn() {
		boolean result = true;
		
		String className = PreferenceUtil.getStringValue(Constants.DB_DRIVER_CLASS);
		String url = PreferenceUtil.getStringValue(Constants.DB_URL);
		String username = PreferenceUtil.getStringValue(Constants.DB_USERNAME);
		String password = PreferenceUtil.getStringValue(Constants.DB_PASSWORD);
		
		try {
			Class.forName(className);
			
			Connection conn = DriverManager.getConnection(url, username, password);
			conn.close();
			
		} catch (Exception e) {
			result = false;
		}
		
		return result;
	}
}
