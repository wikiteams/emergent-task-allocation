package collaboration;

import java.text.DecimalFormat;

import load.ParametrizedSigmoidOption;
import load.SigmoidParameter;

/**
 * Class describing the learning process of a human for simulation purposes. 
 * Uses a sigmoid approximation of a learning function
 * 
 * Allows for simulating experience decay
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 * @version 2.0.11
 * 
 */
public class Experience {

	private double value; // plain experience
	private int top; // mastership level

	private static final double expStub = 0.03; // Initial knowledge is set to 3%
	private static final double decayLevel = 0.0005; // 0,05%
	private static final double stupidityLevel = 0.03; // 3%
	
	private double learningParameter;

	private enum ApproximationMethod {
		SIGMOID, PARAMETRIZED_SIGMOID
	};

	public Experience() {
		this(0d, 0, false);
	}

	public Experience(boolean mastershipLevelRequired) {
		this(0d, 0, mastershipLevelRequired);
	}

	public Experience(double value, int top) {
		this(value, top, false);
	}

	public Experience(double value, int top, boolean mastershipLevelRequired) {
		if (mastershipLevelRequired) {
			int mastershipLevel = SimulationAdvancedParameters.agentSkillsMaximumExperience;
			this.value = mastershipLevel * expStub;
			this.top = mastershipLevel;
		} else {
			this.value = value;
			this.top = top;
		}
		this.learningParameter = SigmoidParameter.INSTANCE.getChosen();
	}

	public double getDelta() {
		if (ParametrizedSigmoidOption.INSTANCE.getChosen()) {
			return getDelta(ApproximationMethod.PARAMETRIZED_SIGMOID);
		} else {
			return getDelta(ApproximationMethod.SIGMOID);
		}
	}

	/***
	 * Decays experience by going back on sigmoid function Hence that despite
	 * the fact that only double this.value is modified, it does changes the
	 * result of sigmoid(this.value) that's why it is possible that we do
	 * decrease the experience by going back on sigmoid function
	 * 
	 * Experience is always used together with sigmoid(this.value)
	 * 
	 * @return double - a new value of experience
	 */
	public void decay() {
		double currentExperience = SigmoidCurve.getDelta(((this.value) / this.top));
		if (currentExperience > stupidityLevel) {
			double newValue = this.value - (this.value * decayLevel);
			double newExpereince = SigmoidCurve.getDelta(newValue/this.top);
			if (newExpereince <= stupidityLevel) {
				// never make less than 3%
				this.value = stupidityLevel * this.top;
			} else {
				this.value = newValue;
			}
		}
	}

//	public Boolean decayWithDeath() {
//		boolean dies = false;
//		double howMuch = this.top * decayLevel;
//		if (value - howMuch <= 0) {
//			dies = true;
//			this.value = 0;
//		} else {
//			this.value = value - howMuch;
//		}
//		return dies;
//	}

	public double getDelta(ApproximationMethod method) {
		switch (method) {
			case SIGMOID:
				return SigmoidCurve.getDelta((value / top) > 1. ? 1. : (value / top));
			case PARAMETRIZED_SIGMOID:
				return SigmoidCurve.getCustomDelta((value / top) > 1. ? 1. : (value / top), learningParameter);
			default:
				return SigmoidCurve.getDelta((value / top) > 1. ? 1. : (value / top)); // use standard sigmoid
		}
	}

	public double getValue() {
		return value;
	}

	public double getTop() {
		return top;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public void increment(double how_much) {
		this.value += how_much;
	}
	
	public void incrementAbsolutly(double how_much) {
		assert how_much >= 0.0;
		assert how_much <= 1.0;
		this.value += this.top * how_much;
	}

}