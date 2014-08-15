package argonauts;

import collaboration.Task;
import collaboration.TaskInternals;

/**
 * Holds a reference to the last Task and TaskInternal
 * Agent worked on during previous tick
 * 
 * @author Oskar Jarczyk
 * @since 1.4
 *
 */
public class GranulatedChoice {

	private int howManyTimes;
	private Task taskChosen;
	private TaskInternals skillChosen;

	public GranulatedChoice() {
		taskChosen = null;
		skillChosen = null;
	}

	public GranulatedChoice(Task taskChosen, TaskInternals skillChosen) {
		this.taskChosen = taskChosen;
		this.skillChosen = skillChosen;
	}

	public GranulatedChoice(Task taskChosen, TaskInternals skillChosen, int c) {
		this.taskChosen = taskChosen;
		this.skillChosen = skillChosen;
		this.howManyTimes = c;
	}

	public int getHowManyTimes() {
		return howManyTimes;
	}

	public void setHowManyTimes(int howManyTimes) {
		this.howManyTimes = howManyTimes;
	}

	public Task getTaskChosen() {
		return taskChosen;
	}

	public void setTaskChosen(Task taskChosen) {
		this.taskChosen = taskChosen;
	}

	public TaskInternals getSkillChosen() {
		return skillChosen;
	}

	public void setSkillChosen(TaskInternals skillChosen) {
		this.skillChosen = skillChosen;
	}

	public void incrementHowManyTimes(int i) {
		this.howManyTimes ++ ;
	}

}
