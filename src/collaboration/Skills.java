package collaboration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import repast.simphony.context.Context;
import repast.simphony.context.DefaultContext;

/***
 * Programming Task - a producer in the simulation
 * 
 * @author Oskar Jarczyk
 * @since 2.0
 * @version 3.0
 */
public class Skills extends DefaultContext<Skill> {
	
	private Set<Skill> listSkills;
	private Map<Skill, List<TaskInternals>> skillsUsed;

	public Skills() {
		super("Skills");
		skillsUsed = new HashMap<Skill, List<TaskInternals>>();
		initializeSkills(this);
	}
	
	private void initializeSkills(Context<Skill> context) {
		addSkills(context);
	}
	
	private void addSkills(Context<Skill> context) {
		// initialize skill pools - information on all known languages
		try {
			listSkills = SkillFactory.getInstance().buildSkillsLibrary();
			for(Skill skill: listSkills) {
				context.add(skill);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Skill getSkill(String name) {
		for (Skill skill : listSkills) {
			if (skill.getName().toLowerCase().equals(name.toLowerCase())) {
				return skill;
			}
		}
		return null;
	}
	
	public int countAllSkills(){
		return listSkills.size();
	}

	public Set<Skill> getListSkills() {
		return listSkills;
	}

	public void setListSkills(Set<Skill> listSkills) {
		this.listSkills = listSkills;
	}

	public Map<Skill, List<TaskInternals>> getSkillsUsed() {
		return skillsUsed;
	}

	public void setSkillsUsed(Map<Skill, List<TaskInternals>> skillsUsed) {
		this.skillsUsed = skillsUsed;
	}

	public void addTaskInternals(TaskInternals taskInternals) {
		Skill reported = taskInternals.getSkill();
		if(skillsUsed.containsKey(reported)) {
			skillsUsed.get(reported).add(taskInternals);
		} else {
			List<TaskInternals> taskInternalsList = new ArrayList<TaskInternals>();
			taskInternalsList.add(taskInternals);
			skillsUsed.put(reported, taskInternalsList);
		}
	}

}
