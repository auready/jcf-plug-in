package jcf.gen.eclipse.core.utils;

public class DbUtils {

	public static String convertToDataType(String colType) {
		String dataType = "String";
		
		if ("NUMBER".equals(colType)) {
			dataType = "java.math.BigDecimal";
		} else if ("BLOB".equals(colType)) {
			dataType = "byte[]";
		} 
		
		return dataType;
	}
}
