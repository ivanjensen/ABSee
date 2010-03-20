package com.ij.absee.dao;

import java.util.Set;

import com.ij.absee.domain.Alternatives;

public interface ExperimentDataStore {

	Alternatives getAlternatives(String cacheKey);

	void setAlternatives(String alternativesCacheKey, Alternatives alternatives);

	public void flush();

	Set<String> getParticipatingTests(String participatingCacheKey);

	void setParticipatingTests(String identity, Set<String> participatingTests);

	Set<String> getTestsListening(String conversionName);

	void setTestsListening(String conversionToUse, Set<String> testsListeningToConversion);

	String getAlternativeThatWon(String testName);

	void newTestCreated(String testName);

	void testFinished(String testName, String finalAlternative);

	boolean hasConverted(String identity, String testName);

	void recordUserConversion(String identity, String testName);

	boolean testExists(String testName);

	boolean hasTestEnded(String testName);

}
