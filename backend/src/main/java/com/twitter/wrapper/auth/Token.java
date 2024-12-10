package com.twitter.wrapper.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    private String consumerKey;
    private String consumerKeySecret;
    private String accessToken;
    private String tokenSecret;
}
