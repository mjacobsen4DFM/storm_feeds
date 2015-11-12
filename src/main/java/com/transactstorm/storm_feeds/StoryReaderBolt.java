package com.transactstorm.storm_feeds;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class StoryReaderBolt extends BaseRichBolt {
	private static final long serialVersionUID = 1L;

    OutputCollector _collector;
	
	@SuppressWarnings("rawtypes")
	public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        _collector = collector;
	}

	public void execute(Tuple tuple) {
		Publisher publisher = new Publisher();
		RedisClient redisClient = new RedisClient();
		StorySubjects subjects;
		String sStory = "";
		String feedKey = "";
		String storyKey = "";
		
		try {
			//Get feed and article objects
			sStory = tuple.getStringByField("story");
			feedKey = tuple.getStringByField("feedKey");
			publisher = new Publisher(tuple.getBinaryByField("publisher"));
			redisClient = new RedisClient(tuple.getBinaryByField("redisClient"));
			sStory = sStory.replace("<" + publisher.getItemElement() + ">", publisher.getItemOutRoot());
			subjects = (StorySubjects) StormUtil.deserialize(tuple.getBinaryByField("subjects"));
			
			System.out.println("SRB->getPhaseType: " + publisher.getPhaseType() + ", SRB->getFeedType: " + publisher.getFeedType());
			
			//Transform story to get attributes
			//System.out.println("XMLString:" + sStory);
			String xsltPath = "/var/storm/examples/transactstorm/storm_feeds/src/xslt/" + publisher.getFeedType() + "_Story.xslt";
			//System.out.println("xslt: " + xsltPath);
			String storyXML = XmlUtil.transform(sStory, xsltPath);
			System.out.println("storyXML: " + storyXML);
			Story story = Story.fromXML(storyXML);
			//Add parental subjects to the story attributes
			story.setStorySubjects(subjects);
			String sStorySubjects = story.toXml();
			System.out.println("XMLstory:" + sStorySubjects);

			//System.out.println("gxa feed.getSourceType(): " + feed.getSourceType() + "story.getGuid(): " + story.getGuid());
			String[] keyArgs = {"content", publisher.getSourceType(), story.getGuid()};
			storyKey = ContentControl.setKey(keyArgs);
			
			//Check uniqueness
			byte[] b5 = StormUtil.serialize(sStory);
			String s5 = new String(b5, StandardCharsets.UTF_8);
			String m5 = ContentControl.createMD5(s5);			
			Map<String, Object> statusMap = new HashMap<String, Object>();
			statusMap = ContentControl.checkStatus(storyKey, m5, redisClient);	
			byte[] binaryStatusMap = StormUtil.serialize(statusMap);		
			
			if((Boolean)statusMap.get("isNew")|(Boolean)statusMap.get("isUpdated")) {
				System.out.println("new/changed: " + story.getTitle() + "; oldSourceMD5: " + (String)statusMap.get("oldMD5") + "; newSourceMD5: " + (String)statusMap.get("newMD5"));
		        _collector.emit(tuple, new Values(storyKey, sStory, sStorySubjects, binaryStatusMap, tuple.getStringByField("feedKey"), tuple.getBinaryByField("publisher"), tuple.getBinaryByField("redisClient")));	
				try{
					Map<String, String> hashMap = new HashMap<String, String>();
					hashMap.put("md5", m5);
					hashMap.put("title", story.getTitle());				
					ContentControl.trackContent(storyKey, hashMap, redisClient);
				} catch (Exception e) {	
					Exception ex = new Exception("StoryReaderBolt error");
					String msg = e.getMessage();
					StormUtil.log(tuple, msg, ex);
					ContentControl.resetMD5(feedKey, redisClient);		
					_collector.fail(tuple);			
					return;
				}
			}
			_collector.ack(tuple);
		} catch (Exception e) {
			String msg = "Reset" + " Key: " + feedKey;			
			StormUtil.log(tuple, msg, e);
			ContentControl.resetMD5(storyKey, redisClient);
			ContentControl.resetMD5(feedKey, redisClient);					
			_collector.fail(tuple);	 
			return;
		}	 
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("storyKey", "story", "sStorySubjects", "statusMap", "feedKey", "publisher", "redisClient"));	
	}  
}
