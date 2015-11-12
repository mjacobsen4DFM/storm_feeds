package com.transactstorm.storm_feeds;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.simple.parser.ParseException;


public class WordPressClient {
	private String host;
	private Integer port;
	private Integer timeout;
	private String username;
	private String password;
	
	public WordPressClient(){}
	
	public WordPressClient(String host, String username, String password){
		this.host = host;
		this.username = username;
		this.password = password;
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
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

	public HashMap<String, String> post(WordPressPost wpp, String endpoint) throws ClientProtocolException, IOException {
		String wppJSON = JsonUtil.toJSON(wpp);
		System.out.println("JSON: " + wppJSON);
		return post(wppJSON, endpoint);
	}

	public HashMap<String, String> post(String json, String endpoint) throws ClientProtocolException, IOException {
		String result = "";
		String url = this.host + endpoint;
		String credentials = this.username + ":" + this.password;
		System.out.println("credentials: " + credentials);
		String credentials64 = Base64.encodeBase64URLSafeString(credentials.getBytes());
		while((credentials64.length() % 4) > 0){
			credentials64 += "=";
		}
		System.out.println("credentials64: " + credentials64);
		HttpClient client = new DefaultHttpClient();
		System.out.println("POST: " + url);
		HttpPost post = new HttpPost(url);				
		post.setHeader("Authorization", "Basic " + credentials64);
		post.setHeader(new BasicHeader("Content-Type", "application/json"));			
		post.setHeader(new BasicHeader("Accept", "application/json"));	
		System.out.println("JSON: " + json);
		StringEntity input = new StringEntity(json);
		post.setEntity(input);
		HttpResponse response = client.execute(post);
		StatusLine sl = response.getStatusLine();
		if( sl.getStatusCode() == 201){
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";	
			while ((line = rd.readLine()) != null) {			
				result += line;
			}
		}
		else{
			result = sl.getReasonPhrase();
		}
		HashMap<String, String> mapResult = new HashMap<String, String>();
		mapResult.put("code", Integer.toString(sl.getStatusCode()));
		mapResult.put("result", result);
		return mapResult;
	}
	
	public HashMap<String, String> post(String endpoint) throws ClientProtocolException, IOException {
		String result = "";
		String url = this.host + endpoint;
		String credentials = this.username + ":" + this.password;
		System.out.println("credentials: " + credentials);
		String credentials64 = Base64.encodeBase64URLSafeString(credentials.getBytes());
		while((credentials64.length() % 4) > 0){
			credentials64 += "=";
		}
		System.out.println("credentials64: " + credentials64);
		HttpClient client = new DefaultHttpClient();
		System.out.println("POST: " + url);
		HttpPost post = new HttpPost(url);				
		post.setHeader("Authorization", "Basic " + credentials64);
		post.setHeader(new BasicHeader("Content-Type", "application/json"));			
		post.setHeader(new BasicHeader("Accept", "application/json"));
		HttpResponse response = client.execute(post);
		StatusLine sl = response.getStatusLine();
		if( sl.getStatusCode() == 201){
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";	
			while ((line = rd.readLine()) != null) {			
				result += line;
			}
		}
		else{
			result = sl.getReasonPhrase();
		}
		HashMap<String, String> mapResult = new HashMap<String, String>();
		mapResult.put("code", Integer.toString(sl.getStatusCode()));
		mapResult.put("result", result);
		return mapResult;
	}

	public HashMap<String, String> put(WordPressPost wpp, String endpoint) throws ClientProtocolException, IOException {
		String wppJSON = JsonUtil.toJSON(wpp);
		System.out.println("JSON: " + wppJSON);
		return put(wppJSON, endpoint);
	}	

	public HashMap<String, String> put(String json, String endpoint) throws ClientProtocolException, IOException {
		String result = "";
		String url = this.host + endpoint;
		String credentials = this.username + ":" + this.password;
		System.out.println("credentials: " + credentials);
		String credentials64 = Base64.encodeBase64URLSafeString(credentials.getBytes());
		while((credentials64.length() % 4) > 0){
			credentials64 += "=";
		}
		System.out.println("credentials64: " + credentials64);
		HttpClient client = new DefaultHttpClient();		
		System.out.println("PUT: " + url);
		HttpPut put = new HttpPut(url);		
		put.setHeader("Authorization", "Basic " + credentials64);
		put.setHeader(new BasicHeader("Content-Type", "application/json"));			
		put.setHeader(new BasicHeader("Accept", "application/json"));
		System.out.println("JSON: " + json);
		StringEntity input = new StringEntity(json);
		put.setEntity(input);
		HttpResponse response = client.execute(put);
		StatusLine sl = response.getStatusLine();
		if( sl.getStatusCode() == 201){
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";	
			while ((line = rd.readLine()) != null) {			
				result += line;
			}
		}
		else{
			result = sl.getReasonPhrase();
		}
		HashMap<String, String> mapResult = new HashMap<String, String>();
		mapResult.put("code", Integer.toString(sl.getStatusCode()));
		mapResult.put("result", result);
		return mapResult;
	}

	public HashMap<String, String> put(String endpoint) throws ClientProtocolException, IOException {
		String result = "";
		String url = this.host + endpoint;
		String credentials = this.username + ":" + this.password;
		System.out.println("credentials: " + credentials);
		String credentials64 = Base64.encodeBase64URLSafeString(credentials.getBytes());
		while((credentials64.length() % 4) > 0){
			credentials64 += "=";
		}
		System.out.println("credentials64: " + credentials64);
		HttpClient client = new DefaultHttpClient();		
		System.out.println("PUT: " + url);
		HttpPut put = new HttpPut(url);		
		put.setHeader("Authorization", "Basic " + credentials64);
		put.setHeader(new BasicHeader("Content-Type", "application/json"));			
		put.setHeader(new BasicHeader("Accept", "application/json"));
		HttpResponse response = client.execute(put);
		StatusLine sl = response.getStatusLine();
		if( sl.getStatusCode() == 201){
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";	
			while ((line = rd.readLine()) != null) {			
				result += line;
			}
		}
		else{
			result = sl.getReasonPhrase();
		}
		HashMap<String, String> mapResult = new HashMap<String, String>();
		mapResult.put("code", Integer.toString(sl.getStatusCode()));
		mapResult.put("result", result);
		return mapResult;
	}
	

	public HashMap<String, String> uploadImage(String imageSource, String imageType, String imageName) throws ClientProtocolException, IOException, ParseException{
		WebClient wc = new WebClient(imageSource);
		BufferedImage bi = wc.GetImage();
		return upload(bi, imageType, imageName, "media");
	}

	public HashMap<String, String> upload(BufferedImage image, String imageType, String imageName, String endpoint) throws ClientProtocolException, IOException {
		String result = "";
		String credentials = this.username + ":" + this.password;
		String url = this.host + endpoint;
		String authorizationString = "Basic " + Base64.encodeBase64URLSafeString(credentials.getBytes());
		HttpClient client = new DefaultHttpClient();
		System.out.println("POST: " + url);
		
		HttpPost post = new HttpPost(url);		
		post.setHeader(new BasicHeader("Content-Type", imageType));	
		post.setHeader(new BasicHeader("Content-Disposition", "filename=" + imageName));
		post.setHeader(new BasicHeader("Accept", "application/json"));	
		post.setHeader("Authorization", authorizationString);
		
		ByteArrayOutputStream baos=new ByteArrayOutputStream(1000);
		ImageIO.write(image, "jpg", baos);
		baos.flush();
		 		
		ByteArrayEntity bits = new ByteArrayEntity(baos.toByteArray());
		baos.close();
		
		post.setEntity(bits);
		
		HttpResponse response = client.execute(post);
		StatusLine sl = response.getStatusLine();
		if( sl.getStatusCode() == 201){
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";	
			while ((line = rd.readLine()) != null) {			
				result += line;
			}
		}
		else{
			result = sl.getReasonPhrase();
		}
		HashMap<String, String> mapResult = new HashMap<String, String>();
		mapResult.put("code", Integer.toString(sl.getStatusCode()));
		mapResult.put("result", result);
		return mapResult;
	}
	 
	public static String get(String[] args) throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet("http://mason-dev.medianewsgroup.com/wp-json/wp/v2/posts/");
		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
		String result = "";
		String line = "";
		while ((line = rd.readLine()) != null) {			
			result += line;
		}
		return result;
	}
}
