package com.twitter.wrapper.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenerateTokenRequest {
    @NotNull(message = "consumerKey must not be null")
    private String consumerKey;
    @NotNull(message = "consumerKeySecret must not be null")
    private String consumerKeySecret;
    @NotNull(message = "accessToken must not be null")
    private String accessToken;
    @NotNull(message = "tokenSecret must not be null")
    private String tokenSecret;
}
