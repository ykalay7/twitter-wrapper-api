package com.twitter.wrapper.controller;

import com.twitter.wrapper.api.Tweet;
import com.twitter.wrapper.api.tweet.TweetService;
import com.twitter.wrapper.api.tweet.TweetServiceException;
import com.twitter.wrapper.auth.AuthenticationException;
import com.twitter.wrapper.auth.Token;
import com.twitter.wrapper.auth.services.AuthService;
import com.twitter.wrapper.dto.CreateTweetRequest;
import com.twitter.wrapper.dto.CreateTweetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tweet")
public class TweetController {

    private final AuthService authService;
    private final TweetService tweetService;

    @Autowired
    public TweetController(AuthService authService, TweetService tweetService) {
        this.authService = authService;
        this.tweetService = tweetService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateTweetResponse generateToken(@RequestBody CreateTweetRequest createTweetRequest, @RequestHeader(HttpHeaders.AUTHORIZATION) String bearToken) throws AuthenticationException, TweetServiceException {
        Token token = authService.retrieveToken(bearToken);
        Tweet newTweet = tweetService.createNewTweet(createTweetRequest.getMessage(), token);
        return new CreateTweetResponse(newTweet.getTweetId(), newTweet.getMessage());
    }
}
