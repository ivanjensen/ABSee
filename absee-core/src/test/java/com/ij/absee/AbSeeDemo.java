package com.ij.absee;

import java.util.Random;

import com.ij.absee.dao.ExperimentDao;
import com.ij.absee.dao.ExperimentDataStore;
import com.ij.absee.dao.impl.JpaExperimentDao;
import com.ij.absee.domain.Alternative;
import com.ij.absee.domain.Experiment;
import com.ij.absee.domain.ExperimentResults;
import com.ij.absee.impl.DefaultExperimentsImpl;
import com.ij.absee.impl.MemcachedExperimentDataStore;
import com.ij.absee.util.Stats;


/**
 * Simple demo to show how ABSee works.
 * Set up an A/B test, run it and show the results.
 */
public class AbSeeDemo {

	public static void main(String [] args) {
		ExperimentDao experimentDao = new JpaExperimentDao();
//		ExperimentDataStore cache = new RedisABSeeCache();
		ExperimentDataStore cache = new MemcachedExperimentDataStore();
//		ExperimentDataStore cache = new HashMapABSeeCache();
		cache.flush(); // Clear down from previous runs.
		Experiments experiments = new DefaultExperimentsImpl(cache, experimentDao);
		RandomIdentitySource randomIdentitySource = new RandomIdentitySource();
		ABSee absee = new ABSee(experiments, randomIdentitySource);

		String testName = "red or blue checkout button";
		
		Random rand = new Random(System.currentTimeMillis());
		for (int x = 0; x < 2000; x++) {
			//absee.test("red or blue checkout button", new String [] {"red", "blue"}, null);
			//absee.test(testName, 10, null);
			String alternative = absee.test(testName, "'bob' => 0.9, 'sam' => 0.1", null);
			int randomNum = rand.nextInt(10);
			if (randomNum < 6 || (randomNum < 7 && alternative.equals("sam"))) {
				absee.conversion(testName);
			}
			randomIdentitySource.next();
		}
		
		experimentDao.beginTransaction();
		Experiment experiment = experimentDao.findOrCreateByTestName(testName);
		System.out.println(experiment.getName());
		System.out.println("Best Alternative:" + experiment.bestAlternative().getContent());
		ExperimentResults results = experiment.results();
		Stats bestStats = results.getStats().get(experiment.bestAlternative().getContent()); 
		if (bestStats != null) {
			if (bestStats.getValue() <= 0.05) {
				System.out.println(String.format("Statistically significant with a confidence level of %s (%s)", bestStats.getPercentage(), bestStats.getDescription()));
			} else {
				System.out.println("Not statistically significant, suggest continuing testing.");
			}
		}
		System.out.println("Alternative breakdown:");
		boolean control = true;
		for (Alternative alternative : experiment.getAlternatives()) {
			Stats stat = results.getStats().get(alternative);
			if (control) {
				control = false;
				stat = Stats.NOT_APPLICABLE;
			}
			System.out.println(String.format("   %s[%.2f]: p/c/r/confidence %s/%s/%.2f%%/%s(%s)", 
					alternative.getContent(), alternative.getWeight(), 
					alternative.getParticipantCount(), alternative.getConversionCount(), 
					alternative.conversionRate() * 100,
					stat.getPercentage(),
					stat.getDescription()));
		}
		System.out.println("Total Participants: " + experiment.participantCount());
		System.out.println("Total Conversions: " + experiment.conversionCount());

		
		experimentDao.rollback();
	}
		
	/**
	 * Produce random identities for tests - should never be consistent from call to call.
	 */
	private static class RandomIdentitySource implements IdentitySource {
		private Random rand = new Random(System.currentTimeMillis() + System.nanoTime());
		private String currentIdentity;
		
		public String get() {
			if (currentIdentity == null) {
				next();
			}
			return currentIdentity;
		}
		
		public void next() {
			currentIdentity = String.valueOf(rand.nextInt());
		}
	}

	
}
