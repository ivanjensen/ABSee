/**
 * 
 */
package com.ij.absee.impl;


/**
 * Simple utility that provides safe cache key names for the data needed by ABSee
 * for certain datastores like Redis and Memcached.
 */
public class DefaultCacheKeyAdapter {
	public String convertedCacheKey(String identity, String testName) {
		return safe("ABSee::conversions(" + identity + "," + testName + ")");
	}

	public String participatingCacheKey(final String identity) {
		return safe("ABSee::participating_tests::" + identity);
	}

	public String testsListeningCacheKey(String conversionToUse) {
		return safe("ABSee::tests_listening_to_conversion" + conversionToUse);
	}

	public String testCompleteCacheKey(final String testName) {
		return safe("ABSee::Experiment::short_circuit(" + testName + ")");	
	}

	public String testExistsCacheKey(final String testName) {
		return safe("ABSee::Experiment::exists(" + testName + ")");
	}

	public String alternativesCacheKey(final String testName) {
		return safe("ABSee::Experiment::" + testName + "::alternatives");
	}
	
	private String safe(final String key) {
		return key.replaceAll(" ", "_");
	}
}