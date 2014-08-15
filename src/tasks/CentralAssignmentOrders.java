package tasks;

import collaboration.Task;
import collaboration.TaskInternals;

public class CentralAssignmentOrders {
	
	private Task chosenTask;
	private TaskInternals chosenSkill;
	
	public CentralAssignmentOrders(Task chosenTask, TaskInternals chosenSkill){
		this.chosenTask = chosenTask;
		this.chosenSkill = chosenSkill;
	}

	public TaskInternals getChosenSkill() {
		return this.chosenSkill;
	}
	
	public String getChosenSkillName() {
		return this.chosenSkill.getSkill().getName();
	}

	public void setChosenSkill(TaskInternals chosenSkill) {
		this.chosenSkill = chosenSkill;
	}
	
	public void cancelOrders(){
		this.chosenSkill = null;
		this.chosenTask = null;
	}

	public Task getChosenTask() {
		return this.chosenTask;
	}

	public void setChosenTask(Task chosenTask) {
		this.chosenTask = chosenTask;
	}
	
	@Override
	public int hashCode() {
		return this.chosenSkill.hashCode() * this.chosenTask.hashCode();
	}
	
	@Override
	public String toString(){
		return this.chosenTask.getName() + " " + this.chosenSkill;
	}

}
