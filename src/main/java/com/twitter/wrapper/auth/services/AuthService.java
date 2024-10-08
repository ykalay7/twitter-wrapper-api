package com.twitter.wrapper.auth.services;

import com.twitter.wrapper.auth.AuthenticationException;
import com.twitter.wrapper.auth.Token;
import com.twitter.wrapper.auth.TokenKey;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.logging.log4j.util.Strings;
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
    public TokenKey generateToken(@Nonnull String consumerKey, @Nonnull String consumerKeySecret,
                                  @Nonnull String accessToken, @Nonnull String tokenSecret) {
        TokenKey tokenKey = new TokenKey();
        tokenKey.setBearerToken(generateBearerToken());
        Token token = createToken(consumerKey, consumerKeySecret, accessToken, tokenSecret);
        tokenCache.put(tokenKey, token);
        return tokenKey;
    }

    @Nonnull
    public Token retrieveToken(@Nonnull String authHeader) throws AuthenticationException {
        TokenKey tokenKey = parseBearerHeader(authHeader);
        Token token = tokenCache.get(tokenKey);
        if(token == null) {
            throw new AuthenticationException("Token is not found internally, request is unauthorised");
        }
        return token;
    }

    public boolean authenticate(@Nonnull String authHeader) {
        TokenKey tokenKey = parseBearerHeader(authHeader);
        if(tokenKey == null) {
            return false;
        }
        return tokenCache.containsKey(tokenKey);
    }

    private String generateBearerToken() {
        byte[] randomBytes = new byte[24]; // 192 bits
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    @Nonnull
    private Token createToken(@Nonnull String consumerKey, @Nonnull String consumerKeySecret,
                              @Nonnull String accessToken, @Nonnull String tokenSecret) {
        return new Token(consumerKey, consumerKeySecret, accessToken, tokenSecret);
    }

    @Nullable
    private TokenKey parseBearerHeader(String authorizationHeader) {
        if(Strings.isEmpty(authorizationHeader)) {
            return null;
        }
        String[] splitHeader = authorizationHeader.split("Bearer ");
        if(splitHeader.length != 2) {
            return null;
        }
        return new TokenKey(splitHeader[1]);
    }
}
