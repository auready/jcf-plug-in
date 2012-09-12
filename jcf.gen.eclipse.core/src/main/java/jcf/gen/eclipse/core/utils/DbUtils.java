package jcf.gen.eclipse.core.utils;

public class DbUtils {

	public static String convertToDataType(String colType) {
		String dataType = "String";
		
		if ("NUMBER".equals(colType)) {
			dataType = "BigDecimal";
		} else if ("BLOB".equals(colType)) {
			dataType = "byte[]";
		} 
		
		return dataType;
	}
	
	public static boolean hasNumberType(String colType) {
		return "NUMBER".equals(colType);
	}
}
