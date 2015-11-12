package com.transactstorm.storm_feeds;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.parsers.DocumentBuilder;  
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.w3c.dom.Document;  
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;


public class FeedLoadBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;

    OutputCollector _collector;
	
	@SuppressWarnings("rawtypes")
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        _collector = collector;
	}

	public void execute(Tuple tuple) {
		Publisher publisher = new Publisher();	
		String feedKey = "";
		
		try {
			publisher = new Publisher(tuple.getBinaryByField("publisher"));
			String[] keyArgs = {"feeds", publisher.getFeedKey().replace("publishers:", "")};
			feedKey = ContentControl.setKey(keyArgs);
			System.out.println("feedKey: " + feedKey);
			
			//Read feed
			WebClient client = new WebClient(publisher);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();  
			Document doc = builder.parse(client.openStream());  
			
			
			//System.out.println("publisher.getItemElement(): " + publisher.getItemElement());
			NodeList items = doc.getElementsByTagName(publisher.getItemElement());   
			
			if(items.getLength() != 0){   
				Document newXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		        Element root = newXmlDocument.createElement("root");
		        newXmlDocument.appendChild(root);
		        for (int i = 0; i < items.getLength(); i++) {
		            Node node = items.item(i);
		            Node copyNode = newXmlDocument.importNode(node, true);
		            root.appendChild(copyNode);
		        }
				String sStories = XmlUtil.serialize(newXmlDocument);   
				sStories= BasicUtil.CleanString(sStories);
				_collector.emit(tuple, new Values(sStories, feedKey, tuple.getBinaryByField("publisher"), tuple.getBinaryByField("redisClient")));
			}
			_collector.ack(tuple);	 
		} catch (MalformedURLException e) {				
			StormUtil.log(tuple, e);
			_collector.fail(tuple);	 
		} catch (ParserConfigurationException e) {				
			StormUtil.log(tuple, e);
			_collector.fail(tuple);	 
		} catch (SAXException e) {				
			StormUtil.log(tuple, e);
			_collector.fail(tuple);	 
		} catch (IOException e) {				
			StormUtil.log(tuple, e);
			_collector.fail(tuple);	 
		} catch (TransformerFactoryConfigurationError e) {			
			//StormUtil.log(tuple, e);
			_collector.fail(tuple);	
		}	
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("stories", "feedKey", "publisher", "redisClient"));
		//declarer.declareStream("GenericXML", new Fields("articles", "feed", "redisClient"));
		//declarer.declareStream("AP", new Fields("entries", "feed", "redisClient"));
	}  
	  
	public String getValue(Element parent, String nodeName) {  
		return parent.getElementsByTagName(nodeName).item(0).getFirstChild().getNodeValue();  
	}  
}
