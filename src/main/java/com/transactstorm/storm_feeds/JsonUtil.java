package com.transactstorm.storm_feeds;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

public class JsonUtil {    
    public static String toJSON(Object obj){
    	Gson gson = new Gson();
    	String json = gson.toJson(obj);
    	//System.out.println(json);
    	return json;
    }   
	   
    
	 @SuppressWarnings("unchecked")
	public static <T> Object fromJSON(String json, Object obj){
	 	Gson gson = new Gson();
	 	Object objOut = gson.fromJson(json, (Class<T>) obj);
	 	return objOut;
	 }
	 
	 public static String getValue(String json, String key) throws ParseException{
			JSONParser parser=new JSONParser();
			JSONObject jsonObj = null;
			String value = "";
			jsonObj = (JSONObject) parser.parse(json);
			value = jsonObj.get(key).toString();
		 return value;
	 }
}
