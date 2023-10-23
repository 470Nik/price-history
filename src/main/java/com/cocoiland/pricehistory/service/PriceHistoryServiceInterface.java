package com.cocoiland.pricehistory.service;

import com.cocoiland.pricehistory.request.ProductPriceHistoryRequest;
import com.cocoiland.pricehistory.response.ProductDetailsResponse;
import com.cocoiland.pricehistory.response.ProductPriceResponse;

import java.io.IOException;

public interface PriceHistoryServiceInterface {

    ProductDetailsResponse getProductDetails(String input) throws Exception;

    ProductPriceResponse getProductPriceHistory(ProductPriceHistoryRequest productPriceHistoryRequest) throws Exception;

}
