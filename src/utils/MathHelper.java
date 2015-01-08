package utils;

public class MathHelper {

	public static boolean isBetween(Double evaluated, double var1, double var2) {
		return var2 > var1 ? evaluated > var1 && evaluated < var2
				: evaluated > var2 && evaluated < var1;
	}
	
	public static boolean isBetweenInc(Double evaluated, double var1, double var2) {
		return var2 > var1 ? evaluated >= var1 && evaluated <= var2
				: evaluated >= var2 && evaluated <= var1;
	}

}
