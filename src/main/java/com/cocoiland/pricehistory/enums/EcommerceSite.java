package com.cocoiland.pricehistory.enums;

public enum EcommerceSite {
    FLIPKART_COM("flipkart.com"),
    AMAZON_IN("amazon.in");

    private final String url;

    EcommerceSite(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}