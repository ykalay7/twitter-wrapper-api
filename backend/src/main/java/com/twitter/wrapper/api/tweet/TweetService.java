package com.twitter.wrapper.api.tweet;

import com.twitter.wrapper.api.Tweet;
import com.twitter.wrapper.api.ext.TwitterExtCreateTweetRequest;
import com.twitter.wrapper.api.ext.TwitterExtCreateTweetResponse;
import com.twitter.wrapper.api.ext.TwitterExtDeleteTweetResponse;
import com.twitter.wrapper.auth.Token;
import com.twitter.wrapper.repository.TweetCacheRepository;
import jakarta.annotation.Nonnull;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

@Service
public class TweetService {

    private static final String TWEET_PATH = "tweets";
    private static final String SPLITTER = "/";
    private final TweetCacheRepository tweetCacheRepository;
    public TweetService(TweetCacheRepository tweetCacheRepository) {
        this.tweetCacheRepository = tweetCacheRepository;
    }
    public Tweet createNewTweet(String message, Token token) throws TweetServiceException {
        WebClient twitterClient = createTwitterClient(token, HttpMethod.POST.toString());
        TwitterExtCreateTweetResponse twitterExtCreateTweetResponse = twitterClient.post()
                .body(BodyInserters.fromValue(new TwitterExtCreateTweetRequest(message)))
                .retrieve()
                .bodyToMono(TwitterExtCreateTweetResponse.class).block();
        if(twitterExtCreateTweetResponse == null || twitterExtCreateTweetResponse.getData() == null) {
            throw new TweetServiceException("Internal error, tweeter creation is returned null from external api");
        }
        Tweet tweet = convertExternalResponseToTweet(twitterExtCreateTweetResponse);
        this.tweetCacheRepository.addNewTweet(tweet);
        return tweet;
    }

    public boolean deleteTweetById(@Nonnull Long id, Token token) throws TweetServiceException {
        WebClient twitterClient = createTwitterClient(token, HttpMethod.DELETE.toString(), createDeleteUrlAddition(id));
        TwitterExtDeleteTweetResponse twitterExtDeleteTweetResponse = twitterClient.delete()
                .retrieve()
                .bodyToMono(TwitterExtDeleteTweetResponse.class).block();
        if(twitterExtDeleteTweetResponse == null || twitterExtDeleteTweetResponse.getData() == null || twitterExtDeleteTweetResponse.getData().getDeleted() == null) {
            throw new TweetServiceException("Internal error, tweeter deletion is returned null from external api");
        }
        this.tweetCacheRepository.deleteTweet(id);
        return twitterExtDeleteTweetResponse.getData().getDeleted();
    }

    public List<Tweet> listRecentTweets() {
        return tweetCacheRepository.listRecentTweets();
    }

    private static String createDeleteUrlAddition(Long twitterId) {
        return SPLITTER + twitterId;
    }

    @Nonnull
    private Tweet convertExternalResponseToTweet(@Nonnull TwitterExtCreateTweetResponse block) {
        return new Tweet(block.getData().getId(), block.getData().getText());
    }

    @Nonnull
    private WebClient createTwitterClient(@Nonnull Token token, String method, @Nonnull String urlAddition) throws TweetServiceException {
        try {
            String baseUrl = "https://api.twitter.com/2/".concat(TWEET_PATH).concat(urlAddition);
            return WebClient.builder()
                    .baseUrl(baseUrl)
                    .defaultHeader("Authorization", generateOAuthHeader(token.getConsumerKey(), token.getConsumerKeySecret(), token.getAccessToken(), token.getTokenSecret(), baseUrl, method))
                    .defaultHeader("Content-Type", "application/json")
                    .defaultHeader("Cookie", "guest_id=v1%3A172799158077332628")
                    .build();
        } catch (Exception e) {
            throw new TweetServiceException("Twitter-api client creation is failed", e);
        }
    }

    @Nonnull
    private WebClient createTwitterClient(@Nonnull Token token, String method) throws TweetServiceException {
        return createTwitterClient(token, method, Strings.EMPTY);
    }

    private String generateOAuthHeader(String consumerKey, String consumerSecret, String token, String tokenSecret, String baseUrl, String method) throws Exception {
        // Implement the OAuth 1.0 signature generation logic here
        // This is a simplified example; you would typically use a library to help with this.
        Map<String, String> oauthParams = new HashMap<>();
        oauthParams.put("oauth_consumer_key", consumerKey);
        oauthParams.put("oauth_token", token);
        oauthParams.put("oauth_signature_method", "HMAC-SHA1");
        oauthParams.put("oauth_timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        oauthParams.put("oauth_nonce", generateNonce());
        oauthParams.put("oauth_version", "1.0");

        // Create the base string and signature (omitted for brevity)
        String baseString = createBaseString(method, baseUrl, oauthParams); // Implement this method
        String signature = generateSignature(baseString, consumerSecret, tokenSecret); // Implement this method

        oauthParams.put("oauth_signature", URLEncoder.encode(signature, StandardCharsets.UTF_8));

        StringBuilder authHeader = new StringBuilder("OAuth ");
        oauthParams.forEach((key, value) -> authHeader.append(key).append("=\"").append(value).append("\", "));

        // Remove the last comma and space
        authHeader.setLength(authHeader.length() - 2);
        return authHeader.toString();
    }

    // Method to generate the OAuth signature
    public static String generateSignature(String baseString, String consumerSecret, String tokenSecret) throws Exception {
        String signingKey = URLEncoder.encode(consumerSecret, StandardCharsets.UTF_8) + "&" +
                URLEncoder.encode(tokenSecret, StandardCharsets.UTF_8);

        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec secretKeySpec = new SecretKeySpec(signingKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        mac.init(secretKeySpec);

        byte[] rawHmac = mac.doFinal(baseString.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(rawHmac);
    }

    // Method to create the base string
    private static String createBaseString(String method, String baseUrl, Map<String, String> oauthParams) {
        // Collect parameters and sort them
        SortedMap<String, String> sortedParams = new TreeMap<>(oauthParams);

        // Create the parameter string
        StringBuilder parameterString = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (parameterString.length() > 0) {
                parameterString.append("&");
            }
            parameterString.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        // Create the base string
        return method + "&" +
                URLEncoder.encode(baseUrl, StandardCharsets.UTF_8) + "&" + URLEncoder.encode(parameterString.toString(), StandardCharsets.UTF_8);
    }

    private static String generateNonce() {
        // Create a SecureRandom instance
        SecureRandom secureRandom = new SecureRandom();

        // Create a byte array to hold the random bytes
        byte[] nonceBytes = new byte[16]; // 16 bytes will give us 24 characters in Base64
        secureRandom.nextBytes(nonceBytes);

        // Encode the byte array to a Base64 string and remove any padding
        return Base64.getUrlEncoder().withoutPadding().encodeToString(nonceBytes);
    }
}
