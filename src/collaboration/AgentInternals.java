package collaboration;

/**
 * Represents agent's representation of a skill and experience
 * 
 * @since 1.0
 * @author Oskar Jarczyk
 * @version 1.3
 */
public class AgentInternals {
	
	private Skill skill;
	private Experience experience;
	
	public AgentInternals(Skill skill, Experience experience){
		this.skill = skill;
		this.experience = experience;
	}
	
	public Skill getSkill() {
		return skill;
	}
	
	public void setSkill(Skill skill) {
		this.skill = skill;
	}
	
	public Experience getExperience(){
		return experience;
	}
	
	public double decayExperience(){
		return experience.decay();
	}
	
	public Boolean decayExperienceWithDeath(){
		return experience.decayWithDeath();
	}

}
