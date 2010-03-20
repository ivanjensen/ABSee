package com.ij.absee;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.ij.absee.dao.ExperimentDao;
import com.ij.absee.dao.impl.JpaExperimentDao;
import com.ij.absee.impl.DefaultExperimentsImpl;
import com.ij.absee.impl.HashMapExperimentDataStore;


public class ABSeeTest {

	private ExperimentDao experimentDao;
	private Experiments experiments;

	@Before
	public void setUp() {
		experimentDao = new JpaExperimentDao();
		experiments = new DefaultExperimentsImpl(new HashMapExperimentDataStore(), experimentDao);
	}
	
	@Test
	public void endedTestReturnsCanonical() {
		IdentitySource identitySource = new IdentitySource() {
			public String get() {
				return "";
			}
		};
		ABSee absee = new ABSee(experiments, identitySource);
		String selectedAlternative = absee.test("test1", new String [] {"alt1", "alt2"}, null);
		assertEquals("alt1", selectedAlternative);
		
		absee.endTest("test1", "alt2");
		assertEquals("Stopped test should return winning test", "alt2", absee.test("test1", new String [] {"alt1", "alt2"}, null));
	}

	@Test
	public void sameChoiceSelectedForUserConsistently() {
		IdentitySource identitySource = new IdentitySource() {
			public String get() {
				return "";
			}
		};
		
		ABSee absee = new ABSee(experiments, identitySource);
		String selectedAlternative = absee.test("test1", new String [] {"alt1", "alt2"}, null);
		for (int x = 1; x < 20; x++) {
			assertEquals(selectedAlternative, absee.test("test1", new String [] {"alt1", "alt2"}, null));
		}
	}

	
	/**
	 * A (somewhat iffy) test that checks the <code>flip</code> is suitably 'fair'.
	 */
	@Test
	public void flipBehavesAsSomewhatFair() {
		final int [] x = new int [1];
		IdentitySource identitySource = new IdentitySource() {
			public String get() {
				return String.valueOf(x[0]);
			}
		};
		
		ABSee absee = new ABSee(experiments, identitySource);
		int [] flips = new int[2];
		
		for (x[0] = 0; x[0] < 200; x[0]++) {
			flips[absee.flip("test1") ? 0 : 1]++;
		}
		assertEquals(100, flips[0], 5);
		assertEquals(100, flips[1], 5);
	}
	
}
