package com.cocoiland.pricehistory.service;

import com.cocoiland.pricehistory.exceptions.ServiceException;
import com.cocoiland.pricehistory.request.ProductPriceHistoryRequest;
import com.cocoiland.pricehistory.response.ProductDetailsResponse;
import com.cocoiland.pricehistory.response.ProductPriceResponse;

public interface PriceHistoryServiceInterface {

    ProductDetailsResponse getProductDetails(String input) throws ServiceException;

    ProductPriceResponse getProductPriceHistory(ProductPriceHistoryRequest productPriceHistoryRequest) throws ServiceException;

}
