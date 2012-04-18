package jcf.gen.eclipse.core.jdbc.serivce;

import java.sql.Connection;

public abstract class AbstractJdbcService {
	
	public AbstractJdbcService(String className) {
		try {
			Class.forName(className);
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	}
	
	public abstract Connection getConnection(String url, String username, String password);
	
	public abstract boolean close(Connection conn);
}
