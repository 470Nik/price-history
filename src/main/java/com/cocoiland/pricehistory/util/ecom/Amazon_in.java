package com.cocoiland.pricehistory.util.ecom;

import com.cocoiland.pricehistory.dto.ProductIdAndLsp;
import com.cocoiland.pricehistory.dto.UserInputDetails;
import com.cocoiland.pricehistory.entity.ProductDetails;

import java.io.IOException;

public class Amazon_in implements EcommerceSite {
    private String productUrl;

    public Amazon_in(String productUrl){
        //TODO: clean and repair the url
        this.productUrl = productUrl;
    }

    @Override
    public ProductIdAndLsp findProductIdAndPrice() throws IOException {
        return null;
    }

    @Override
    public ProductDetails getProductDetailsFromEcommerceSite() throws IOException {
        return null;
    }
}
