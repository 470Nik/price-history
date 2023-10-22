package com.cocoiland.pricehistory.service;

import com.cocoiland.pricehistory.request.ProductPriceHistoryRequest;
import com.cocoiland.pricehistory.response.ProductPriceResponse;

import java.io.IOException;

public interface PriceHistoryServiceInterface {

    String getProductDetails(String input) throws IOException;

    ProductPriceResponse getProductPriceHistory(ProductPriceHistoryRequest productPriceHistoryRequest) throws IOException;

}
