package com.transactstorm.storm_feeds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class SubscriberBolt extends BaseRichBolt {
	private static final long serialVersionUID = 1L;

    OutputCollector _collector;
	
	@SuppressWarnings("rawtypes")
	public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        _collector = collector;
	}

	public void execute(Tuple tuple) {
		Publisher publisher = new Publisher();
		RedisClient redisClient = new RedisClient();
		String subscriberKey = "";
		ArrayList<String> subscribers = null;
		
		try {
			publisher = new Publisher(tuple.getBinaryByField("publisher"));
			String[] keyArgs = {"subscribers", publisher.getFeedKey().replace("publishers:", ""),"*"};
			subscriberKey = ContentControl.setKey(keyArgs);
			redisClient = new RedisClient(tuple.getBinaryByField("redisClient"));
			subscribers = redisClient.keys(subscriberKey);
			Iterator<String> SubscriberIterator = subscribers.iterator();
			while (SubscriberIterator.hasNext()) {
				String subscriber = SubscriberIterator.next();
				if(!subscriber.contains("content")){//Skip Story keys
					Map<String, String> subscriberMap = redisClient.hgetAll(subscriber);
					byte[] binarySubscriberMap = StormUtil.serialize(subscriberMap);			
					String subscriberType = subscriberMap.get("type");
					System.out.println("subscriberType: " + subscriberType);
					_collector.emit(subscriberType, tuple, new Values(binarySubscriberMap, subscriber, tuple.getStringByField("storyKey"), tuple.getStringByField("story"), tuple.getStringByField("sStorySubjects"), tuple.getBinaryByField("statusMap"), tuple.getStringByField("feedKey"), tuple.getBinaryByField("publisher"), tuple.getBinaryByField("redisClient")));					
				}	
			}
			_collector.ack(tuple);
		} catch (Exception e) {
			String msg = "Reset" + " feedKey: " + tuple.getStringByField("feedKey") + " storyKey: " + tuple.getStringByField("storyKey");			
			StormUtil.log(tuple, msg, e);
			ContentControl.resetMD5(tuple.getStringByField("storyKey"), redisClient);
			ContentControl.resetMD5(tuple.getStringByField("feedKey"), redisClient);					
			_collector.fail(tuple);	 
			return;
		}	 
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream("WordPress", new Fields("subscriberMap", "subscriber", "storyKey", "story", "sStorySubjects", "statusMap", "feedKey", "publisher", "redisClient"));	
		//declarer.declareStream("Other", new Fields("binarySubscriberMap", "storyKey", "story", "statusMap", "feedKey", "feed", "redisClient"));	
	}  
}
