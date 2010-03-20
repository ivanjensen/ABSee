package com.ij.absee.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.ij.absee.Experiments;
import com.ij.absee.dao.ExperimentDao;
import com.ij.absee.dao.impl.JpaExperimentDao;
import com.ij.absee.domain.impl.StringArrayAlternatives;

public class DefaultExperimentsImplTest {

	private Experiments experiments;
	private ExperimentDao experimentDao;


	@Before
	public void setUp() {
		experimentDao = new JpaExperimentDao();
		experiments = new DefaultExperimentsImpl(new HashMapExperimentDataStore(), experimentDao);
	}
	
	@Test
	public void testExists() {
		assertFalse(experiments.exists("test"));
		experiments.startExperiment("test", new StringArrayAlternatives(new String [] {"alt1", "alt2"}), null);
		assertTrue(experiments.exists("test"));
	}
	

	@Test
	public void testEndExperiment() {
		experiments.startExperiment("test", new StringArrayAlternatives(new String [] {"alt1", "alt2"}), null);
		experiments.endExperiment("test", "alt2");
		assertTrue(experiments.testEnded("test"));
		assertEquals("alt2", experiments.alternativeThatWon("test"));
		experimentDao.beginTransaction();
		assertEquals("Ended", experimentDao.findOrCreateByTestName("test").getStatus());
		experimentDao.rollback();
	}
	
	@Test
	public void testRecordingParticipationInTest() {
		experiments.startExperiment("test", new StringArrayAlternatives(new String [] {"alt1", "alt2"}), null);
		experiments.recordParticipation("test", "myidentity");
		assertTrue(((DefaultExperimentsImpl) experiments).participatingInTest("myidentity", "test"));
		experimentDao.beginTransaction();
		assertEquals(1, experimentDao.findOrCreateByTestName("test").getAlternatives().get(1).getParticipantCount());
		experimentDao.rollback();
	}
	
	@Test
	public void testRecordConversionAfterParticipation() {
		experiments.startExperiment("test", new StringArrayAlternatives(new String [] {"alt1", "alt2"}), null);
		experiments.recordParticipation("test", "myidentity");
		experiments.recordConversion("test", "myidentity");
		assertTrue(((DefaultExperimentsImpl) experiments).convertedInTest("myidentity", "test"));
		experimentDao.beginTransaction();
		assertEquals(1, experimentDao.findOrCreateByTestName("test").getAlternatives().get(1).getConversionCount());
		experimentDao.rollback();
	}
	
	@Test
	public void testRecordConversionWithNoParticipation() {
		experiments.startExperiment("test", new StringArrayAlternatives(new String [] {"alt1", "alt2"}), null);
		experiments.recordConversion("test", "myidentity");
		assertFalse(((DefaultExperimentsImpl) experiments).convertedInTest("myidentity", "test"));
		experimentDao.beginTransaction();
		assertEquals(0, experimentDao.findOrCreateByTestName("test").getAlternatives().get(1).getParticipantCount());
		assertEquals(0, experimentDao.findOrCreateByTestName("test").getAlternatives().get(1).getConversionCount());
		experimentDao.rollback();
	}

	@Test
	public void multipleTestsSharingAConversion() {
		experiments.startExperiment("purchase", new StringArrayAlternatives(new String [] {"alt1", "alt2"}), null);
		experiments.startExperiment("second test", new StringArrayAlternatives(new String [] {"alt1", "alt2"}), "purchase");
		experiments.startExperiment("third test", new StringArrayAlternatives(new String [] {"alt1", "alt2"}), "purchase");
		experiments.startExperiment("abandon", new StringArrayAlternatives(new String [] {"alt1", "alt2"}), "abandon");
		
		experiments.recordParticipation("purchase", "myidentity");
		experiments.recordParticipation("second test", "myidentity");
		// No participation in "third test"
		experiments.recordParticipation("abandon", "myidentity");

		// Convert in shared conversion
		experiments.recordConversion("purchase", "myidentity");
		
		assertTrue(((DefaultExperimentsImpl) experiments).convertedInTest("myidentity", "purchase"));		
		assertTrue(((DefaultExperimentsImpl) experiments).convertedInTest("myidentity", "second test"));		
		assertFalse(((DefaultExperimentsImpl) experiments).convertedInTest("myidentity", "third test"));		
		assertFalse(((DefaultExperimentsImpl) experiments).convertedInTest("myidentity", "abandon"));		
	}
	
}
