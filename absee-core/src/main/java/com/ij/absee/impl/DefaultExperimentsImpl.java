package com.ij.absee.impl;

import java.util.HashSet;
import java.util.Set;

import com.ij.absee.Experiments;
import com.ij.absee.dao.ExperimentDao;
import com.ij.absee.dao.ExperimentDataStore;
import com.ij.absee.domain.Alternatives;
import com.ij.absee.domain.Experiment;

public class DefaultExperimentsImpl implements Experiments {

	private ExperimentDataStore dataStore;
	private final ExperimentDao experimentDao;
	
	public DefaultExperimentsImpl(final ExperimentDataStore dataStore, final ExperimentDao experimentDao) {
		this.dataStore = dataStore;
		this.experimentDao = experimentDao;
	}
	
	public boolean testEnded(final String testName) {
		return dataStore.hasTestEnded(testName);
	}
	
	public String alternativeThatWon(final String testName) {
		return dataStore.getAlternativeThatWon(testName);
	}
	
	public boolean exists(String testName) {
		boolean existsInCache = dataStore.testExists(testName);
		if (existsInCache) {
			return true;
		}
		experimentDao.beginTransaction();
		boolean exists = experimentDao.testExists(testName);
		experimentDao.rollback();
		return exists;
	}
	
	public void startExperiment(String testName, Alternatives alternatives, final String conversionName) {
		String conversionToUse = testName;
		if (conversionName != null) {
			conversionToUse = conversionName;
		}
		
		experimentDao.beginTransaction();
		Experiment experiment = experimentDao.findOrCreateByTestName(testName);
		experiment.start();
		experiment.removeAlternatives();
		experiment.setAlternatives(alternatives);
		experimentDao.update(experiment);
		experimentDao.commit();
		
		dataStore.newTestCreated(testName);
		Set<String> testsListeningToConversion = dataStore.getTestsListening(conversionToUse);
		if (testsListeningToConversion == null) {
			testsListeningToConversion = new HashSet<String>();
		}
		if (!testsListeningToConversion.contains(testName)) {
			testsListeningToConversion.add(testName);
			dataStore.setTestsListening(conversionToUse, testsListeningToConversion);
		}
		dataStore.setAlternatives(testName, alternatives);

	}
	
	public void endExperiment(final String testName, final String finalAlternative) {
		experimentDao.beginTransaction();
		Experiment experiment = experimentDao.findOrCreateByTestName(testName);
		experiment.finish();
		dataStore.testFinished(testName, finalAlternative);
		experimentDao.commit();
	}
	
	public String findAlternativeForUser(final String testName, final String identity) {
		Alternatives alternatives = dataStore.getAlternatives(testName);
		return alternatives.selectForUser(testName, identity);
	}
	
	
	public void recordParticipation(final String testName, final String identity) {
		
		Set<String> participatingTests = dataStore.getParticipatingTests(identity);
		if (participatingTests == null) {
			participatingTests = new HashSet<String>();
		}
		
		if (!participatingTests.contains(testName)) {
			participatingTests.add(testName);
			dataStore.setParticipatingTests(identity, participatingTests);
			experimentDao.beginTransaction();
			experimentDao.incrementParticipation(testName, findAlternativeForUser(testName, identity));
			experimentDao.commit();
		}
	}
	
	public void recordConversion(String conversionName, String identity) {
		Set<String> testsListeningToConversion = dataStore.getTestsListening(conversionName);

		if (testsListeningToConversion == null || testsListeningToConversion.isEmpty()) {
			// conversion based on test name
			scoreConversion(conversionName, identity);
		} else {
			for (String conversion : testsListeningToConversion) {
				scoreConversion(conversion, identity);
			}
		}
	}

	private void scoreConversion(String testName, String identity) {
		Set<String> participatingTests = dataStore.getParticipatingTests(identity);
		if (participatingTests != null && participatingTests.contains(testName)) {
			if (!dataStore.hasConverted(identity, testName)) {
				dataStore.recordUserConversion(identity, testName);
				experimentDao.beginTransaction();
				experimentDao.incrementConversion(testName, findAlternativeForUser(testName, identity));
				experimentDao.commit();				
			}
		}
	}

	// For testing only
	public boolean participatingInTest(final String identity, final String testName) {
		return dataStore.getParticipatingTests(identity).contains(testName);
	}
	
	// For testing only
	public boolean convertedInTest(final String identity, final String testName) {
		return dataStore.hasConverted(identity, testName);
	}
}
