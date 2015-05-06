package utils;

import load.FunctionSet;
import collaboration.Utility.UtilityType;

/**
 * Setting the utility function of the evolutionary mechanism
 * 
 * @author Oskar Jarczyk
 * @since 2.0.9
 * @version 2.0.11
 */
public class UtilityFactory {

	public static void setUtility(UtilityType chosen){
		FunctionSet.INSTANCE.setChosen(chosen);
	}

}
