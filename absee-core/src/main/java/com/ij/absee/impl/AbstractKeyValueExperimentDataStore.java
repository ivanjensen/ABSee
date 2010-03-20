package com.ij.absee.impl;


import com.ij.absee.dao.ExperimentDataStore;

/**
 * Abstract class with common behaviour for key-value datastores. 
 */
public abstract class AbstractKeyValueExperimentDataStore implements ExperimentDataStore {
	
	protected DefaultCacheKeyAdapter cacheKeyAdapter = new DefaultCacheKeyAdapter();

	protected abstract String get(final String key);
	protected abstract void put(final String key, final String value);
	protected abstract boolean containsKey(final String key);
	
	public String getAlternativeThatWon(String testName) {
		return get(cacheKeyAdapter.testCompleteCacheKey(testName));
	}
	
	public void newTestCreated(String testName) {
		put(cacheKeyAdapter.testExistsCacheKey(testName), "1");
	}
	
	public void testFinished(String testName, String finalAlternative) {
		put(cacheKeyAdapter.testCompleteCacheKey(testName), finalAlternative);
	}
	
	public boolean hasTestEnded(String testName) {
		return containsKey(cacheKeyAdapter.testCompleteCacheKey(testName));
	}
	
	public boolean hasConverted(String identity, String testName) {
		String value = get(cacheKeyAdapter.convertedCacheKey(identity, testName));
		if (value == null) {
			return false;
		}
		return "1".equalsIgnoreCase(value);
	}
	
	public void recordUserConversion(String identity, String testName) {
		put(cacheKeyAdapter.convertedCacheKey(identity, testName), "1");		
	}
	
	public boolean testExists(final String testName) {
		return containsKey(cacheKeyAdapter.testExistsCacheKey(testName));
	}
	
	protected interface ConnectionFactory<T> {
		public T getConnection();
	}
	
	/**
	 * Abstract class to allow correct/consistent use of key-value connectors
	 *
	 * @param <T> the type of connector to be used inside the template.
	 */
	protected class CacheOperationTemplate<T> {
		private final T connection;

		public CacheOperationTemplate(final ConnectionFactory<T> connectionFactory) {
			this.connection = connectionFactory.getConnection();
		}
		
		public <R> R query(KeyValueQuery<T, R> query) {
			try {
				synchronized (connection) {
					return query.doOperation(connection);
				}
			} catch (Exception e) {
				
			} finally {
			}
			return null;
		}

		public void update(KeyValueUpdate<T> query) {
			try {
				synchronized (connection) {
					query.doOperation(connection);
				}
			} catch (Exception e) {
				
			} finally {
			}
		}
	}
	
	protected interface KeyValueQuery<T, R> {
		public abstract R doOperation(final T client) throws Exception;
		
	}

	protected interface KeyValueUpdate<T> {
		public abstract void doOperation(final T client) throws Exception;
		
	}
}
