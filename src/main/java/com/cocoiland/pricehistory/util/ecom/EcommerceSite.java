package com.cocoiland.pricehistory.util.ecom;

import com.cocoiland.pricehistory.dto.ProductIdAndLsp;
import com.cocoiland.pricehistory.dto.UserInputDetails;
import com.cocoiland.pricehistory.entity.ProductDetails;

import java.io.IOException;

public interface EcommerceSite {
    public ProductIdAndLsp findProductIdAndPrice() throws IOException;

    public ProductDetails getProductDetailsFromEcommerceSite() throws IOException;
}
