package collaboration;

import java.text.DecimalFormat;

/**
 * Represents a work on a particular Skill in a Task
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 * @version 2.0.3
 */
public class TaskInternals {

	private Skill skill;
	private WorkUnit workRequired;
	private WorkUnit workDone;
	private Task owner;

	public TaskInternals(Skill skill, WorkUnit workRequired, WorkUnit workDone,
			Task owner) {
		assert workRequired.d >= workDone.d;
		assert workRequired.d > 0.;
		this.skill = skill;
		this.workDone = workDone;
		this.workRequired = workRequired;
		this.owner = owner;
	}

	public Skill getSkill() {
		return skill;
	}

	public String getSkillName() {
		return skill.getName();
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	public Task getOwner() {
		return owner;
	}

	public void setOwner(Task owner) {
		this.owner = owner;
	}

	public WorkUnit getWorkRequired() {
		return workRequired;
	}

	public void setWorkRequired(WorkUnit workRequired) {
		this.workRequired = workRequired;
	}

	public WorkUnit getWorkDone() {
		return workDone;
	}

	public void setWorkDone(WorkUnit workDone) {
		this.workDone = workDone;
	}

	public boolean isWorkDone() {
		return (this.getWorkDone().d >= this.getWorkRequired().d);
	}

	/**
	 * Get advancement (work done between 0 and 1) of this Skill (TaskInternal).
	 * In rare cases the task can be overworked (returns > 100%)
	 * 
	 * @return double - always returns a progress value between [0,1]+c
	 */
	public double getProgress() {
		return Math.abs(this.workDone.d / this.workRequired.d);
	}

	/**
	 * Get a work left for this particular skill (TaskInternal) of a Task
	 * 
	 * @return double - work left, always a value between [0, 1]
	 */
	public double getWorkLeft() {
		return Math.abs(this.workRequired.d - this.workDone.d);
	}

	@Override
	public int hashCode() {
		return owner.hashCode() * skill.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if ((this.owner.equals(((TaskInternals) obj).owner))
				&& (this.skill == ((TaskInternals) obj).skill))
			return true;
		else
			return false;
	}

	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#.######");
		return this.skill.getName() + " " + df.format(workDone.d) + "/"
				+ df.format(workRequired.d);
	}
}
