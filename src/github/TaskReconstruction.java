package github;

import collaboration.CollaborationBuilder;
import collaboration.Skill;
import collaboration.Skills;
import collaboration.Task;
import collaboration.TaskInternals;
import collaboration.WorkUnit;

public class TaskReconstruction {

	public static void giveWork(Task task, String skillName,
			Integer workDoneInt, Integer workRequiredInt) {
		try {
			Skill skill = ((Skills) CollaborationBuilder.skills).getSkill(skillName);
			
			assert skill != null;

			WorkUnit workDone = new WorkUnit(workDoneInt);
			WorkUnit workRequired = new WorkUnit(workRequiredInt);
			
			assert workDoneInt <= workRequiredInt;

			TaskInternals taskInternals = new TaskInternals(skill,
					workRequired, workDone, task);
			task.addSkill(skill.getName(), taskInternals);
		} catch (NullPointerException nexc) {
			System.out.println("Problematic [Skill] name: " + skillName);
			System.out.println("Please verify it is present in all-languages.csv");
			throw nexc;
		}
	}
}
