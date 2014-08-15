package tasks;

import collaboration.Agent;
import collaboration.Skill;
import collaboration.Task;

import java.util.Collection;
import java.util.Map;

import argonauts.PersistAdvancement;

public class Preferential {
	
	private static final double emptyResultSignal = -0.9;

	public Task concludeMath(Agent agent){
		Collection<Skill> allAgentSkillsPref = agent.getSkills();
		Task chosen = null;
		double adv = -1;
		for (Skill singleSkill : allAgentSkillsPref) {
			Object[] r = PersistAdvancement.getMostAdvanced(singleSkill);
			if (((Double) r[0]) < emptyResultSignal)
				continue;
			if (((Double) r[0]) > adv){
				chosen = (Task) r[1];
			}
		}
		return chosen;
	}

}
