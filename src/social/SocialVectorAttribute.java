package social;

public class SocialVectorAttribute {
	private boolean b;
	private double c;
	private int d;
	private String type;
	private double w;

	/**
	 * define a new attribute with a boolean value
	 * */
	public SocialVectorAttribute(boolean v, double f) {
		this.b = v;
		this.w = f;
		this.type = "boolean";
	}

	/**
	 * define a new attribute with a continuous value
	 * */
	public SocialVectorAttribute(double v, double f) {
		this.c = v;
		this.w = f;
		this.type = "double";
	}

	/**
	 * define a new attribute with a discrete value
	 * */
	public SocialVectorAttribute(int v, double f) {
		this.d = v;
		this.w = f;
		this.type = "int";
	}

	/**
	 * get the type of vector
	 * */
	public String getType() {
		return this.type;
	}

	/**
	 * get the value of attribute
	 * */
	public double getValue() {
		if (this.type.equals("boolean")) {
			if (this.b) {
				return 1;
			} else {
				return 0;
			}
		} else if (this.type.equals("double")) {
			return this.c;
		} else if (this.type.equals("int")) {
			return this.d;
		}
		return 0;
	}

	/**
	 * get the weight of vector
	 * */
	public double getWeight() {
		return this.w;
	}
}
