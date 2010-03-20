package com.ij.absee.impl;

import java.util.Set;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.ij.absee.domain.Alternatives;

/**
 * An experiment data store backed by Memcached.
 */
@SuppressWarnings("unchecked")
public class MemcachedExperimentDataStore extends AbstractKeyValueExperimentDataStore {

	private ConnectionFactory<MemCachedClient> connectionFactory = new MemCachedConnectionFactoryImpl();
	private DefaultCacheKeyAdapter cacheKeyAdapter = new DefaultCacheKeyAdapter(); 
	
	public Set<String> getParticipatingTests(final String identity) {
		CacheOperationTemplate<MemCachedClient> template = new CacheOperationTemplate<MemCachedClient>(connectionFactory);
		return template.query(new KeyValueQuery<MemCachedClient, Set<String>>() {
			public Set<String> doOperation(MemCachedClient client) throws Exception {
				return (Set<String>) client.get(cacheKeyAdapter
						.participatingCacheKey(identity));
			}
		});
	}	
	
	public void setParticipatingTests(final String identity, final Set<String> participatingTests) {
		CacheOperationTemplate<MemCachedClient> template = new CacheOperationTemplate<MemCachedClient>(connectionFactory);
		template.update(new KeyValueUpdate<MemCachedClient>() {
			public void doOperation(MemCachedClient client) throws Exception {
				client.set(cacheKeyAdapter.participatingCacheKey(identity), participatingTests);
			}
		});
	};
	
	public Set<String> getTestsListening(final String conversionName) {
		CacheOperationTemplate<MemCachedClient> template = new CacheOperationTemplate<MemCachedClient>(connectionFactory);
		return template.query(new KeyValueQuery<MemCachedClient, Set<String>>() {
			public Set<String> doOperation(MemCachedClient client) throws Exception {
				return (Set<String>) client.get(cacheKeyAdapter.testsListeningCacheKey(conversionName));
			}
		});
	}

	
	public void setTestsListening(final String conversionToUse, final Set<String> testsListeningToConversion) {
		CacheOperationTemplate<MemCachedClient> template = new CacheOperationTemplate<MemCachedClient>(connectionFactory);
		template.update(new KeyValueUpdate<MemCachedClient>() {
			public void doOperation(MemCachedClient client) throws Exception {
				client.set(cacheKeyAdapter.testsListeningCacheKey(conversionToUse), testsListeningToConversion);
			}
		});		
	}
	
	public void flush() {
		CacheOperationTemplate<MemCachedClient> template = new CacheOperationTemplate<MemCachedClient>(connectionFactory);
		template.update(new KeyValueUpdate<MemCachedClient>() {
			public void doOperation(MemCachedClient client) throws Exception {
				client.flushAll();
			}
		});			
	}
	
	public boolean containsKey(final String key) {
		CacheOperationTemplate<MemCachedClient> template = new CacheOperationTemplate<MemCachedClient>(connectionFactory);
		return template.query(new KeyValueQuery<MemCachedClient, Boolean>() {
			public Boolean doOperation(MemCachedClient client) throws Exception {
				return client.keyExists(key);
			}
		});		
	}

	public String get(final String key) {
		CacheOperationTemplate<MemCachedClient> template = new CacheOperationTemplate<MemCachedClient>(connectionFactory);
		return template.query(new KeyValueQuery<MemCachedClient, String>() {
			public String doOperation(MemCachedClient client) throws Exception {
				return (String) client.get(key);
			}
		});	
	}

	public void put(final String key, final String value) {
		CacheOperationTemplate<MemCachedClient> template = new CacheOperationTemplate<MemCachedClient>(connectionFactory);
		template.update(new KeyValueUpdate<MemCachedClient>() {
			public void doOperation(MemCachedClient client) throws Exception {
				client.set(key, value);
			}
		});				
	}
	
	public void setAlternatives(final String testName, final Alternatives alternatives) {
		CacheOperationTemplate<MemCachedClient> template = new CacheOperationTemplate<MemCachedClient>(connectionFactory);
		template.update(new KeyValueUpdate<MemCachedClient>() {
			public void doOperation(MemCachedClient client) throws Exception {
				client.set(cacheKeyAdapter.alternativesCacheKey(testName), alternatives);
			}
		});		
	}
	
	public Alternatives getAlternatives(final String testName) {
		CacheOperationTemplate<MemCachedClient> template = new CacheOperationTemplate<MemCachedClient>(connectionFactory);
		return template.query(new KeyValueQuery<MemCachedClient, Alternatives>() {
			public Alternatives doOperation(MemCachedClient client) throws Exception {
				return (Alternatives) client.get(cacheKeyAdapter.alternativesCacheKey(testName));
			}
		});	
	}
	
	private class MemCachedConnectionFactoryImpl implements ConnectionFactory<MemCachedClient> {
		private SockIOPool pool = SockIOPool.getInstance();
		private MemCachedClient connection = new MemCachedClient();
		{
			pool.setServers(new String [] {"127.0.0.1:11211"});
			pool.initialize();
		}
		public MemCachedClient getConnection() {
			return connection;
		}
	}

}
