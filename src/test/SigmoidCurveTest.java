package test;

import static org.junit.Assert.*;

import org.junit.Test;

import collaboration.SigmoidCurve;

public class SigmoidCurveTest {
	
	private double delta = 0.002472623156634775 + 0.00000001;

	@Test
	public void testMin() {
		assertEquals(0.0, SigmoidCurve.getDelta(0.0), delta);
	}
	
	@Test
	public void testMax() {
		assertEquals(1.0, SigmoidCurve.getDelta(1.0), delta);
	}
	
	@Test
	public void testMiddle() {
		assertEquals(0.5, SigmoidCurve.getDelta(0.5), 0.00000001);
	}
	
	@Test
	public void testOverLearning() {
		assertEquals(1.0, SigmoidCurve.getDelta(1.3), 0.00000001);
	}

}
