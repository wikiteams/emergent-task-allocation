package github;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.SystemUtils;

public class MyDatabaseConnector {
	
	private static Connection connection = null;
	private static ResultSet rs = null;

	public static Boolean init() {
		boolean success = true;
		String filename = "test.db";
		try {
			Class.forName("org.sqlite.JDBC");
			String connectionString = SystemUtils.IS_OS_LINUX ? "jdbc:sqlite:" + filename
					: "jdbc:sqlite:" + filename;
			connection = DriverManager.getConnection(connectionString);
			createResultSet();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		System.out.println("Opened database successfully");
		return success;
	}
	
	private static Boolean createResultSet() throws SQLException{
		Boolean result = true;
		Statement statement = connection.createStatement();
	    statement.setQueryTimeout(360);  // set timeout to 360 sec.
	    rs = statement.executeQuery("select * from person");
	    return result;
	}

}
