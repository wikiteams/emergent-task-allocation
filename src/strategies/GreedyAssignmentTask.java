package strategies;

import collaboration.SimulationParameters;
import collaboration.Task;
import collaboration.TaskInternals;
import collaboration.WorkUnit;

public class GreedyAssignmentTask extends EmergenceStrategy {

	public void increment(Task task, TaskInternals singleTaskInternal, int n,
			double experience) {
		WorkUnit workDone = singleTaskInternal.getWorkDone();
		workDone.increment(n * experience);
		doAftearmath(task, singleTaskInternal);
	}

	@Override
	protected void doAftearmath(Task task, TaskInternals singleTaskInternal) {
		if (SimulationParameters.deployedTaskInternalsLeave) {
			if (singleTaskInternal.isWorkDone()) {
				super.say("Work in [TaskInternal]:" + singleTaskInternal
						+ " is done.");
				task.removeSkill(singleTaskInternal.getSkill().getName());
			}
		}
	}

}
