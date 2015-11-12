package com.transactstorm.storm_feeds;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class ContentControl {
	public static boolean isNew(String redisKey, RedisClient redisClient){
		return getMD5(redisKey, redisClient) == null;
	}
	
	public static boolean isUpdated(String redisKey, String content, RedisClient redisClient){
		String md5Test = getMD5(redisKey, redisClient);
		String md5 = (md5Test != null)?md5Test:"";
		return !md5.contentEquals(createMD5(content));
	}
	
	public static boolean isNewOrUpdated(String redisKey, String content, RedisClient redisClient){
		return isNew(redisKey, redisClient) | isUpdated(redisKey, content, redisClient);
	}	
	
	public static Map<String, Object> checkStatus(String redisKey, String md5, RedisClient redisClient){		
		return compareKeys(redisKey, "md5", md5, redisClient);
	}	
	
	public static Map<String, Object> compareKeys(String redisKey, String key, String newValue, RedisClient redisClient){
		Map<String, Object> statusMap = new HashMap<String, Object>();
		Map<String, String> keys = redisClient.hgetAll(redisKey);	
		
		boolean bNew = (keys.get(key) == null)?true:false;
		String preKey = (bNew)?"":keys.get(key);
		String thisKey =  newValue;
		boolean bUpdated = (bNew)?false:!preKey.contentEquals(thisKey);
		
		statusMap.put("isNew", bNew);
		statusMap.put("isUpdated", bUpdated);
		statusMap.put("old" + key.toUpperCase(), preKey);
		statusMap.put("new" + key.toUpperCase(), thisKey);
		if(!bNew){
			statusMap.put("id", keys.get("id"));
			statusMap.put("title", keys.get("title"));
		}
		return statusMap;
	}
	
	public static void setMD5(String redisKey, String content, RedisClient redisClient){
		redisClient.hset(redisKey, "md5", createMD5(content));	
	}		
	
	public static String getMD5(String redisKey, RedisClient redisClient){
		Map<String, String> keys = redisClient.hgetAll(redisKey);
		return keys.get("md5");
	}
	
	public static void updateMD5(String redisKey, String content, RedisClient redisClient){
		setMD5(redisKey, content, redisClient);
	}		
	
	public static void resetMD5(String redisKey, RedisClient redisClient){
		redisClient.hset(redisKey, "md5", "");	
	}	
	
	public static String cleanKey(String key){
		key = key.replace("http://", "");
		key = key.substring(1);
		return key;
	}	
	
	public static String setKey(String[] args){
		String key = "";
		for (int i=0; i<args.length; i++){
			key = key + ":" +  args[i];			
		}
		return cleanKey(key);
	}	
	
	public static void trackContent(String redisKey, String hashKey, String hashValue, RedisClient redisClient){
		//setMD5(redisKey, content, redisClient);
		redisClient.hset(redisKey, hashKey, hashValue);
	}
	
	public static void trackContent(String redisKey, Map<String, String> hashMap, RedisClient redisClient){
		//setMD5(redisKey, content, redisClient);
		redisClient.hmset(redisKey, hashMap);
	}
    
  	public static String createMD5(String str){
  		byte[] bytesOfMessage = null;
  		try {
  			bytesOfMessage = str.getBytes("UTF-8");
  		} catch (UnsupportedEncodingException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}

  		MessageDigest md = null;
  		try {
  			md = MessageDigest.getInstance("MD5");
  		} catch (NoSuchAlgorithmException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
  		byte[] thedigest = md.digest(bytesOfMessage);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < thedigest.length; ++i) {
          sb.append(Integer.toHexString((thedigest[i] & 0xFF) | 0x100).substring(1,3));
        }
        return sb.toString();        
  	}
}
