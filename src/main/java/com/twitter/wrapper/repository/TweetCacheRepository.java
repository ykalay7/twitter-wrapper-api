package com.twitter.wrapper.repository;

import com.twitter.wrapper.api.Tweet;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TweetCacheRepository {
    private final Map<Long, Tweet> tweetCache;
    public TweetCacheRepository() {
        this.tweetCache = new HashMap<>();
    }

    public void addNewTweet(Tweet tweet) {
        this.tweetCache.put(tweet.getTweetId(), tweet);
    }

    public void deleteTweet(Long tweetId) {
        this.tweetCache.remove(tweetId);
    }

    public List<Tweet> listRecentTweets() {
        return this.tweetCache.values().stream().toList();
    }
}
