package com.twitter.wrapper.auth.services;

import com.twitter.wrapper.auth.Token;
import com.twitter.wrapper.auth.TokenKey;
import com.twitter.wrapper.dto.GenerateTokenRequest;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final SecureRandom secureRandom;
    private final Base64.Encoder base64Encoder;
    private final Map<TokenKey, Token> tokenCache;

    public AuthService() {
        secureRandom = new SecureRandom();
        base64Encoder = Base64.getUrlEncoder().withoutPadding();
        tokenCache = new ConcurrentHashMap<>();
    }

    @Nonnull
    public TokenKey generateToken(@Nonnull GenerateTokenRequest generateTokenRequest) {
        TokenKey tokenKey = new TokenKey();
        tokenKey.setBearerToken(generateBearerToken());
        Token token = createToken(generateTokenRequest);
        tokenCache.put(tokenKey, token);
        return tokenKey;
    }

    public boolean authenticate(TokenKey tokenKey) {
        return tokenCache.containsKey(tokenKey);
    }

    private String generateBearerToken() {
        byte[] randomBytes = new byte[24]; // 192 bits
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    @Nonnull
    private Token createToken(@Nonnull GenerateTokenRequest generateTokenRequest) {
        return new Token(generateTokenRequest.getConsumerKey(), generateTokenRequest.getConsumerKeySecret(), generateTokenRequest.getAccessToken(), generateTokenRequest.getTokenSecret());
    }
}
