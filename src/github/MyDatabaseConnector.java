package github;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		PreparedStatement statement = connection
				.prepareStatement(
						"select time, taskid, language, sum(workdone), "
								+ "sum(workrequired) from workload where taskid in"
								+ " (select taskid from (select time, taskid, language, sum(workdone)"
								+ " as wd, sum(workrequired) as wr from workload group by"
								+ " taskid, language order by time asc) group by taskid HAVING ( count(taskid) > 1 )"
								+ " order by time asc) "
								+ "group by taskid, language order by time asc",
						ResultSet.TYPE_FORWARD_ONLY,
						// SQLite only supports TYPE_FORWARD_ONLY cursors 
						ResultSet.CONCUR_READ_ONLY);
		statement.setQueryTimeout(360); // set timeout to 360 sec.
		return statement.executeQuery();
	}

	public static List<Task> get(int count) throws SQLException {
		List<Task> result = new ArrayList<Task>();
		for (int i = 0; i < count; i++) {
			
			Entry entry = new Entry(resultSet.getString(2), resultSet.getString(3),
					resultSet.getInt(4), resultSet.getInt(5));
			//results.add(entry);
			Task task = new Task();
			say("Creating [Task] " + task.getId());
			TaskReconstruction.giveWork(task, entry.getSkillName(), entry.getWorkDone(), entry.getWorkLeft());

			while (resultSet.next()) {
				Entry nextEntry = new Entry(resultSet.getString(2), resultSet.getString(3),
						resultSet.getInt(4), resultSet.getInt(5));
				if (nextEntry.getRepoId().equals(entry.getRepoId())) {
					TaskReconstruction.giveWork(task, nextEntry.getSkillName(),
							nextEntry.getWorkDone(), nextEntry.getWorkLeft());
				} else {
					break;
				}
			}

			result.add(task);
		}
		assert count == result.size();
		return result;
	}
	
	//private static Queue<Entry> results = new LinkedList<Entry>();

	private static void say(String s) {
		PjiitOutputter.say(s);
	}

}

class Entry{
	private String repoId;
	private String skillName;
	private Integer workDone;
	private Integer workLeft;
	
	public Entry(String repoId, String skillName, Integer workDone, Integer workLeft){
		this.repoId = repoId;
		this.skillName = skillName;
		this.workDone = workDone;
		this.workLeft = workLeft;
	}

	public String getRepoId() {
		return repoId;
	}

	public void setRepoId(String repoId) {
		this.repoId = repoId;
	}
	
	public String getSkillName() {
		return skillName;
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}

	public Integer getWorkDone() {
		return workDone;
	}

	public void setWorkDone(Integer workDone) {
		this.workDone = workDone;
	}

	public Integer getWorkLeft() {
		return workLeft;
	}

	public void setWorkLeft(Integer workLeft) {
		this.workLeft = workLeft;
	}
}

