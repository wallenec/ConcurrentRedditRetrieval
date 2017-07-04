package com.reddit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.json.flattener.JsonFlattener;

@RestController
public class RedditRetrievalController {

	private Executor executor;
	private ObjectMapper objectMapper;
	
	//Here I am assuming that "top post" in the requirements refers to the #1 post retrieved
	//from "top" in the Reddit API and not the current #1 post on that subreddit's page.
	//This could easily be changed by altering REDDIT_API_SUFFIX.
	private static final String REDDIT_API_SUFFIX = "/top/.json?limit=1";
	private static final String REDDIT_API_ADDRESS = "http://www.reddit.com/r/";
	
	public RedditRetrievalController() {
		this.executor = new SimpleAsyncTaskExecutor();
		this.objectMapper = new ObjectMapper();
	}

	@RequestMapping
	public String index() {
        return "This ConcurrentRedditRetrieval project was created by Emily Wallen on 7/3/17. Visit /getRedditPosts to"
        		+ " retrieve the top posts from r/programming, r/aww, r/askscience, r/worldnews, and r/food.";
    }
	
    @RequestMapping("/getRedditPosts")
    public String getRedditPosts() throws Exception {
    	
    	List<String> subreddits = Arrays.asList(new String[] { "programming", "aww", "askscience", "worldnews", "food" });
        List<RedditRetrievalTask> tasks = new ArrayList<RedditRetrievalTask>();
        List<RedditPost> result = new ArrayList<RedditPost>();
        
        //Create a task for each API call
        for(String subreddit : subreddits) {
	        tasks.add(new RedditRetrievalTask(REDDIT_API_ADDRESS + subreddit + REDDIT_API_SUFFIX, this.executor));
        }
        
        //Wait for all tasks to complete
        while(!tasks.isEmpty()) {
        	
            for(Iterator<RedditRetrievalTask> it = tasks.iterator(); it.hasNext();) {
            	RedditRetrievalTask task = it.next();
                if(task.isDone()) {
                    String response = task.getResponse();
                    Map<String, Object> map = JsonFlattener.flattenAsMap(response);
                    result.add(new RedditPost(map));
                    it.remove();
                }
            }
            
            //Avoid re-checking *too* frequently
            if(!tasks.isEmpty()) Thread.sleep(100);
        }

        String results = objectMapper.writeValueAsString(result);
        return results;
    }

    //Represents a task to retrieve post(s) from one subreddit. Wraps Callable and Future.
    class RedditRetrievalTask {
        
    	private RedditRetrievalRequest work;
        private FutureTask<String> task;
        
        public RedditRetrievalTask(String url, Executor executor) {
            this.work = new RedditRetrievalRequest(url);
            this.task = new FutureTask<String>(work);
            executor.execute(this.task);
        }
        
        public String getRequest() {
            return this.work.getUrl();
        }
       
        public String getResponse() {
            try {
                return this.task.get();
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        public boolean isDone() {
            return this.task.isDone();
        }
        
    }

    //Represents the HTTP request itself
    class RedditRetrievalRequest implements Callable<String> {
    	
        private final String url;
        
        public RedditRetrievalRequest(String url) {
            this.url = url;
        }
        
        public String getUrl() {
            return this.url;
        }
        
        public String call() throws Exception {
        	List<Header> headers = new ArrayList<Header>();
        	
        	//If the caller doesn't specify a customer user-agent, the API uses a default user, which is not
        	//permitted to make requests as frequently as we need to.
        	headers.add(new BasicHeader("User-agent", "wallenec"));
            return HttpClientBuilder.create().setDefaultHeaders(headers).build().execute(new HttpGet(getUrl()), new BasicResponseHandler());
        }
        
    }
	
}
