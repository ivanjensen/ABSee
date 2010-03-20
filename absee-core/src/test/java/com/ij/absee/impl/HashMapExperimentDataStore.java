package com.ij.absee.impl;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ij.absee.domain.Alternatives;

/**
 * A simple ExperimentDataStore for testing - not for use in production as data will not be 
 * persisted! 
 */
@SuppressWarnings("unchecked")
public class HashMapExperimentDataStore extends AbstractKeyValueExperimentDataStore {

	private Map<String, Object> cache = new ConcurrentHashMap<String, Object>(); 
	
	public boolean containsKey(String key) {
		return cache.containsKey(key);
	}

	public String get(String key) {
		return (String) cache.get(key);
	}

	public void put(String key, String value) {
		cache.put(key, value);
	}

	public void flush() {
		cache.clear();
	}

	public Alternatives getAlternatives(String testName) {
		return (Alternatives) cache.get(cacheKeyAdapter.alternativesCacheKey(testName));
	}

	public Set<String> getParticipatingTests(String identity) {
		return (Set<String>) cache.get(cacheKeyAdapter.participatingCacheKey(identity));
	}

	public Set<String> getTestsListening(String conversionName) {
		return (Set<String>) cache.get(cacheKeyAdapter.testsListeningCacheKey(conversionName));
	}

	public void setAlternatives(String testName,
			Alternatives alternatives) {
		cache.put(cacheKeyAdapter.alternativesCacheKey(testName), alternatives);
	}

	public void setParticipatingTests(String identity,
			Set<String> participatingTests) {
		cache.put(cacheKeyAdapter.participatingCacheKey(identity), participatingTests);
	}

	public void setTestsListening(String conversionName,
			Set<String> testsListeningToConversion) {
		cache.put(cacheKeyAdapter.testsListeningCacheKey(conversionName), testsListeningToConversion);
	}

	

	
}
