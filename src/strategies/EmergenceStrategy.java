package strategies;

import logger.VerboseLogger;
import collaboration.Task;
import collaboration.TaskInternals;

public abstract class EmergenceStrategy {
	
	protected abstract void doAftearmath(Task task, TaskInternals singleTaskInternal);
	
	protected void say(String s) {
		VerboseLogger.say(s);
	}

	protected void sanity(String s) {
		VerboseLogger.sanity(s);
	}

}
