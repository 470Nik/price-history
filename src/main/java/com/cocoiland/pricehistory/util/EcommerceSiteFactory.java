package com.cocoiland.pricehistory.util;

import com.cocoiland.pricehistory.dto.UserInputDetails;
import com.cocoiland.pricehistory.util.ecom.Amazon_in;
import com.cocoiland.pricehistory.util.ecom.EcommerceSite;
import com.cocoiland.pricehistory.util.ecom.Flipkart_in;

public class EcommerceSiteFactory {

    /**
     * This function return the Ecommerce Site based on userInput filed: platform
     * @param userInputDetails Analysed user input
     * @return EcommerceSite => e.g. Flipkart_in, Amazon_in, etc.
     */
    public static EcommerceSite getEcommerceSite(UserInputDetails userInputDetails) {
        switch (userInputDetails.getPlatform()) {
            case FLIPKART_COM:
                return new Flipkart_in(userInputDetails.getUrl());
            case AMAZON_IN:
                return new Amazon_in(userInputDetails.getUrl());
            default:
                throw new IllegalArgumentException("Unsupported shopping site");
        }

    }
}
