package collaboration;

/**
 * Represents agent's representation of a skill and experience
 * 
 * @since 1.0
 * @author Oskar Jarczyk
 * @version 2.0.6
 */
public class AgentInternals {

	private Skill skill;
	private Experience experience;

	public AgentInternals(Skill skill, Experience experience) {
		this.skill = skill;
		this.experience = experience;
	}

	public Skill getSkill() {
		return skill;
	}

	public void setSkill(Skill skill) {
		this.skill = skill;
	}

	public Experience getExperience() {
		return experience;
	}

	public void decayExperience() {
		experience.decay();
	}

//	public Boolean decayExperienceWithDeath() {
//		return experience.decayWithDeath();
//	}

	public AgentInternals deepCopy() {
		return new AgentInternals(getSkill(), new Experience(getExperience()
				.getValue(), (int) getExperience().getTop()));
	}

}
