package com.transactstorm.storm_feeds;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerConfigurationException;
//import javax.xml.transform.TransformerException;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.io.*;
//import java.util.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class XmlUtil{  
    public static Document deserialize(String sXML) throws ParserConfigurationException, SAXException, IOException{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
        DocumentBuilder builder; 
        builder = factory.newDocumentBuilder();  
        Document doc = builder.parse( new InputSource( new StringReader(sXML) ) ); 
        return doc;
    }  
    
	public static Document loadXMLFrom(String xml) throws SAXException, IOException {
	    return loadXMLFrom(new ByteArrayInputStream(xml.getBytes()));
	}

	public static Document loadXMLFrom(InputStream inputStream) throws SAXException, IOException {
		Document doc = null;
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    javax.xml.parsers.DocumentBuilder builder = null;
	    try {
	        builder = factory.newDocumentBuilder();
		    doc = builder.parse(inputStream);
	    }
	    catch (ParserConfigurationException ex) {   
			StormUtil.log(ex);
	    }
	    finally{
		    inputStream.close();	    	
	    }
	    return doc;
	}
	
	public static Document deserialize(Object object) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();

	    JAXBContext context = JAXBContext.newInstance(object.getClass());	
	    Marshaller m = context.createMarshaller();
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);	
	    m.marshal(object, doc);	
	    return doc;
	}
    
    public static String serialize(Document doc){
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            // below code to remove XML declaration
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return output;
        } catch (Exception e) {   
			StormUtil.log(e);
            return null;
        }
    }
    
    public static String serialize(Node doc){
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
            // below code to remove XML declaration
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString();
            return output;
        } catch (Exception e) {   
			StormUtil.log(e);
            return null;
        }
    }
    
	public static String transform(String xmlString, String stylesheetPathname) throws TransformerException {
		try {
		    TransformerFactory factory = TransformerFactory.newInstance();
		    Source stylesheetSource = new StreamSource(new File(stylesheetPathname).getAbsoluteFile());
		    Transformer transformer = factory.newTransformer(stylesheetSource);
		    Source inputSource = new StreamSource(new StringReader(xmlString));
		    Writer outputWriter = new StringWriter();
		    Result outputResult = new StreamResult(outputWriter);
		    transformer.transform(inputSource, outputResult);
		    return outputWriter.toString();
		} catch(TransformerConfigurationException tce) {
			throw new TransformerException(tce.getMessageAndLocation());
		} catch (TransformerException te) {
			throw new TransformerException(te.getMessageAndLocation());
		}
	}

    
	public static String getFirstChildNodeValue(Element parent, String nodeName) {
		try {
			return parent.getElementsByTagName(nodeName).item(0).getFirstChild().getNodeValue();
        } catch (Exception e) {   
			StormUtil.log(e);
            return null;
        }
	}  
	
	public static String getFirstChildNodeValue(Document parent, String nodeName) {  
		try {
			return parent.getElementsByTagName(nodeName).item(0).getFirstChild().getNodeValue();  
        } catch (Exception e) {   
			StormUtil.log(e);
            return null;
        }
	}
	  
	public static String getFirstValueFromXPath(Element parent, String xpath) {
		try {
			XPath xPath =  XPathFactory.newInstance().newXPath();
			//NodeList nodeList = (NodeList) xPath.compile(xpath).evaluate(parent, XPathConstants.NODESET);
			return (String) xPath.compile(xpath).evaluate(parent, XPathConstants.STRING);
			//return nodeList.item(0).getNodeValue();
      } catch (Exception e) {   
			StormUtil.log(e);
          return null;
      }
	}  
	  
	public static String getFirstValueFromXPath(Document parent, String xpath) {
		try {
			XPath xPath =  XPathFactory.newInstance().newXPath();
			//NodeList nodeList = (NodeList) xPath.compile(xpath).evaluate(parent, XPathConstants.NODESET);
			return (String) xPath.compile(xpath).evaluate(parent, XPathConstants.STRING);
			//return nodeList.item(0).getNodeValue();
		} catch (Exception e) {   
			StormUtil.log(e);
			return null;
		}	
	}

//	public static String serialize(NodeList nodes) throws TransformerFactoryConfigurationError, TransformerException {
//		StringWriter sw = new StringWriter(); 
//		Transformer serializer = TransformerFactory.newInstance().newTransformer(); 
//		serializer.transform(new DOMSource(nodes.item(0)), new StreamResult(sw));	
//		String result = sw.toString(); 
//		return result;
//	}  
}

