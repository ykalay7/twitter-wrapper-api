package com.twitter.wrapper.dto;

public enum ExceptionCodes {
    UNKNOWN("001");

    private final String code;

    ExceptionCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
