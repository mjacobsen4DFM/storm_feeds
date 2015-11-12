package com.transactstorm.storm_feeds;

import java.io.Serializable;
import java.util.Map;

public class Publisher implements Serializable {
	private static final long serialVersionUID = -4787117016512778268L;
	private String originator;
	private Integer pubid;
	private String pubKey;
	private String url;
	private String cat;
	private String api_url;
	private String feedType;
	private String sourceType;
	private String phaseType;
	private String itemElement;
	private String itemOutRoot;
	private String username;
	private String password;
	private String oauthkey;
	
	
	public Publisher(){}
	
	public Publisher(Map<String, String> keys){		
		this.originator = keys.get("originator");
		this.pubid = Integer.parseInt(keys.get("pubid"));
		this.pubKey = keys.get("pubKey");
		this.url = keys.get("url");
		this.cat = keys.get("cat");
		this.api_url = keys.get("api_url");
		this.itemElement = keys.get("itemElement");
		this.itemOutRoot = keys.get("itemOutRoot");
		this.username = keys.get("stamp");
		this.password = keys.get("validity");
		this.oauthkey = keys.get("guid");
		this.feedType = keys.get("feedType");
		this.sourceType = keys.get("sourceType");
		this.phaseType = keys.get("phaseType");
	}
	
	public Publisher(byte[] serializedFeed){
		Publisher feed = new Publisher();
		try {
			feed = (Publisher)StormUtil.deserialize(serializedFeed);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		this.originator = feed.getOriginator();
		this.pubid = feed.getPubid();
		this.pubKey = feed.getFeedKey();
		this.url = feed.getUrl();
		this.cat = feed.getCat();
		this.api_url = feed.getApi_url();
		this.itemElement = feed.getItemElement();
		this.itemOutRoot = feed.getItemOutRoot();
		this.username = feed.getUsername();
		this.password = feed.getPassword();
		this.oauthkey = feed.getOauthkey();
		this.feedType = feed.getFeedType();
		this.sourceType = feed.getSourceType();
		this.phaseType = feed.getPhaseType();
	}
	
	public Integer getPubid() {
		return pubid;
	}
	public void setPubid(Integer id) {
		this.pubid = id;
	}
	
	public String getFeedKey() {
		return pubKey;
	}
	public void setFeedKey(String feedKey) {
		this.pubKey = feedKey;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getCat() {
		return cat;
	}
	public void setCat(String cat) {
		this.cat = cat;
	}
	
	public String getApi_url() {
		return api_url;
	}
	public void setApi_url(String api_url) {
		this.api_url = api_url;
	}
	
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getOauthkey() {
		return oauthkey;
	}
	public void setOauthkey(String oauthkey) {
		this.oauthkey = oauthkey;
	}


	public String getOriginator() {
		return originator;
	}

	public void setOriginator(String originator) {
		this.originator = originator;
	}

	public String getFeedType() {
		return feedType;
	}

	public void setFeedType(String feedType) {
		this.feedType = feedType;
	}

	public String getItemElement() {
		return itemElement;
	}

	public void setItemElement(String itemElement) {
		this.itemElement = itemElement;
	}

	public String getItemOutRoot() {
		return itemOutRoot;
	}

	public void setItemOutRoot(String itemOutRoot) {
		this.itemOutRoot = itemOutRoot;
	}

	public String getPhaseType() {
		return phaseType;
	}

	public void setPhaseType(String phaseType) {
		this.phaseType = phaseType;
	}

}
