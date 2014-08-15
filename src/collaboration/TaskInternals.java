package collaboration;

import java.text.DecimalFormat;

public class TaskInternals {
	
	private Skill skill;
	private WorkUnit workRequired;
	private WorkUnit workDone;
	private Task owner;
	
	public TaskInternals(Skill skill, WorkUnit workRequired, WorkUnit workDone, Task owner){
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
	
	public boolean isWorkDone(){
		return (this.getWorkDone().d >= this.getWorkRequired().d);
	}
	
	/**
	 * Get advancement (work done between 0 and 1) of the TaskInternal.
	 * In rare cases the task can be overworked (returns > 100%)
	 * 
	 * @return Always returns a progress value between [0,1+
	 */
	public double getProgress(){
		return Math.abs(this.workDone.d / this.workRequired.d);
	}
	
	public double getWorkLeft(){
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
	public String toString(){
		DecimalFormat df = new DecimalFormat("#.######");
		return this.skill.getName() + " " + 
				df.format(workDone.d) + "/" + df.format(workRequired.d);
	}
}
