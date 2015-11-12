package com.transactstorm.storm_feeds;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class PublisherSpout extends BaseRichSpout{
	private static final long serialVersionUID = 1L;
	private SpoutOutputCollector spoutOutputCollector;
	private String redisHost = "pub-redis-11611.us-west-2-1.1.ec2.garantiadata.com";
	private Integer redisPort = 11611;
	private Integer redisTimeout = 30000;
	//private String redisHost = "127.0.0.1"; //pub-redis-11611.us-west-2-1.1.ec2.garantiadata.com";
	//private Integer redisPort = 6379; //11611;
	//private Integer redisTimeout = 30000;
	private String redisPassword= "pw4Myredis";
	private String redisKey = "publishers:*";
	private String hostname = "Unknown";
	private ArrayList<String> sites = null;
	private RedisClient redisClient = new RedisClient();
	private byte[] serRedisClient = null;
	
	@SuppressWarnings("rawtypes")
	public void open(Map conf, TopologyContext context, SpoutOutputCollector spoutOutputCollector) {
		// Open the spout
		this.spoutOutputCollector = spoutOutputCollector;
		try
		{
		    InetAddress addr;
		    addr = InetAddress.getLocalHost();
		    hostname = addr.getHostName();
		}
		catch (Exception ex)
		{
		    System.out.println("Hostname can not be resolved");
		}
		
		try {
			redisClient = new RedisClient(redisHost, redisPort, redisTimeout, redisPassword);
			//redisClient = new RedisClient(redisHost, redisPort, redisTimeout);	//, redisPassword);
			serRedisClient = StormUtil.serialize(redisClient);
		} catch (IOException e) {
			StormUtil.log(e);
		}
	}
		
	public void nextTuple() {
		sites = redisClient.keys(redisKey);
		Iterator<String> SiteIterator = sites.iterator();
		String pubKey = "";
		String msgId = "";
		try {
			while (SiteIterator.hasNext()) {
				pubKey = SiteIterator.next();
				msgId = pubKey + ":" + hostname + ":" + System.nanoTime();
				if(!pubKey.contains("/")){ //Skip URL keys
					spoutOutputCollector.emit(new Values(pubKey, serRedisClient), msgId);
				}
			}
			//Thread.sleep(60000);
		} catch (Exception e) {
			String msg = "Spout(" + msgId + ");";
			StormUtil.log(msg, e);
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// create the tuple with field names for Site
		declarer.declare(new Fields("pubKey", "redisClient"));
	}

	public void ack(Object msgId) {
	    System.out.println("Spout ACK on msgId " + msgId);
	}
	
	public void fail(Object msgId){
	    System.out.println("Spout FAIL on msgId" + msgId);
	}
}