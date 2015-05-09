package collaboration;

import github.AgentModeling;
import intelligence.EquilibriumDetector;
import intelligence.ImpactFactor;
import logger.VerboseLogger;
import utils.NamesGenerator;
import argonauts.PersistJobDone;
import argonauts.PersistRewiring;

public enum Preprocess {
	
	INSTANCE;
	
	public static void clearStaticHeap() {
		VerboseLogger.say("Clearing [static data] from previous simulation");
		VerboseLogger.sanity("Hence despite the fact there is a seperate JVM "
				+ "for every instance, a new run need to reset static fields in classes");
		PersistJobDone.clear();
		PersistRewiring.clear();
		SkillFactory.skills.clear();
		NamesGenerator.clear();
		Tasks.clearTasks();
		Agent.totalAgents = 0;
		AgentSkillsFrequency.clear();
		AgentModeling.clear();
		ImpactFactor.clear();
		EquilibriumDetector.clear();
	}

}
