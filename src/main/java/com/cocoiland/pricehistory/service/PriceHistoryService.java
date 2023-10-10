package com.cocoiland.pricehistory.service;

import com.cocoiland.pricehistory.entity.ProductDetails;
import com.cocoiland.pricehistory.repository.ProductRepository;
import com.cocoiland.pricehistory.util.Scrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
public class PriceHistoryService implements PriceHistoryServiceInterface{
    @Autowired
    Scrapper scrapper;

    @Autowired
    ProductRepository productRepository;

    @Override
    public String getPrice() throws Exception {
        Optional<ProductDetails> productDetails = null;
        productDetails = productRepository.findById("1");
        if(productDetails.isPresent())
            return scrapper.getProductIdAndPriceFlipkartCom("https://dl.flipkart.com/s/IrlqW3NNNN").toString();
        return "nothing";
    }


}
