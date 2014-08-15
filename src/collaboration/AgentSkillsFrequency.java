package collaboration;

import java.math.BigInteger;

/**
 * @since 1.4.1
 * @author Oskar
 * @version 1.4.1
 */
public abstract class AgentSkillsFrequency {

	public static BigInteger tasksCheckSum;

	public static void clear() {
		tasksCheckSum = BigInteger.ZERO;
	}

}
