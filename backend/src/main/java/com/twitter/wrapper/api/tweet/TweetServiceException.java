package com.twitter.wrapper.api.tweet;

public class TweetServiceException extends Exception {
    public TweetServiceException(String message) {
        super(message);
    }

    public TweetServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
