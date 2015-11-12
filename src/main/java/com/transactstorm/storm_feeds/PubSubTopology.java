package com.transactstorm.storm_feeds;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
//import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class PubSubTopology {
	public static StormTopology buildTopology(String[] args) throws AlreadyAliveException, InvalidTopologyException {
		TopologyBuilder builder = new TopologyBuilder();
		// Spout to get feeds names from Redis
		builder.setSpout("PublisherSpout", new PublisherSpout(), 1);
		// Bolt to get feed URL from Redis based on feed name
		builder.setBolt("PublisherBolt", new PublisherBolt(), 1).shuffleGrouping("PublisherSpout");
		// Bolt to determine feed type and send to appropriate bolt based on feed class
		builder.setBolt("FeedLoadBolt", new FeedLoadBolt(), 1).shuffleGrouping("PublisherBolt");
		// Bolt to process Feed class
		builder.setBolt("FeedReaderBolt", new FeedReaderBolt(), 1).shuffleGrouping("FeedLoadBolt");
		// Bolt to extract Feed Item data
		builder.setBolt("SinglePhaseLoadBolt", new SinglePhaseLoadBolt(), 1).shuffleGrouping("FeedReaderBolt", "SinglePhase");
		// Bolt to extract Feed Item data
		builder.setBolt("MultiPhaseLoadBolt", new MultiPhaseLoadBolt(), 1).shuffleGrouping("FeedReaderBolt", "MultiPhase");
		// Bolt to extract Feed Item data
		builder.setBolt("StoryReaderBolt", new StoryReaderBolt(), 1).fieldsGrouping("SinglePhaseLoadBolt", new Fields("story", "feedKey", "publisher", "redisClient")).fieldsGrouping("MultiPhaseLoadBolt", new Fields("story", "subjects", "feedKey", "publisher", "redisClient"));
		// Bolt to extract Item data
		builder.setBolt("SubscriberBolt", new SubscriberBolt(), 1).shuffleGrouping("StoryReaderBolt");
		// Bolt to extract Publish Item data
		builder.setBolt("WordPressBolt", new WordPressBolt(), 1).shuffleGrouping("SubscriberBolt", "WordPress");
		//builder.setBolt("OtherBolt", new WordPressBolt(), 4).shuffleGrouping("SubscriberBolt", "Other");
		return builder.createTopology();
	}

	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
		Config conf = new Config();
		conf.setDebug(false);
		conf.setNumWorkers(6);
		conf.setMaxSpoutPending(1);
		conf.setMessageTimeoutSecs(60);
		// create an instance of LocalCluster class for executing topology in local mode.
		LocalCluster cluster = new LocalCluster();

		// FeedToplogy is the name of submitted topology.
		cluster.submitTopology("PubSubTopology", conf, buildTopology(args));
	}
}