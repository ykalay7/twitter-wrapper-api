package com.twitter.wrapper.api.ext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TwitterExtCreateTweetResponse {
    private TweetData data;
}
