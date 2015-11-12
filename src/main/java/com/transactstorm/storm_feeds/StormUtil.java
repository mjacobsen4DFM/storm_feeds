package com.transactstorm.storm_feeds;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
//import java.io.UnsupportedEncodingException;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;

import backtype.storm.tuple.Tuple;


public class StormUtil {
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        byte[] buffer = baos.toByteArray();
        oos.close();
        baos.close();
        return buffer;
    }

    public static Object deserialize(byte[] buffer) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);        
        ObjectInputStream ois = new ObjectInputStream(bais);        
        Object obj = ois.readObject();
        ois.close();
        bais.close();
        return obj;
    }	
    
	public static void log(Exception e){
		String fullStackTrace = getException(e);
		log("", "", fullStackTrace);
	}	   
    
	public static void log(String msg, Exception e){
		String fullStackTrace = getException(e);
		log("", msg, fullStackTrace);
	}
    
    public static void log(Tuple tuple, Exception e){
    	log(tuple, "", e);
    }   
	    
    public static void log(Tuple tuple, String msg, Exception e){
    	String tupleMsg = getTuple(tuple);
    	String fullStackTrace = getException(e);
    	log(tupleMsg, msg, fullStackTrace);
    }
    
    private static void log(String tupleMsg, String friendlyMsg, String exceptionMsg){
    	String strTuple = (tupleMsg != "")?"Tuple(" + tupleMsg + ");":tupleMsg;
    	String strMessage = (friendlyMsg != "")?"Message(" + friendlyMsg + ");":friendlyMsg;
    	String strException = (exceptionMsg != "")?"Exception(" + exceptionMsg + ");":exceptionMsg;
    	String strLogMessage = strTuple + " " + strMessage + " " + strException;
    	log("Error: " + strLogMessage.replace(");", ");\r\n"));
    	
    }   
    
    public static void log(String msg){
    	System.out.println(msg);
    }   
    
    private static String getTuple(Tuple tuple){
    	return "source: " + tuple.getSourceComponent() + ", stream: " + tuple.getSourceStreamId() + ", id: " + tuple.getMessageId() + ", Task: " + tuple.getSourceTask();
    }
    
    private static String getException(Exception e){
    	return org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace(e);
    }
}
