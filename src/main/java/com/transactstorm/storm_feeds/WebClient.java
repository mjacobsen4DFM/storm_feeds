package com.transactstorm.storm_feeds;


import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.xml.sax.InputSource;

public class WebClient {
	private String url;
	private Integer port;
	private Integer timeout;
	private String username;
	private String password;
	
	public WebClient(){}
	
	public WebClient(String url){
		this.url = url;
		this.port = 80;
		this.timeout = 20000;
		this.username = "";
		this.password = "";		
	}
	
	public WebClient(String url, Integer port, Integer timeout, String username, String password){
		this.url = url;
		this.port = port;
		this.timeout = timeout;
		this.username = username;
		this.password = password;
	}
	
	public WebClient(Publisher publisher){
		this.url = publisher.getUrl();
		this.port = 80;
		this.timeout = 20000;
		this.username = publisher.getUsername();
		this.password = publisher.getPassword();
	}

	public WebClient(String url, String username, String password) {
		this.url = url;
		this.port = 80;
		this.timeout = 20000;
		this.username = username;
		this.password = password;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public Integer getTimeout() {
		return timeout;
	}
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public static String post(WordPressPost wpp) throws ClientProtocolException, IOException {
		String credentials = "mjacobsen:pw4wp4DFM!";
		//String url = "http://192.168.56.104/wp-json/wp/v2/posts";
		String url = "http://mason-dev.medianewsgroup.com/wp-json/wp/v2/posts";
		//String url = "http://mason-dev.medianewsgroup.com/wp-json/posts";
		System.out.println("POST: " + url);
		String authorizationString = "Basic " + Base64.encodeBase64URLSafeString(credentials.getBytes());
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);		
		post.setHeader(new BasicHeader("Content-Type", "application/json"));		
		post.setHeader("Authorization", authorizationString);
		String wppJSON = JsonUtil.toJSON(wpp);
		System.out.println("JSON: " + wppJSON);
		StringEntity input = new StringEntity(wppJSON);
		post.setEntity(input);
		HttpResponse response = client.execute(post);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String result = "";
		String line = "";
		while ((line = rd.readLine()) != null) {			
			result += line;
		}
		return result;
	}
	
	public static String put(WordPressPost wpp) throws ClientProtocolException, IOException {
		String credentials = "mjacobsen:pw4wp4DFM!";
		//String url = "http://192.168.56.104/wp-json/wp/v2/posts/" + wpp.getID();
		String url = "http://mason-dev.medianewsgroup.com/wp-json/wp/v2/posts/" + wpp.getID();
		//String url = "http://mason-dev.medianewsgroup.com/wp-json/posts";
		System.out.println("PUT: " + url);
		String authorizationString = "Basic " + Base64.encodeBase64URLSafeString(credentials.getBytes());
		HttpClient client = new DefaultHttpClient();		
		HttpPut put = new HttpPut(url);		
		put.setHeader(new BasicHeader("Content-Type", "application/json"));		
		put.setHeader("Authorization", authorizationString);
		String wppJSON = JsonUtil.toJSON(wpp);
		System.out.println("JSON: " + wppJSON);
		StringEntity input = new StringEntity(wppJSON);
		put.setEntity(input);
		HttpResponse response = client.execute(put);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String result = "";
		String line = "";
		while ((line = rd.readLine()) != null) {			
			result += line;
		}
		return result;
	}
	 
	public String get() throws ClientProtocolException, IOException {
		String credentials = this.username + ":" + this.password;
		String credentials64 = Base64.encodeBase64URLSafeString(credentials.getBytes());
		while((credentials64.length() % 4) > 0){
			credentials64 += "=";
		}
		String authorizationString = "Basic " + credentials64;		
		
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(this.url);	
		request.setHeader("Authorization", authorizationString);

		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String result = "";
		String line = "";
		while ((line = rd.readLine()) != null) {			
			result += line;
		}
	
	    return result;
	}
	 
	public InputSource openStream() throws ClientProtocolException, IOException {
		String credentials = this.username + ":" + this.password;
		String credentials64 = Base64.encodeBase64URLSafeString(credentials.getBytes());
		while((credentials64.length() % 4) > 0){
			credentials64 += "=";
		}
		String authorizationString = "Basic " + credentials64;
		
		
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(this.url);	
		request.setHeader("Authorization", authorizationString);

		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String result = "";
		String line = "";
		while ((line = rd.readLine()) != null) {			
			result += line;
		}
		
	    InputSource inputSource = new InputSource(new StringReader(result));
	    return inputSource;
	}
	

	 
	public BufferedImage GetImage() throws ClientProtocolException, IOException {		
		BufferedImage image = null;
		URL imageURL = new URL(this.url);	
		image = ImageIO.read(imageURL);
	    return image;
	}

}