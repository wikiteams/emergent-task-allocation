package load;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import collaboration.Utility.UtilityType;

public class ParametersDividerTest {

	ParametersDivider parametersDivider;

	@SuppressWarnings("serial")
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AgentCount.INSTANCE.setCounts(new HashSet<Integer>() {
			{
				add(50);
				add(120);
			}
		});
		GenerationLength.INSTANCE.setLengths(new HashSet<Integer>() {
			{
				add(20);
				add(100);
			}
		});
		ExpDecayOption.INSTANCE.setOptions(new HashSet<Boolean>() {
			{
				add(true);
				add(false);
			}
		});
	}

	@Before
	public void setUp() throws Exception {
		// parametersDivider = new ParametersDivider();
	}

	@SuppressWarnings("serial")
	@Test
	public void test() {
		assertEquals(2, AgentCount.INSTANCE.getCounts().size());
		assertEquals(2, GenerationLength.INSTANCE.getLengths().size());
		assertEquals("Count of permutations", Integer.valueOf(8),
				ParametersDivider.countSettings());
		// fail("Not yet implemented");
		assertEquals(2, AgentCount.INSTANCE.getCounts().size());
		assertEquals(2, GenerationLength.INSTANCE.getLengths().size());
		FunctionSet.INSTANCE.setFunctions(new HashSet<UtilityType>() {
			{
				add(UtilityType.LearningSkills);
			}
		});
		assertEquals(1, FunctionSet.INSTANCE.getFunctions().size());
		assertEquals("Count of permutations", Integer.valueOf(8),
				ParametersDivider.countSettings());
	}

}
