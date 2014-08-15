package github;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.4.1
 * @author Oskar Jarczyk
 * @since 1.4.1
 *
 */
public abstract class TaskSkillFrequency {

    public static final Map<String, Double> frequency;
    static
    {
    	frequency = new HashMap<String, Double>();
    	frequency.put("SMALL", 10d);
    	frequency.put("MEDIUM", 60d);
    	frequency.put("HIGH", 250d);
    	frequency.put("ENORMOUS", 2125d);
    }
    
    public static final double MAX = 2124;
    
    public static BigInteger tasksCheckSum;
    
    public static void clear(){
    	tasksCheckSum = BigInteger.ZERO;
    }

}
