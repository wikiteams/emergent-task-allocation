package strategies;

import collaboration.Task;
import collaboration.TaskInternals;
import collaboration.WorkUnit;

public class ProportionalTimeDivision extends EmergenceStrategy {

	public void increment(Task task, TaskInternals singleTaskInternal, int n,
			double alpha, double experience) {
		WorkUnit workDone = singleTaskInternal.getWorkDone();
		workDone.increment(n * alpha * experience);
		doAftearmath(task, singleTaskInternal);
	}

	@Override
	protected void doAftearmath(Task task, TaskInternals singleTaskInternal) {
		if (singleTaskInternal.isWorkDone()) {
			System.out.println("Work in [TaskInternal]:" + singleTaskInternal
					+ " is done.");
			task.removeSkill(singleTaskInternal.getSkill().getName());
		}
	}

}
