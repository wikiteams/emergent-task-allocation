package collaboration;

import github.AgentModeling;
import intelligence.EquilibriumDetector;
import utils.NamesGenerator;
import argonauts.PersistJobDone;
import argonauts.PersistRewiring;

public enum Preprocess {
	
	INSTANCE;
	
	public static void clearStaticHeap() {
		System.out.println("Clearing [static data] from previous simulation");
		System.out.println("Hence despite the fact there is a seperate JVM "
				+ "for every instance, a new run need to reset static fields in classes");
		PersistJobDone.clear();
		PersistRewiring.clear();
		SkillFactory.skills.clear();
		NamesGenerator.clear();
		Tasks.clearTasks();
		Agent.totalAgents = 0;
		AgentSkillsFrequency.clear();
		AgentModeling.clear();
		EquilibriumDetector.clear();
	}

}
