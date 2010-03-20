package com.ij.absee.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Set;

import org.jredis.JRedis;
import org.jredis.RedisException;
import org.jredis.ri.alphazero.JRedisClient;

import com.ij.absee.domain.Alternatives;

/**
 * A redis backed experiment data store backed by Redis, accessed through JRedis.
 * 
 * @See http://code.google.com/p/jredis/
 * @See http://code.google.com/p/redis/
 */
@SuppressWarnings("unchecked")
public class RedisABSeeCache extends AbstractKeyValueExperimentDataStore {

	public ConnectionFactory<JRedis> connectionFactory = new JRedisConnectionFactoryImpl();
	
	public Set<String> getTestsListening(final String conversionName) {
		CacheOperationTemplate<JRedis> template = new CacheOperationTemplate<JRedis>(
				connectionFactory);
		return template.query(new KeyValueQuery<JRedis, Set<String>>() {

			public java.util.Set<String> doOperation(JRedis client)
					throws Exception {

				byte[] value = client.get(cacheKeyAdapter
						.testsListeningCacheKey(conversionName));
				try {
					if (value == null) {
						return null;
					}
					ObjectInputStream ois = new ObjectInputStream(
							new ByteArrayInputStream(value));
					return (Set<String>) ois.readObject();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		});
	}
	
	public void setTestsListening(final String conversionToUse, final Set<String> testsListeningToConversion) {
		
		CacheOperationTemplate<JRedis> template = new CacheOperationTemplate<JRedis>(connectionFactory);
		template.update(new KeyValueUpdate<JRedis>() {
			public void doOperation(JRedis client) throws RedisException {
				client.set(cacheKeyAdapter.testsListeningCacheKey(conversionToUse), (Serializable) testsListeningToConversion);
			}			
		});
	}	
	
	public void setParticipatingTests(final String identity, final Set<String> participatingTests) {
		CacheOperationTemplate<JRedis> template = new CacheOperationTemplate<JRedis>(connectionFactory);
		template.update(new KeyValueUpdate<JRedis>() {
			public void doOperation(JRedis client) throws RedisException {
				client.set(cacheKeyAdapter.participatingCacheKey(identity), (Serializable) participatingTests);
			}
		});
	}
	
	public Set<String> getParticipatingTests(final String identity) {
		CacheOperationTemplate<JRedis> template = new CacheOperationTemplate<JRedis>(connectionFactory);
		return template.query(new KeyValueQuery<JRedis, Set<String>>() {
			public Set<String> doOperation(JRedis client) throws Exception {
				byte [] value = client.get(cacheKeyAdapter.participatingCacheKey(identity));
				try {
					if (value == null) {
						return null;
					}
					ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(value));
					return (Set<String>) ois.readObject();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;			}
			
		});
	}

	
	public void flush() {
		CacheOperationTemplate<JRedis> template = new CacheOperationTemplate<JRedis>(connectionFactory);
		template.update(new KeyValueUpdate<JRedis>() {
			public void doOperation(JRedis client) throws RedisException {
				client.flushdb();
			}			
		});
	}
	
	public boolean containsKey(final String key) {
		CacheOperationTemplate<JRedis> template = new CacheOperationTemplate<JRedis>(connectionFactory);
		return template.query(new KeyValueQuery<JRedis, Boolean>() {
			public Boolean doOperation(JRedis client) throws Exception {
				return client.exists(key);
			}
		});
	}

	public String get(final String key) {
		CacheOperationTemplate<JRedis> template = new CacheOperationTemplate<JRedis>(connectionFactory);
		return template.query(new KeyValueQuery<JRedis, String>() {
			public String doOperation(JRedis client) throws Exception {
				byte [] value = client.get(key);
				return value == null ? null : new String(value, Charset.forName("UTF-8"));
			}
		});
	}

	public void put(final String key, final String value) {
		CacheOperationTemplate<JRedis> template = new CacheOperationTemplate<JRedis>(connectionFactory);
		template.update(new KeyValueUpdate<JRedis>() {
			public void doOperation(JRedis client) throws Exception {
				client.set(key, value);
			}
		});
	}
	
	public void setAlternatives(final String testName, final Alternatives alternatives) {
		CacheOperationTemplate<JRedis> template = new CacheOperationTemplate<JRedis>(connectionFactory);
		template.update(new KeyValueUpdate<JRedis>() {
			public void doOperation(JRedis client) throws Exception {
				client.set(cacheKeyAdapter.alternativesCacheKey(testName), alternatives);
			}
		});
	}
	
	public Alternatives getAlternatives(final String testName) {
		CacheOperationTemplate<JRedis> template = new CacheOperationTemplate<JRedis>(connectionFactory);
		return template.query(new KeyValueQuery<JRedis, Alternatives>() {
			public Alternatives doOperation(JRedis client) throws Exception {
				byte [] value = client.get(cacheKeyAdapter.alternativesCacheKey(testName));
				try {
					if (value == null) {
						return null;
					}
					ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(value));
					return (Alternatives) ois.readObject();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
		});
	}
	
	private class JRedisConnectionFactoryImpl implements ConnectionFactory<JRedis> {
		private JRedis connection = new JRedisClient("secret");
		public JRedis getConnection() {
			return connection;
		}
	}

}
