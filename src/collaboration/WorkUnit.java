package collaboration;

/**
 * 
 * Represents a time bit during which agent spends time on work
 * 
 * @version 1.1
 * @since 1.0
 * @author Oskar Jarczyk
 *
 */
public class WorkUnit {

	public int factor;
	public double d;

	public WorkUnit(double d, int factor) {
		this.d = d;
		this.factor = factor;
	}
	
	public WorkUnit(double d) {
		this.d = d;
		this.factor = 1;
	}
	
	public void increment(double how_much){
		this.d += how_much;
	}

}
