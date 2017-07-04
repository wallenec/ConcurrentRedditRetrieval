<b>Concurrent Reddit Retrieval</b><br/>
Emily Wallen<br/>
July 3, 2017
 
This application calls the Reddit API to retrieve the top post from five different subreddits simultaneously. It would be trivial to configure the application to work with user-determined subreddits, but for this proof of concept I have chosen <b>r/aww</b>, <b>r/askScience</b>, <b>r/worldnews</b>, <b>r/programming</b>, and <b>r/food</b>. To retrieve the five top posts, simply visit <b>/getRedditPosts</b>.
 
 
<b>Notes:</b>
  - Requirements simply asked for the application to retrieve the “top” post. I interpreted this to mean the post returned by /top for a given subreddit in the API, i.e. the post you’d see if you went into the subreddit and selected “top” and “past 24 hours.” 
  - Requirements requested a count of downvotes. Unfortunately, Reddit no longer publicizes downvote counts; they can only be    approximated based on upvote count and upvote-downvote ratio. Making this approximation requires an additional API call, and I figured it was probably out of the scope of the project. Downvote counts will be displayed as zero, which is what the API returns.
  - Requirements requested “resource URL or content.” Since a post can have a URL and/or content (“self text”), these are two separate fields in the returned JSON.


Hopefully JSON fields have intuitive names, but just in case:
  - <b>subredditName:</b> subreddit name, e.g. “programming”
  - <b>postTitle:</b> title of the post, e.g. “look at this cat picture”
  - <b>postAuthor:</b> the author or user who uploaded the post, e.g. “unidan”
  - <b>timestampMst:</b> the time the post was created, formatted as dd/MM/yyyy hh:ss a in MST, e.g. “07/03/2017 12:13 PM”
  - <b>upvoteCount:</b> the number of upvotes a post has
  - <b>downvoteCount:</b> the number of downvotes a post has (will always be zero)
  - <b>selftext:</b> whatever text the author included in the text field of the post; this could be the entirety of the post, or just some form of commentary on the URL.
  - <b>url:</b> the URL associated with the post, if it exists 
