package test;

import static org.junit.Assert.*;

import org.junit.Test;

import collaboration.SigmoidCurve;

public class SigmoidCurveTest {
	
	private double delta = 0.002472623156634775 + 0.00000001;

	@Test
	public void testMin() {
		SigmoidCurve sc = new SigmoidCurve();
		assertEquals(0.0, sc.getDelta(0.0), delta);
	}
	
	@Test
	public void testMax() {
		SigmoidCurve sc = new SigmoidCurve();
		assertEquals(1.0, sc.getDelta(1.0), delta);
	}
	
	@Test
	public void testMiddle() {
		SigmoidCurve sc = new SigmoidCurve();
		assertEquals(0.5, sc.getDelta(0.5), 0.00000001);
	}
	
	@Test
	public void testOverLearning() {
		SigmoidCurve sc = new SigmoidCurve();
		assertEquals(1.0, sc.getDelta(1.3), 0.00000001);
	}

}
