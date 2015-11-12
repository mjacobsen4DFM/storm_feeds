package com.transactstorm.storm_feeds;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.SafeEncoder;

public class RedisClient implements Serializable {
	private static final long serialVersionUID = -9102851779759656124L;
	private String host;
	private Integer port;
	private Integer timeout;
	private String password;
	
	public RedisClient(){}
	
	public RedisClient(String host, Integer port, Integer timeout, String password){
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.password = password;
	}

	
	public RedisClient(String host, Integer port, Integer timeout){
		this.host = host;
		this.port = port;
		this.timeout = timeout;
	}
	
	public RedisClient(byte[] serializedRedisClient){
		RedisClient redisClient = new RedisClient();
		try {
			redisClient = (RedisClient)StormUtil.deserialize(serializedRedisClient);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		this.host = redisClient.getHost();
		this.port = redisClient.getPort();
		this.timeout = redisClient.getTimeout();
		this.password = redisClient.getPassword();
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public Integer getTimeout() {
		return timeout;
	}
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public ArrayList<String> keys(String redisKey){
		return keys(redisKey, this.host, this.port, this.timeout, this.password);
	}
	
	public static ArrayList<String> keys(String redisKey, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass){
	    JedisPool pool = new JedisPool(new JedisPoolConfig(), redisHost, redisPort, redisTimeout, redisPass);
	    Jedis jedis = pool.getResource();
	    try {
	        Set<byte[]> rKeys = jedis.keys(SafeEncoder.encode(redisKey));
	        if (CollectionUtils.isEmpty(rKeys)) {
	            return new ArrayList<String>();
	        } else {
	            ArrayList<String> sKeys = new ArrayList<String>();
	            for (byte[] bb : rKeys) {
	                sKeys.add(SafeEncoder.encode(bb));
	            }
	            return sKeys;
	        }
	    } catch (Exception e) {  
	        e.printStackTrace();  
	        return null;
	    }
	    finally{
	    	if(jedis != null){
	    		jedis.close();
	    	}
	    	if(pool != null){
	    		pool.close();
	    	    pool.destroy();
	    	}
	    }
	}	
	
	public Map<String, String> hgetAll(String redisKey){
		return hgetAll(redisKey, this.host, this.port, this.timeout, this.password);
	}
	
	public static Map<String, String> hgetAll(String redisKey, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass){
		Map<String, String> keys;
	    JedisPool pool = new JedisPool(new JedisPoolConfig(), redisHost, redisPort, redisTimeout, redisPass);
	    Jedis jedis = pool.getResource();
	    try{
			keys = jedis.hgetAll(redisKey);
	    } catch (Exception e) {  
	        e.printStackTrace();  
	        return null;
	    }
	    finally{
	    	if(jedis != null){
	    		jedis.close();
	    	}
	    	if(pool != null){
	    		pool.close();
	    	    pool.destroy();
	    	}
	    }
		return keys;
	}		
	
	public String hget(String redisKey, String hashKey){
		return hget(redisKey, hashKey, this.host, this.port, this.timeout, this.password);
	}
	
	public static String hget(String redisKey, String hashKey, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass){
		String value;
	    JedisPool pool = new JedisPool(new JedisPoolConfig(), redisHost, redisPort, redisTimeout, redisPass);
	    Jedis jedis = pool.getResource();
	    try{
			value = jedis.hget(redisKey, hashKey);
	    } catch (Exception e) {  
	        e.printStackTrace();  
	        return null;
	    }
	    finally{
	    	if(jedis != null){
	    		jedis.close();
	    	}
	    	if(pool != null){
	    		pool.close();
	    	    pool.destroy();
	    	}
	    }
		return value;
	}		
	
	public void hmset(String redisKey, Map<String, String> hash){
		 hmset(redisKey, hash, this.host, this.port, this.timeout, this.password);
	}
	
	public static void hmset(String redisKey, Map<String, String> hash, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass){
	    JedisPool pool = new JedisPool(new JedisPoolConfig(), redisHost, redisPort, redisTimeout, redisPass);
	    Jedis jedis = pool.getResource();
	    try{
	    	jedis.hmset(redisKey, hash);
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }
	    finally{
	    	if(jedis != null){
	    		jedis.close();
	    	}
	    	if(pool != null){
	    		pool.close();
	    	    pool.destroy();
	    	}
	    }
	}	
	
	public void hset(String redisKey, String hashKey, String hashValue){
		 hset(redisKey, hashKey, hashValue, this.host, this.port, this.timeout, this.password);
	}
	
	public static void hset(String redisKey, String hashKey, String hashValue, String redisHost, Integer redisPort, Integer redisTimeout, String redisPass){
	    JedisPool pool = new JedisPool(new JedisPoolConfig(), redisHost, redisPort, redisTimeout, redisPass);
	    Jedis jedis = pool.getResource();
	    try{
	    	jedis.hset(redisKey, hashKey, hashValue);
	    } catch (Exception e) {  
	        e.printStackTrace();  
	    }
	    finally{
	    	if(jedis != null){
	    		jedis.close();
	    	}
	    	if(pool != null){
	    		pool.close();
	    	    pool.destroy();
	    	}
	    }
	}
}
