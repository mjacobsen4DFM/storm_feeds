package com.transactstorm.storm_feeds;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

public class Images implements Serializable{
	private static final long serialVersionUID = -2824561042786328184L;
	private Image image;

	public Image getImage() {
		return image;
	}

	@XmlElement( name = "image" )
	public void setLinks(Image image) {
		this.image = image;
	}	

}
