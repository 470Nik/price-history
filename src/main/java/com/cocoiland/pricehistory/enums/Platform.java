package com.cocoiland.pricehistory.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Platform {
    FLIPKART_COM("flipkart.com"),
    AMAZON_IN("amazon.in");
    //can add many more

    private final String url;

    Platform(String url) {
        this.url = url;
    }

    @JsonValue
    public String getUrl() {
        return url;
    }
}