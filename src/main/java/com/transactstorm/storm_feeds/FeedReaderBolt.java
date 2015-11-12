package com.transactstorm.storm_feeds;

import org.w3c.dom.Document;  
import org.w3c.dom.Element;  
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;


public class FeedReaderBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;

    OutputCollector _collector;
	
	@SuppressWarnings("rawtypes")
	public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        _collector = collector;
	}
	
	public void execute(Tuple tuple) {
		Publisher publisher = new Publisher();
		RedisClient redisClient = new RedisClient();			
		String sStories = "";
		String feedKey = "";
		String phaseType = "";
		try {
			sStories = tuple.getStringByField("stories");
			publisher = new Publisher(tuple.getBinaryByField("publisher"));
			phaseType = publisher.getPhaseType();
			feedKey = tuple.getStringByField("feedKey");
			redisClient = new RedisClient(tuple.getBinaryByField("redisClient"));

			String md5 = ContentControl.createMD5(sStories);
			Map<String, Object> statusMap = new HashMap<String, Object>();
			statusMap = ContentControl.checkStatus(feedKey, md5, redisClient);
			boolean bNew = (Boolean)statusMap.get("isNew");
			boolean bUpdated = (Boolean)statusMap.get("isUpdated");
			
			if(bNew|bUpdated) {
				System.out.println("UPDATED FeedType: " + publisher.getFeedType() + " feedKey: " + feedKey);
				Document doc = XmlUtil.deserialize(sStories);
				NodeList stories = doc.getElementsByTagName(publisher.getItemElement());
		        for (int i = 0; i < stories.getLength(); i++) {  
		            Element story = (Element)stories.item(i);  
		        	String sStory = XmlUtil.serialize(story);		        	  
		        	sStory= BasicUtil.CleanString(sStory);
					//System.out.println("phaseType: " + phaseType);
		        	_collector.emit(phaseType, tuple, new Values(sStory, feedKey, tuple.getBinaryByField("publisher"), tuple.getBinaryByField("redisClient")));	        
			    }  
				Map<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("md5", md5);
				ContentControl.trackContent(feedKey, hashMap, redisClient);
			} 
			_collector.ack(tuple);	 
		} catch (Exception e) {	
			String msg = "Reset" + " Key: " + feedKey;			
			StormUtil.log(tuple, msg, e);
			ContentControl.resetMD5(feedKey, redisClient);	
			_collector.fail(tuple);	 	 
		}	
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream("SinglePhase", new Fields("story", "feedKey", "publisher", "redisClient"));	
		declarer.declareStream("MultiPhase", new Fields("story", "feedKey", "publisher", "redisClient"));			
	}  
}
