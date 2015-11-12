package com.transactstorm.storm_feeds;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
public class PublisherBolt extends BaseRichBolt{

	private static final long serialVersionUID = 1L;

    OutputCollector _collector;
	
	@SuppressWarnings("rawtypes")
	public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        _collector = collector;
	}

	public void execute(Tuple tuple) {
		// Get the fields from input tuple.
		String pubKey = tuple.getStringByField("pubKey");
		System.out.println("pubKey: " + pubKey);
		RedisClient redisClient = new RedisClient(tuple.getBinaryByField("redisClient"));
		try {
			Map<String, String> keys = redisClient.hgetAll(pubKey);
			keys.put("pubKey", pubKey);
			Publisher publisher = new Publisher(keys);
			byte[] binaryPublisher = StormUtil.serialize(publisher);
			_collector.emit(tuple, new Values(binaryPublisher, tuple.getBinaryByField("redisClient")));
	        _collector.ack(tuple);
		} catch (Exception e) {				
			StormUtil.log(tuple, e);
	        _collector.fail(tuple);
		}	
	}
	 
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("publisher", "redisClient"));			
	}
}
