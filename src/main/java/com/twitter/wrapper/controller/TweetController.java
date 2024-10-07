package com.twitter.wrapper.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tweet")
public class TweetController {

    @PostMapping("/create")
    public String generateToken(String echo) {
        return echo;
    }
}
