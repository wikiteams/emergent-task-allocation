package github;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.SystemUtils;

public class MyDatabaseConnector {

	private static Connection connection = null;
	private static ResultSet resultSet = null;
	private static String filename = "workload.db";
	private static String filepath = SystemUtils.IS_OS_LINUX ? "data/"
			: "data\\";

	public static Boolean init() {
		boolean success = true;
		try {
			Class.forName("org.sqlite.JDBC");
			String connectionString = "jdbc:sqlite:" + filepath + filename;
			connection = DriverManager.getConnection(connectionString);
			resultSet = createResultSet();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		System.out.println("Opened database successfully");
		return success;
	}

	private static ResultSet createResultSet() throws SQLException {
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(360); // set timeout to 360 sec.
		return statement
				.executeQuery("select time, taskid, language, sum(workdone), "
						+ "sum(workrequired) from workload group by "
						+ "taskid, language order by time asc");
	}

}
