package com.twitter.wrapper.api.ext;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TweetData {
    private Long id;
    private List<Long> edit_history_tweet_ids;
    private String text;
}
