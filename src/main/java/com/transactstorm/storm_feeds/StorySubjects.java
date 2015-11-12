package com.transactstorm.storm_feeds;

import java.io.IOException;
import java.io.Serializable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

//@SuppressWarnings("restriction")
@XmlRootElement(name = "subjects")
public class StorySubjects  implements Serializable{
	private static final long serialVersionUID = -428988001071921405L;
	private String[] subjects;
    
	StorySubjects(){}
	
	public static StorySubjects fromXML(String xml) throws JAXBException, ParserConfigurationException, SAXException, IOException{
		JAXBContext jc = JAXBContext.newInstance(StorySubjects.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        JAXBElement<StorySubjects> je1 = unmarshaller.unmarshal(XmlUtil.deserialize(xml), StorySubjects.class);
        return je1.getValue();		
	}


	public String[] getSubjects() {
		return subjects;
	}

	@XmlElement( name = "subject" )
	public void setSubjects(String[] subject) {
		this.subjects = subject;
	}	
}