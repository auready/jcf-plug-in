package jcf.gen.eclipse.core.utils;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author mina
 * DB ColumnName 을 CamelCase 등으로 변환해주는 클래스.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class AbstractColumnNameConvertMap extends HashMap {

    private static final long serialVersionUID = 1L;

    /* (non-Javadoc)
     * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
     *
     */
    public Object put(Object key, Object value) {
        return super.put((Object) columnNameConvert((String) key), (Object) convertIfLob(value));

    }

    /**
     * @param columnName
     * @return
     * ColumnName을 받아서 변환해서 넘김.
     */
    public abstract String columnNameConvert(String columnName);
    
    /**
     * <p>Blob --> byte[], Clob --> String 으로 변환. 그 이외에는 입력값 그래도 반환.
     * 
     * @param object
     * @return
     */
    public Object convertIfLob(Object object){
    	Object result = object;
    	if( object instanceof Blob ){
    		try {
    			Blob blob = (Blob) object;
    			result = blob.getBytes((long)1, (int)blob.length());
			} catch (SQLException e) {
				throw new RuntimeException("Exception occurred when convert Blob to byte[]", e);
			}
    	}
    	else if( object instanceof Clob ){
    		try {
    			Clob clob = (Clob) object;
				result = clob.getSubString(1, (int) clob.length());
			} catch (SQLException e) {
				throw new RuntimeException("Exception occurred when convert Clob to String", e);
			}
    	}
    	
    	return result;
    }
}
