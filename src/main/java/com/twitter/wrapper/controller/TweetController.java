package com.twitter.wrapper.controller;

import com.twitter.wrapper.api.Tweet;
import com.twitter.wrapper.api.tweet.TweetService;
import com.twitter.wrapper.api.tweet.TweetServiceException;
import com.twitter.wrapper.auth.AuthenticationException;
import com.twitter.wrapper.auth.Token;
import com.twitter.wrapper.auth.services.AuthService;
import com.twitter.wrapper.dto.*;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public CreateTweetResponse createTweet(@RequestBody CreateTweetRequest createTweetRequest, @RequestHeader(HttpHeaders.AUTHORIZATION) String bearToken) throws AuthenticationException, TweetServiceException {
        Token token = authService.retrieveToken(bearToken);
        Tweet newTweet = tweetService.createNewTweet(createTweetRequest.getMessage(), token);
        return new CreateTweetResponse(new TweetDto(newTweet.getTweetId(), newTweet.getMessage()));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<DeleteTweetResponse> deleteItem(@PathVariable @NotNull Long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String bearToken) throws AuthenticationException, TweetServiceException {
        Token token = authService.retrieveToken(bearToken);
        boolean isDeleted = tweetService.deleteTweetById(id, token);
        if (isDeleted) {
            return ResponseEntity.ok(new DeleteTweetResponse(true));
        } else {
            return ResponseEntity.status(404).body(new DeleteTweetResponse(false));
        }
    }

    @GetMapping("/listRecent")
    public ListRecentTweetResponse createTweet() {
        List<Tweet> tweetList = tweetService.listRecentTweets();
        return new ListRecentTweetResponse(tweetList.stream().map(tweet -> new TweetDto(tweet.getTweetId(), tweet.getMessage())).toList());
    }

    // Exception Handler for CustomException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ServerException> handleUnexpectedExceptions(@Nonnull RuntimeException runtimeException) {
        return new ResponseEntity<>(new ServerException(runtimeException.getMessage(), ExceptionCodes.UNKNOWN), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
