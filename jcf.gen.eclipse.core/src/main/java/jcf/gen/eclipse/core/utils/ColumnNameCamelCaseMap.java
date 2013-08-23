package jcf.gen.eclipse.core.utils;

import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

/**
 * @author mina
 * HashMap으로 가져온 DB ColumnName 에 CamelCase를 적용하는 클래스
 */
public class ColumnNameCamelCaseMap extends AbstractColumnNameConvertMap {

    private static final long serialVersionUID = 1L;

    public String columnNameConvert(String columnName) {
        String newColumnName = null;

        if (columnName.indexOf("_") == -1)
            newColumnName = columnName.toLowerCase();
        else {
            StringBuffer sb = new StringBuffer();
            boolean isFirst = true;
            StringTokenizer tokenizer = new StringTokenizer(columnName, "_");
            while (tokenizer.hasMoreTokens()) {
                if (isFirst)
                    sb.append(tokenizer.nextToken().toLowerCase());
                else {
                    sb.append(StringUtils.capitalize(tokenizer.nextToken()
                            .toLowerCase()));
                }
                isFirst = false;
            }

            newColumnName = sb.toString();
        }
        return newColumnName;
    }
    
    public String camelCaseConverter(String columnName) {
        String newColumnName = null;

        if (columnName.indexOf("_") == -1)
            newColumnName = columnName.toLowerCase();
        else {
            StringBuffer sb = new StringBuffer();
            boolean isFirst = true;
            StringTokenizer tokenizer = new StringTokenizer(columnName, "_");
            while (tokenizer.hasMoreTokens()) {
                if (isFirst)
                    sb.append(tokenizer.nextToken().toLowerCase());
                else {
                    sb.append(StringUtils.capitalize(tokenizer.nextToken()
                            .toLowerCase()));
                }
                isFirst = false;
            }

            newColumnName = sb.toString();
        }
        return newColumnName;
    }
    
    public String pascalCaseConverter(String columnName) {
    	String newColumnName = null;

        if (columnName.indexOf("_") == -1)
            newColumnName = StringUtils.capitalize(columnName.toLowerCase());
        else {
            StringBuffer sb = new StringBuffer();
            StringTokenizer tokenizer = new StringTokenizer(columnName, "_");
            while (tokenizer.hasMoreTokens()) {
                    sb.append(StringUtils.capitalize(tokenizer.nextToken()
                            .toLowerCase()));
            }

            newColumnName = sb.toString();
        }

        return newColumnName;
    }
    
    public String tableNameConvert(String tableName) {
    	String camelCaseName = this.camelCaseConverter(tableName);
    	
    	if (camelCaseName.length() > 1) {
    		return camelCaseName.substring(0, 1).toUpperCase() + camelCaseName.substring(1);
    	} else {
    		return camelCaseName.toUpperCase();
    	}
    }

}
