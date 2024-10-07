package com.twitter.wrapper.controller;

import com.twitter.wrapper.auth.TokenKey;
import com.twitter.wrapper.auth.services.AuthService;
import com.twitter.wrapper.dto.GenerateTokenRequest;
import com.twitter.wrapper.dto.GenerateTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService aUthService;

    @Autowired
    public AuthController(AuthService aUthService) {
        this.aUthService = aUthService;
    }

    @PostMapping("/createToken")
    public GenerateTokenResponse generateToken(GenerateTokenRequest generateTokenRequest) {
        TokenKey tokenKey = aUthService.generateToken(generateTokenRequest);
        return new GenerateTokenResponse(tokenKey.getBearerToken());
    }
}
