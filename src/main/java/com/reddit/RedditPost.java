package com.reddit;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
//Represents a single Reddit post
public class RedditPost {

	@JsonProperty
	private String subredditName;
	@JsonProperty
	private String postTitle;
	@JsonProperty
	private String postAuthor;
	@JsonProperty
	private String timestampMst;
	@JsonProperty
	private String upvoteCount;
	//Note: Reddit no longer publicizes downvote counts. They can only be approximated based on
	//vote ratio. I assumed this was out of the scope of this project.
	@JsonProperty
	private String downvoteCount;
	@JsonProperty
	private String selftext;
	@JsonProperty
	private String url;
	
	public RedditPost(String subredditName, String postTitle, String postAuthor, String timestampMst, String upvoteCount,
			String downvoteCount, String selftext, String url) {
		this.subredditName = subredditName;
		this.postTitle = postTitle;
		this.postAuthor = postAuthor;
		this.timestampMst = timestampMst;
		this.upvoteCount = upvoteCount;
		this.downvoteCount = downvoteCount;
		this.selftext = selftext;
		this.url = url;
	}
	
	//Converts a map of flattened API response data into a RedditPost object.
	//This strategy is in lieu of creating a pile of boilerplate classes to replicate the structure of 
	//the API's response. This boilerplate could simply be filled in by the ObjectMapper.
	public RedditPost(Map<String, Object> jsonMap) {
		this.postTitle = (String)jsonMap.get("data.children[0].data.title");
		this.subredditName = (String)jsonMap.get("data.children[0].data.subreddit");
		this.selftext = (String)jsonMap.get("data.children[0].data.selftext");
		this.url = (String)jsonMap.get("data.children[0].data.url");
		this.downvoteCount = convertVoteCount((BigDecimal)jsonMap.get("data.children[0].data.downs"));
		this.upvoteCount = convertVoteCount((BigDecimal)jsonMap.get("data.children[0].data.ups"));
		this.postAuthor = (String)jsonMap.get("data.children[0].data.author");
		this.timestampMst = convertTimestamp((BigDecimal)jsonMap.get("data.children[0].data.created_utc"));
	}
	
	//Formats vote counts to be comma-separated in typical American format.
	public String convertVoteCount(BigDecimal count) {
		return NumberFormat.getNumberInstance(Locale.US).format(count.longValue());
	}
	
	//Converts UTC time in seconds to formatted MST timestamp
	public String convertTimestamp(BigDecimal millis) {
		long longMillis = millis.longValue();
		Date date = new Date(longMillis * 1000);
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		formatter.setTimeZone(TimeZone.getTimeZone("America/Denver"));
		return formatter.format(date);
	}

	//Needed for Jackson serialization
	public RedditPost() { }
	
	public String getsubredditName() {
		return subredditName;
	}

	public String getpostTitle() {
		return postTitle;
	}

	public String getpostAuthor() {
		return postAuthor;
	}

	public String gettimestampMst() {
		return timestampMst;
	}

	public String getupvoteCount() {
		return upvoteCount;
	}

	public String getdownvoteCount() {
		return downvoteCount;
	}

	public String getselftext() {
		return selftext;
	}
	
	public String getUrl() {
		return url;
	}
	
}
