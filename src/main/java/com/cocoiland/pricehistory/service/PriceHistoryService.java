package com.cocoiland.pricehistory.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.cocoiland.pricehistory.constants.Constants;
import com.cocoiland.pricehistory.dto.ProductIdAndPrice;
import com.cocoiland.pricehistory.dto.UserInputDetails;
import com.cocoiland.pricehistory.entity.ProductDetails;
import com.cocoiland.pricehistory.enums.EcommerceSite;
import com.cocoiland.pricehistory.util.Scrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PriceHistoryService implements PriceHistoryServiceInterface{
    @Autowired
    Scrapper scrapper;
    @Autowired
    ElasticsearchClient esClient;

    @Override
    public String getPrice() throws Exception {
        String userInput = "I https://dl.flipkart.com/s/IrlqW3NNNN please find it's price history";

        UserInputDetails userInputDetails = getUserInputDetails(userInput);
        ProductIdAndPrice productIdAndPrice = null;
        if(!userInputDetails.getIsUrlPresent()){
            //TODO: handle this
            System.out.println("Search the product using the input string");
            return "URL Not found";
        }
        else{
            productIdAndPrice = findProductIdAndPrice(userInputDetails);
        }

        if(productIdAndPrice == null) {
            //TODO: handle this
            return null;
        }

        ProductDetails productDetails = fetchProductDetailsFromES(productIdAndPrice.getPid());
        if(productDetails == null){ //If the product detail is not alredy present in ES => add it
            ProductDetails fetchedProductDetails = scrapper.getProductDetailsFromFlipkartCom(userInputDetails.getUrl());
            fetchedProductDetails.setCreatedAt(new Date());
            fetchedProductDetails.setCreatedBy(Constants.SYSTEM);
            //addProductDetailsToES(fetchedProductDetails)
        }
        //If the product details are outdated => update it.
        else if(productDetails.getUpdatedAt() == null || TimeUnit.MILLISECONDS.toDays(new Date().getTime() - productDetails.getUpdatedAt().getTime()) > Constants.UPDATE_INTERVAL){
            ProductDetails fetchedProductDetails = scrapper.getProductDetailsFromFlipkartCom(userInputDetails.getUrl());
            productDetails.setRating(fetchedProductDetails.getRating());
            productDetails.setName(fetchedProductDetails.getName());
            productDetails.setImageUrl(fetchedProductDetails.getImageUrl());
            productDetails.setUpdatedAt(new Date());
            productDetails.setUpdatedBy(Constants.SYSTEM);
            //updateProductDetailsToES(productDetails)
        }
        return productDetails.toString();//TODO: complete this later
    }

    private ProductDetails fetchProductDetailsFromES(String pid) throws IOException {
        SearchResponse<ProductDetails> searchResponse = esClient.search(s -> s
                        .index("product-details-alias") //TODO: replace this value with variable
                        .query(q -> q
                                .term(t -> t
                                        .field("pid") //TODO: replace this value with variable
                                        .value(v -> v.stringValue(pid))
                                )),
                ProductDetails.class);

        for (Hit<ProductDetails> hit: searchResponse.hits().hits()) {
            ProductDetails productDetails = hit.source();
            productDetails.setId(hit.id());
            return productDetails;
        }
//        SearchResponse<ProductDetails> searchResponse = esClient.search(s -> s
//                        .index("product-details-alias") //TODO: replace this value with variable
//                        .query(q -> q
//                                .term(t -> t
//                                        .field("pid") //TODO: replace this value with variable
//                                        .value(v -> v.stringValue(pid))
//                                ))
//                        .source(sb -> sb
//                                .fields(f -> f.add("_id"))) // Fetch the _id field
//                , ProductDetails.class);

//        SearchRequest sr = SearchRequest.of(r -> r
//                .index("product-details-alias")
//                .source(s -> s.fetch(true)));
//
//        System.out.println("Request: " + sr);
//
//        try {
//            var response = this.esClient.search(sr, Void.class);
//            response.hits().hits().forEach(hit -> {
//                System.out.println("ID: " + hit.id());
//            });
//        } catch (IOException ignored) {
//        }
        return null;
    }

    private UserInputDetails getUserInputDetails(String userInput) {
        UserInputDetails userInputDetails = new UserInputDetails();
        if(userInput == null) {
            //TODO: handle this
            return userInputDetails;
        }

        // Check if userInput contains any supported URL
        int startingIndex = -1;
        for (EcommerceSite ecommerceSite : EcommerceSite.values()) {
            startingIndex = userInput.indexOf(ecommerceSite.getUrl());
            if(startingIndex != -1){
                userInputDetails.setIsUrlPresent(true);
                userInputDetails.setEcommerceSite(ecommerceSite);
                userInputDetails.setUrl(cleanUrl(userInput, startingIndex));
                return userInputDetails;
            }
        }

        //No url found => Use the String
        userInputDetails.setIsUrlPresent(false);
        userInputDetails.setSearchText(userInput);
        return userInputDetails;
    }
    private String cleanUrl(String input, int startingIndex) {
        input = input.substring(startingIndex);
        int garbage_index = input.indexOf(" ");
        if(garbage_index == -1)
            return input;
        return input.substring(0, garbage_index);
    }

    private ProductIdAndPrice findProductIdAndPrice(UserInputDetails userInputDetails) throws IOException {

        switch (userInputDetails.getEcommerceSite()) {
            case FLIPKART_COM:
                return scrapper.getProductIdAndPriceFlipkartCom(userInputDetails.getUrl());
            case AMAZON_IN:
                System.out.println("Selected site: Amazon India");
                return null;
            default:
                return null;
        }
    }

}
