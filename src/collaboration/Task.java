package collaboration;

import repast.simphony.engine.watcher.Watch;
import repast.simphony.engine.watcher.WatcherTriggerSchedule;

public class Task {
	
	String __barebones = "MockSkill: Java ";
	int __bare_work_done = 66;
	int __bare_work_left = 100;
	
	@Watch(watcheeClassName = "collaboration.Task",
			watcheeFieldNames = "__bare_work_done",
			query = "colocated",
			whenToTrigger = WatcherTriggerSchedule.IMMEDIATE)
	public void run(){
		System.out.println("Task with skills running procedure");
		System.out.println(__barebones + __bare_work_done + "/" + __bare_work_left);
	}

}
