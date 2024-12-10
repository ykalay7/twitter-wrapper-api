package com.twitter.wrapper.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TokenKey {
    private String bearerToken;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenKey tokenKey = (TokenKey) o;
        return Objects.equals(bearerToken, tokenKey.bearerToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bearerToken);
    }
}
