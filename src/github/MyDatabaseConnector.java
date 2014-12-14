package github;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import logger.PjiitOutputter;

import org.apache.commons.lang3.SystemUtils;

import collaboration.Task;

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
			assert resultSet.first();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return false;
		}
		System.out.println("Opened [sqlite] database successfully");
		return success;
	}

	private static ResultSet createResultSet() throws SQLException {
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(360); // set timeout to 360 sec.
		return statement
				.executeQuery("select time, taskid, language, sum(workdone), "
						+ "sum(workrequired) from workload where taskid in"
						+ " (select taskid from (select time, taskid, language, sum(workdone)"
						+ " as wd, sum(workrequired) as wr from workload group by"
						+ " taskid, language order by time asc) group by taskid HAVING ( count(taskid) > 1 )"
						+ " order by time asc) "
						+ "group by taskid, language order by time asc");
	}

	public static List<Task> get(int count) throws SQLException {
		List<Task> result = new ArrayList<Task>();
		for (int i = 0; i < count; i++) {
			String repoId = resultSet.getString(2);
			Task task = new Task();
			say("Creating Task " + task.getId());
			TaskReconstruction.giveWork(task, resultSet.getString(3),
					resultSet.getInt(4), resultSet.getInt(5));

			while (resultSet.next()) {
				if (resultSet.getString(2).equals(repoId)) {
					TaskReconstruction.giveWork(task, resultSet.getString(3),
							resultSet.getInt(4), resultSet.getInt(5));
				} else {
					resultSet.previous();
					break;
				}
			}

			result.add(task);
		}
		return result;
	}

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}
