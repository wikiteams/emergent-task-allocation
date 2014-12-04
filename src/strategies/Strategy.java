package strategies;

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
		 * also the agent’s skill. Agent’s experience in this skill plays here
		 * no role.
		 */
		PREFERENTIAL,
		/**
		 * *Homophily* is an algorithm for assigning tasks which are best
		 * adjusted to an agent, while choice of most different task from the
		 * agent’s skills is called *heterophily*. In other words, both of them
		 * use an algorithm for assigning tasks which are most (mis)matched to
		 * an agent
		 */
		HOMOPHYLY, HETEROPHYLY,
		/**
		 * *Central strategy* - We also call it a ’strategy of central
		 * assignment’, or a ’central planner’ or ’central task allocation’
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
		 * Dla kazdego Sn pracuj rowno po czesci 1/n jezeli parametr allowRookie
		 * wlaczony, to omijaj intersekcje i pracuj nad wszystkim w danym tasku
		 */
		PROPORTIONAL_TIME_DIVISION,
		/**
		 * Pracuj dla wybranego Sn. Jezeli postepy puste w kazdym ze skilli w
		 * tasku to wybierz losowy. W przeciwnym razie pracuj tylko nad tym
		 * taskiem, ktory jest najbardziej zaczety (najmniej mu do zamkniecia)
		 */
		GREEDY_ASSIGNMENT,
		/**
		 * Pracuj wylacznie nad tym skillem, w ktory agent ma najwiecej
		 * doswiadczenia
		 */
		CHOICE_OF_AGENT,
		/**
		 * Pracuj zawsze nad losowo wybranym skillem
		 */
		RANDOM
	}

	@Override
	public String toString() {
		return this.taskChoice.name() + "," + this.skillChoice.name();
	}

}
