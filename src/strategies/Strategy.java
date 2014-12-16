package strategies;

import java.util.LinkedList;

/***
 * Types of Strategies used in the simulation
 * 
 * Strategy for Agent {strategy for choosing tasks}
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 * @version 2.0.6
 * 
 */
public class Strategy {

	/**
	 * This value is used to automatically generate agent identifiers.
	 * 
	 * The 8086 ("eighty-eighty-six", also called iAPX 86) is a 16-bit
	 * microprocessor chip designed by Intel between early 1976 and mid-1978,
	 * when it was released. The Intel 8088, released in 1979, was a slightly
	 * modified chip with an external 8-bit data bus (allowing the use of
	 * cheaper and fewer supporting ICs), and is notable as the processor used
	 * in the original IBM PC design, including the widespread version called
	 * IBM PC XT.
	 * 
	 * @field serialVersionUID
	 */
	public static final long serialVersionUID = 8086L;

	public TaskChoice taskChoice;
	public SkillChoice skillChoice;

	public Strategy(TaskChoice taskChoice, SkillChoice skillChoice) {
		this.taskChoice = taskChoice;
		this.skillChoice = skillChoice;
	}

	public static Strategy getInstance(StrategyDistribution strategyDistribution,
			Integer i, Integer max) {
		StrategySet set = strategyDistribution.getStrategySet();
		LinkedList<StrategyFrequency> elements = 
				(LinkedList<StrategyFrequency>) set.getStrategies().clone();
		while (true) {
			StrategyFrequency frequency = elements.poll();
			if (( ((double)i) / ((double)max) ) <= frequency.getDistribution()) {
				return new Strategy(frequency.getTaskChoice(),
						strategyDistribution.getSkillStrategy());
			} else {
				// do nothing
			}
		}
	}

	public enum TaskChoice {
		/**
		 * *Random choice* - for evaluating results of other strategies. Agents
		 * are choosing completely randomly a task from the pool of unfinished
		 * tasks in simulation.
		 */
		RANDOM,
		/**
		 * *Preferential strategy* searches for the most advanced task in the
		 * simulation. It is calculated by average of work done within all
		 * skills inside a task. Such values is used to sort all tasks by their
		 * general advancement decreasing. General advancement for a task is
		 * calculated by an average, where a single value is an advancement in a
		 * skill in percentage. Preferential strategy works by letting agent
		 * choose a most advanced task that has at least one skill, which is
		 * also the agent's skill. Agent's experience in this skill plays here
		 * no role.
		 */
		PREFERENTIAL,
		/**
		 * *Homophily* is an algorithm for assigning tasks which are best
		 * adjusted to an agent, while choice of most different task from the
		 * agent's skills is called *heterophily*. In other words, both of them
		 * use an algorithm for assigning tasks which are most (mis)matched to
		 * an agent
		 */
		HOMOPHYLY, HETEROPHYLY,
		/**
		 * *Central strategy* - We also call it a 'strategy of central
		 * assignment', or a 'central planner' or 'central task allocation'
		 * strategy. Algorithm performs a multiple sorting operation.
		 * Simplifying the idea, it works by choosing a task with the least
		 * advanced skill inside and assign it to an agent which have highest
		 * experience in it. Before launching an iteration of the simulation,
		 * every agent has a task assigned to him or her (unless the number of
		 * tasks is smaller than the number of agents, then we can simply say
		 * that every task have an agent assigned to it).
		 */
		CENTRAL_ASSIGNMENT
	}

	public enum SkillChoice {
		/**
		 * Proportional module allows to work equally on multiple skills by
		 * diving time into particles, which makes for incrementing work done
		 * and experience by fractions of 1.
		 */
		PROPORTIONAL_TIME_DIVISION,
		/**
		 * Most advanced method: this module selects a single most done skill
		 * inside a task. If there is no such skill (i.e. all skills have 'work
		 * done' at zero point), then it select randomly a single skill.
		 */
		GREEDY_ASSIGNMENT,
		/**
		 * Greatest experience method: method selects only one skill in which an
		 * agent is most experienced. If the agent is equally well-experienced
		 * in more than one skill, then selects randomly one skill from this.
		 * set.
		 */
		CHOICE_OF_AGENT,
		/**
		 * Random method: this module selects one single random skill to work
		 * on. Firstly, the strategy evaluates the intersection of agents skills
		 * and tasks required skills. If this intersection is empty, the
		 * strategy continues to choose between any random skill left.
		 */
		RANDOM
	}

	@Override
	public String toString() {
		return this.taskChoice.name() + "," + this.skillChoice.name();
	}

	public Strategy copy() {
		return new Strategy(this.taskChoice, this.skillChoice);
	}

	public void copyStrategy(Strategy copyFrom) {
		this.taskChoice = copyFrom.taskChoice;
		this.skillChoice = copyFrom.skillChoice;
	}

}
