package constants;

/**
 * 
 * Just often used message constraints
 * 
 * @since 1.1
 * @author Oskar
 *
 */
public abstract class Constraints {
	
	public static final String EMPTY_STRING = "";

	public static final String LOGGER_INITIALIZED = "PjiitLogger initialized";
	public static final String VALIDATION_LOGGER_INITIALIZED = "Validation logger initialized";
	public static final String UNKNOWN_EXCEPTION = "Unknown exception";
	public static final String IO_EXCEPTION = "Input/output exception";
	public static final String LOADING_PARAMETERS = "Loading parameters";
	public static final String ERROR_INITIALIZING_PJIITLOGGER = 
			"Error initializing PjiitLogger!";
	public static final String ERROR_INITIALIZING_PJIITLOGGER_AO_PARAMETERS = 
			"Error initializing PjiitLogger and/or Simulation Parameters!";
	public static final String INSIDE_PROPORTIONAL_TIME_DIVISION = 
			"Inside switch - PROPORTIONAL_TIME_DIVISION";
	public static final String INSIDE_GREEDY_ASSIGNMENT_BY_TASK = 
			"Inside switch - GREEDY_ASSIGNMENT_BY_TASK";
	public static final String INSIDE_RANDOM = 
			"Inside switch - RANDOM";
	public static final String INSIDE_CHOICE_OF_AGENT = 
			"Inside switch - CHOICE_OF_AGENT";
	public static final String DIDNT_FOUND_TASK = 
			"Didn't found task with such skills and/or experience which agent have!";
	public static final String DIDNT_FOUND_TASK_W_SUCH_SKILLS = 
			"Didn't found task with such skills which agent have!";
	public static final String DIDNT_FOUND_TASK_TO_WORK_ON = 
			"Didn't found task to work on!";
	public static final String ROOKIE = 
			"Choose random task left! Agents are in a desire of job even if they rookie!";

}
