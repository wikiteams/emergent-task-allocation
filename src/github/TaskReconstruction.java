package github;

import collaboration.Skill;
import collaboration.SkillFactory;
import collaboration.Task;
import collaboration.TaskInternals;
import collaboration.WorkUnit;

public class TaskReconstruction {

	public static void giveWork(Task task, String skillName,
			Integer workDoneInt, Integer workRequiredInt) {

		SkillFactory skillFactory = SkillFactory.getInstance();
		Skill skill = skillFactory.getSkill(skillName);

		WorkUnit workDone = new WorkUnit(workDoneInt);
		WorkUnit workRequired = new WorkUnit(workRequiredInt);

		TaskInternals taskInternals = new TaskInternals(skill, workRequired,
				workDone, task);
		task.addSkill(skill.getName(), taskInternals);

	}
}
