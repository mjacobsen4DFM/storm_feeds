package com.transactstorm.storm_feeds;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class SinglePhaseLoadBolt extends BaseRichBolt {
	private static final long serialVersionUID = 1L;

    OutputCollector _collector;
	
	@SuppressWarnings("rawtypes")
	public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        _collector = collector;
	}

	public void execute(Tuple tuple) {
		RedisClient redisClient = new RedisClient();
		String sStory = "";
		String feedKey = "";
		
		try {
			//System.out.println("phaseTypeBOLT: SinglePhase");
			//Get feed and article objects
			sStory = tuple.getStringByField("story");
			feedKey = tuple.getStringByField("feedKey");
			redisClient = new RedisClient(tuple.getBinaryByField("redisClient"));
      	  
	        sStory= BasicUtil.CleanString(sStory);
			
			//Simple pass-thru for when story content is within the feed
        	_collector.emit(tuple, new Values(sStory, tuple.getStringByField("feedKey"), tuple.getBinaryByField("publisher"), tuple.getBinaryByField("redisClient")));	 
			_collector.ack(tuple);
		} catch (Exception e) {
			String msg = "Reset" + " Key: " + feedKey;			
			StormUtil.log(tuple, msg, e);
			ContentControl.resetMD5(feedKey, redisClient);					
			_collector.fail(tuple);	 
			return;
		}	 
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("story", "feedKey", "publisher", "redisClient"));	
	}  
}
