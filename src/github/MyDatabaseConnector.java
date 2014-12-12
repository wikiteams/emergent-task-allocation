package github;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.lang3.SystemUtils;

public class MyDatabaseConnector {

	public static Boolean init() {
		boolean success = true;
		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			String connectionString = SystemUtils.IS_OS_LINUX ? "jdbc:sqlite:test.db"
					: "jdbc:sqlite:test.db";
			c = DriverManager.getConnection(connectionString);
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			success = false;
		}
		System.out.println("Opened database successfully");
		return success;
	}

}
