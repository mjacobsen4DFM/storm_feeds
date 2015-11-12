package com.transactstorm.storm_feeds;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class MultiPhaseLoadBolt extends BaseRichBolt {
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
		String phaseStory = "";
		String sourceURL = "";
		String sourceURLs = "";
		String sourceKey = "";
		String updateTest = "";
		Map<String, Object> statusMap = new HashMap<String, Object>();
		Map<String, String> hashMap = new HashMap<String, String>();
		
		try {
			//System.out.println("phaseTypeBOLT: TwoPhase");
			//Get feed and article objects
			Publisher publisher = new Publisher(tuple.getBinaryByField("publisher"));
			WebClient client = new WebClient(publisher);
			sStory = tuple.getStringByField("story");
			feedKey = tuple.getStringByField("feedKey");
			redisClient = new RedisClient(tuple.getBinaryByField("redisClient"));
			
			String xsltPath = "/var/storm/examples/transactstorm/storm_feeds/src/xslt/" + publisher.getFeedType() + "_MultiPhase.xslt";
			String storyXML = XmlUtil.transform(sStory, xsltPath);
			Story story = Story.fromXML(storyXML);
			StoryLinks storyLinks = story.getStoryLinks();
			String[] links = storyLinks.getLinks();
			StorySubjects storySubjects = story.getStorySubjects();
			//String[] subjects = storySubjects.getSubjects();
			String[] keyArgs = {"source", publisher.getSourceType(), story.getGuid()};
			sourceKey = ContentControl.setKey(keyArgs);
			
			updateTest = story.getUpdateTest();
			if(updateTest != null && updateTest != ""){
				statusMap = ContentControl.compareKeys(sourceKey, "updateTest", updateTest, redisClient);
				if((Boolean)statusMap.get("isNew")|(Boolean)statusMap.get("isUpdated")) {
			        for (int i = 0; i < links.length; i++) { 
			        	sourceURL = links[i]; 
			        	sourceURLs += " " + sourceURLs;
						hashMap.put("sourceURL", sourceURLs.trim());	
			        	client.setUrl(sourceURL);
			        	phaseStory += client.get();
			        }      	  
			        phaseStory= BasicUtil.CleanString(phaseStory);
			        _collector.emit(tuple, new Values(phaseStory, StormUtil.serialize(storySubjects), tuple.getStringByField("feedKey"), tuple.getBinaryByField("publisher"), tuple.getBinaryByField("redisClient")));

					try{
						hashMap.put("updateTest", story.getUpdateTest());
						hashMap.put("title", story.getTitle());							
						ContentControl.trackContent(sourceKey, hashMap, redisClient);
					} catch (Exception e) {	
						Exception ex = new Exception("StoryReaderBolt error");
						String msg = e.getMessage();
						StormUtil.log(tuple, msg, ex);
						ContentControl.resetMD5(feedKey, redisClient);		
						_collector.fail(tuple);	
					}						
				}
			} 
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
		declarer.declare(new Fields("story", "subjects", "feedKey", "publisher", "redisClient"));	
	}    
}
