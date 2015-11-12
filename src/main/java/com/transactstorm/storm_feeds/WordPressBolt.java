package com.transactstorm.storm_feeds;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class WordPressBolt extends BaseRichBolt {
	private static final long serialVersionUID = 1L;

    OutputCollector _collector;
	
	@SuppressWarnings("rawtypes")
	public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        _collector = collector;
	}

	@SuppressWarnings("unchecked")
	public void execute(Tuple tuple) {
		Publisher publisher = new Publisher();
		RedisClient redisClient = new RedisClient();
		String sStory = "";
		String feedKey = "";
		String storyKey = "";
		String subscriber = "";
		HashMap<String, String> mapResult = new HashMap<String, String>();
		String wpId = "";
		String deliveredStoryKey = "";
		String sStorySubjects = "";
		Boolean isPost = false;
		Map<String, Object> statusMap = new HashMap<String, Object>();
		Map<String, String> subscriberMap = new HashMap<String, String>();
		
		try {
			//Get feed and article objects
			subscriberMap = (Map<String, String>)StormUtil.deserialize(tuple.getBinaryByField("subscriberMap"));
			storyKey = tuple.getStringByField("storyKey");
			subscriber = tuple.getStringByField("subscriber");
			sStory = tuple.getStringByField("story");
			sStorySubjects = tuple.getStringByField("sStorySubjects");
			statusMap = (Map<String, Object>)StormUtil.deserialize(tuple.getBinaryByField("statusMap"));
			feedKey = tuple.getStringByField("feedKey");
			publisher = new Publisher(tuple.getBinaryByField("publisher"));
			redisClient = new RedisClient(tuple.getBinaryByField("redisClient"));
			String[] keyArgs = {subscriber.replace("subscribers", "delivered"), storyKey};
			deliveredStoryKey = ContentControl.setKey(keyArgs);
			Story story = Story.fromXML(sStorySubjects);
			String guid = story.getGuid();
			System.out.println("BIGTEST:" + guid);
			System.out.println("getFeedType: " + publisher.getFeedType() + " deliveredStoryKey: " + deliveredStoryKey);
			
			String xsltPath = "/var/storm/examples/transactstorm/storm_feeds/src/xslt/" + publisher.getFeedType() + "_"+ subscriberMap.get("type") + ".xslt";
			//System.out.println("xslt: " + xsltPath);
			String wppXML = XmlUtil.transform(sStory, xsltPath);
			System.out.println("wpXML: " + wppXML);
			WordPressPost wpp = WordPressPost.fromXML(wppXML);
			WordPressClient wpc = new WordPressClient(subscriberMap.get("api_url"), subscriberMap.get("stamp"), subscriberMap.get("validity"));

			//Prep Subjects			
			StorySubjects storySubjects = story.getStorySubjects();	//StorySubjects.fromXML(sStorySubjects);
			String[] subjects = storySubjects.getSubjects();
			String subject = "";
			String tag_id = "";
			String subjectKeyRoot = "taxonomy:" + subscriberMap.get("type") + ":" + publisher.getFeedType() + ":tag:";
			String subjectKey = "";
			
			//Check uniqueness
			byte[] b5 = StormUtil.serialize(wpp);
			String s5 = new String(b5, StandardCharsets.UTF_8);
			String m5 = ContentControl.createMD5(s5);			
			Map<String, Object> subscriberStatusMap = new HashMap<String, Object>();
			subscriberStatusMap = ContentControl.checkStatus(deliveredStoryKey, m5, redisClient);
			String mapId = (String) subscriberStatusMap.get("id");

			//Load/get images
			//xsltPath = "/var/storm/examples/transactstorm/storm_feeds/src/xslt/" + publisher.getFeedType() + "_"+ subscriberMap.get("type") + "_images.xslt";
			//String wppXMLI = XmlUtil.transform(sStory, xsltPath);
			//WordPressPost wppI = WordPressPost.fromXML(wppXMLI);
			Images[] images = wpp.getImages();
			if(images == null) {		
				if(wppXML.contains("<image>")){ 
					System.out.println("IsImage: " + wppXML);
				}
				_collector.fail(tuple);			
				return;
			}
			Image image = null;
			String imageId = "";
			String imageSource = "";
			String imageType = "";
			String imageName = "";			
			String imageCaption = "";		
			String imageCredit = "";		
			String imageTitle = "";
			String imageDate = "";
			
			

			String endpoint = "";
			
			if((Boolean)subscriberStatusMap.get("isNew")|(Boolean)subscriberStatusMap.get("isUpdated")) {				
				try{
					if((Boolean)statusMap.get("isNew")|(mapId == null)){
						//System.out.println("NEW Key: " + storyKey);
						isPost = true;
						System.out.println("WP->New: title:" + wpp.getTitle() + "; content:" + wpp.getContent().substring(0, 5) + "; excerpt:" + wpp.getExcerpt() + "; newWordpressMD5: " + (String)subscriberStatusMap.get("newMD5"));
						endpoint = "posts";
						mapResult = wpc.post(wpp, "posts");
					} else if((Boolean)statusMap.get("isUpdated")) {
						//System.out.println("UPDATED Key: " + storyKey);
						System.out.println("WP->Update: id: " + (String)subscriberStatusMap.get("id") + "; title:" + (String)subscriberStatusMap.get("title") + "; oldWordpressMD5: " + (String)subscriberStatusMap.get("oldMD5") + "; newWordpressMD5: " + (String)subscriberStatusMap.get("newMD5"));
						wpp.setID(mapId);
						endpoint = "posts/" + wpp.getID();
						mapResult = wpc.put(wpp, "posts");
					};

					System.out.println("WPc: " + mapResult.get("code"));
					System.out.println("WPr: " + mapResult.get("result"));
					if(Integer.parseInt(mapResult.get("code").trim()) == 201){	
						System.out.println("GOOD");
						wpId = JsonUtil.getValue(mapResult.get("result"), "id");
						Map<String, String> hashMap = new HashMap<String, String>();
						hashMap.put("id", wpId);
						hashMap.put("title", wpp.getTitle());
						hashMap.put("md5", m5);
						ContentControl.trackContent(deliveredStoryKey, hashMap, redisClient);
						
						if(subjects != null && subjects.length > 0){
							wpp.setID(wpId);
					        for (int i = 0; i < subjects.length; i++) {  
					        	subject = subjects[i];	//BasicUtil.Hyphenate(subjects[i]);
					        	System.out.println("subject: " + subject);
					        	subjectKey = subjectKeyRoot + subject;
					        	System.out.println("subjectKey: " + subjectKey);
					        	tag_id = redisClient.hget(subjectKey, "id");
					        	System.out.println("TAG_ID: " + tag_id);
					        	endpoint = "posts/" + wpId + "/terms/tag/" + tag_id;
								mapResult = wpc.post(endpoint);			
								System.out.println("TAGc: " + mapResult.get("code"));						
								System.out.println("TAGr: " + mapResult.get("result"));
					        }
						}
						
						String json = "";
						if(images != null && images.length != 0){
							for (int i = 0; i < images.length; i++) { 
								image = images[i].getImage();
								if(image != null){
									imageSource = image.getSource();
									imageType = image.getMimetype();
									imageCaption = image.getCaption();
									imageTitle = image.getTitle();
									imageName = BasicUtil.Hyphenate(image.getName());
									imageCredit = image.getCredit();
									imageDate = wpp.getDate();
									mapResult = wpc.uploadImage(imageSource, imageType, imageName);
									if(Integer.parseInt(mapResult.get("code").trim()) == 201){
										imageId = JsonUtil.getValue(mapResult.get("result"), "id");
							        	endpoint = "media/" + imageId;
							        	json = "{\"id\":" + imageId + ",\"author\":3,\"title\": \"" + imageTitle + "\",\"date_gmt\": \"" + imageDate + "\",\"caption\": \"" + imageCaption + "\",\"post\":" + wpId + "}";						
										System.out.println("imgjson: " + json);
										mapResult = wpc.post(json, endpoint);			
									}
								}
					        }   
						}
					}
				} catch (Exception e) {	
					if(mapResult.get("result").startsWith("[")){
						String wpMsg = JsonUtil.getValue(mapResult.get("result").replace("[", "").replace("]", ""), "message");						
						String msg = "Wordpress says, \"" + wpMsg + "\"; isPost: " + isPost + "; For Url: " + wpc.getHost() + "/" + (String)subscriberStatusMap.get("id") +"; Resetting feedKey: " + feedKey + "; for storyKey: "  + storyKey + "; deliveredStoryKey: " + deliveredStoryKey;	
						Exception ex = new Exception("Wordpress error");
						StormUtil.log(tuple, msg, ex);				
					} else if(mapResult.get("result").startsWith("<")){
						String wpMsg = mapResult.get("result");						
						String msg = "Wordpress says, \"" + wpMsg + "\"; isPost: " + isPost + "; For Url: " + wpc.getHost() + "/" + (String)subscriberStatusMap.get("id") +"; Resetting feedKey: " + feedKey + "; for storyKey: "  + storyKey + "; deliveredStoryKey: " + deliveredStoryKey;	
						Exception ex = new Exception("Wordpress error");
						StormUtil.log(tuple, msg, ex);				
					} else {
						StormUtil.log(tuple, e);						
					}		
					ContentControl.resetMD5(deliveredStoryKey, redisClient);
					ContentControl.resetMD5(storyKey, redisClient);	
					ContentControl.resetMD5(feedKey, redisClient);				
					_collector.fail(tuple);			
					return;
				}
				_collector.ack(tuple);
				return;
			} else {
				System.out.println("WP->NO CHANGE: id: " + (String)subscriberStatusMap.get("id") + "; title:" + (String)subscriberStatusMap.get("title") + "; oldWordpressMD5: " + (String)subscriberStatusMap.get("oldMD5") + "; newWordpressMD5: " + (String)subscriberStatusMap.get("newMD5"));						
			};
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
	//	declarer.declare(new Fields("item"));			
	}  
}
