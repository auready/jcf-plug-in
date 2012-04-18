package jcf.gen.eclipse.core.jdbc.serivce;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcService extends AbstractJdbcService {

	private Connection conn = null;
	
	public JdbcService(String className) {
		super(className);
	}

	@Override
	public Connection getConnection(String url, String username, String password) {
		try {
			conn = DriverManager.getConnection(url, username, password);
		} catch (SQLException se) {
			se.printStackTrace();
		}
		
		return conn;
	}
	
	@Override
	public boolean close(Connection conn) {
		try {
			conn.close();
		} catch (SQLException se) {
			se.printStackTrace();
			return false;
		}
		
		return true;
	}

}
