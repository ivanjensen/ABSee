package com.ij.absee.domain.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Map;

import org.junit.Test;

import com.ij.absee.domain.Alternative;
import com.ij.absee.domain.Alternatives;
import com.ij.absee.domain.Experiment;
import com.ij.absee.domain.ExperimentResults;
import com.ij.absee.util.Stats;

public class ExperimentImplTest {

	@Test
	public void testBestAlternative() {
		Alternatives alternatives = new StringArrayAlternatives(new String [] {"true", "false"});
		
		Experiment experiment = new ExperimentImpl();
		experiment.setAlternatives(alternatives);
		experiment.getAlternatives().get(0).incrementParticipants();  
		experiment.getAlternatives().get(0).incrementConversions();   // convert
		experiment.getAlternatives().get(1).incrementParticipants();  // don't convert
		assertEquals("true", experiment.bestAlternative().getContent());
	}
	
	
//	@Test
//	public void testZScore() {
//		Alternatives alternatives = new StringArrayAlternatives(new String [] {"true", "false"});
//		
//		Experiment experiment = new ExperimentImpl();
//		experiment.setAlternatives(alternatives);
//		experiment.getAlternatives().get(0).incrementParticipants();  
//		experiment.getAlternatives().get(0).incrementConversions();   // convert
//		experiment.getAlternatives().get(1).incrementParticipants();  // don't convert
//		assertEquals(Double.NEGATIVE_INFINITY, experiment.zScore(), 0.005);	
//	}

	@Test
	public void testZScore2() {
		Alternatives alternatives = new StringArrayAlternatives(new String [] {"control", "A", "B", "C"});
		
		/*
		Control 	182	35	19.23%	N/A
		Treatment A	180	45	25.00%	1.33
		Treatment B	189	28	14.81%	-1.13
		Treatment C	188	61	32.45%	2.94		
		*/
		
		ExperimentImpl experiment = new ExperimentImpl();
		experiment.setAlternatives(alternatives);
		addParticipantsAndConversions(experiment.getAlternatives().get(0), 182, 35); 
		addParticipantsAndConversions(experiment.getAlternatives().get(1), 180, 45);
		addParticipantsAndConversions(experiment.getAlternatives().get(2), 189, 28);
		addParticipantsAndConversions(experiment.getAlternatives().get(3), 188, 61);
		ExperimentResults results = experiment.results(); 
		assertEquals(1.33, results.getzScores().get(experiment.getAlternatives().get(1)), 0.005);
		assertSame(Stats.FAIRLY_CONFIDENT, results.getStats().get(experiment.getAlternatives().get(1)));
		assertEquals(-1.13, results.getzScores().get(experiment.getAlternatives().get(2)), 0.005);
		assertSame(Stats.NOT_CONFIDENT, results.getStats().get(experiment.getAlternatives().get(2)));
		assertEquals(2.94, results.getzScores().get(experiment.getAlternatives().get(3)), 0.005);
		assertSame(Stats.VERY_CONFIDENT, results.getStats().get(experiment.getAlternatives().get(3)));
		
	}
	
	private static long id = 0;
	private void addParticipantsAndConversions(Alternative alternative, int participants, int conversions) {
		((AlternativeImpl) alternative).id = id++; 
		for (int x = 0; x < participants; x++) {
			alternative.incrementParticipants();
		}
		for (int x = 0; x < conversions; x++) {
			alternative.incrementConversions();
		}
	}
}
