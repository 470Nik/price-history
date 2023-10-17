package com.cocoiland.pricehistory.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.CreateResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.cocoiland.pricehistory.constants.Constants;
import com.cocoiland.pricehistory.dto.ProductIdAndLsp;
import com.cocoiland.pricehistory.dto.UserInputDetails;
import com.cocoiland.pricehistory.entity.ProductDetails;
import com.cocoiland.pricehistory.enums.Platform;
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

    /**
     * This function fetches the product data from web (based on ecommerce site) and Elasticsearch
     * and servers the user with that data.
     *
     * @return TODO: write documented comments
     * @throws IOException
     */
    @Override
    public String getProductDetails() throws IOException { //TODO: handle exceptions & write advisor
        String userInput = "I https://www.flipkart.com/fossil-grant-spor-analog-watch-men/p/itmf3zh3kuxnqkct?pid=WATET6SFGZW2EKVG&lid=LSTWATET6SFGZW2EKVGAYWSUV&marketplace=FLIPKART&fm=factBasedRecommendation%2FrecentlyViewed&iid=R%3Arv%3Bpt%3App%3Buid%3A5db15da5-6c8c-11ee-b154-8d7f909d8d6e%3B.WATET6SFGZW2EKVG&ppt=pp&ppn=pp&ssid=1lmvgg4l1s0000001697506057026&otracker=pp_reco_Recently%2BViewed_5_36.productCard.RECENTLY_VIEWED_FOSSIL%2BGRANT%2BSPOR%2BAnalog%2BWatch%2B%2B-%2BFor%2BMen_WATET6SFGZW2EKVG_factBasedRecommendation%2FrecentlyViewed_4&otracker1=pp_reco_PINNED_factBasedRecommendation%2FrecentlyViewed_Recently%2BViewed_DESKTOP_HORIZONTAL_productCard_cc_5_NA_view-all&cid=WATET6SFGZW2EKVG https://dl.flipkart.com/s/IrlqW3NNNN please find it's price history";
        UserInputDetails userInputDetails = getUserInputDetails(userInput);

        ProductIdAndLsp productIdAndLsp = null;

        if(!userInputDetails.getIsUrlPresent()){ //Checking if user input doesn't a valid URL => search for product using String.
            //TODO: handle this
            System.out.println("Search the product using the input string");
            return "URL Not found";
        }
        else{//If user input has URL =>
            productIdAndLsp = findProductIdAndPrice(userInputDetails);
        }

        if(productIdAndLsp == null) {
            //TODO: handle this
            return null;
        }

        ProductDetails productDetails = fetchProductDetailsFromES_using_pid_and_platform(productIdAndLsp.getPid(), userInputDetails.getPlatform());
        if(productDetails == null){ //If the product detail is not alredy present in ES => add it
            ProductDetails fetchedProductDetails = scrapper.getProductDetailsFromFlipkartCom(userInputDetails.getUrl());
            fetchedProductDetails.setPlatform(Platform.FLIPKART_COM.getUrl());
            fetchedProductDetails.setPid(productIdAndLsp.getPid());
            fetchedProductDetails.setId(fetchedProductDetails.getPlatform() + "_" + fetchedProductDetails.getPid());
            fetchedProductDetails.setDescription("No Description Available");
            fetchedProductDetails.setCreatedAt(new Date());
            fetchedProductDetails.setCreatedBy(Constants.SYSTEM);
            addProductDetailsToES(fetchedProductDetails);
            return fetchedProductDetails.toString();
        }
        //If the product details are outdated => update it.
        else if(productDetails.getUpdatedAt() == null || TimeUnit.MILLISECONDS.toDays(new Date().getTime() - productDetails.getUpdatedAt().getTime()) > Constants.UPDATE_INTERVAL){
//            ProductDetails fetchedProductDetails = scrapper.getProductDetailsFromFlipkartCom(userInputDetails.getUrl());
//            productDetails.setRating(fetchedProductDetails.getRating());
//            productDetails.setName(fetchedProductDetails.getName());
//            productDetails.setImageUrl(fetchedProductDetails.getImageUrl());
//            productDetails.setUpdatedAt(new Date());
//            productDetails.setUpdatedBy(Constants.SYSTEM);
//            //updateProductDetailsToES(productDetails)
        }

        return productDetails.toString();//TODO: complete this later
    }

    private void addProductDetailsToES(ProductDetails fetchedProductDetails) throws IOException {
        CreateResponse response = esClient.create(c -> c
                .index("product-details") //TODO: replace this value with variable
                .id(fetchedProductDetails.getId())
                .document(fetchedProductDetails));

        System.out.println("NikStatus: " + response.id() + " and: " + response.toString());
    }

//    @Override
//    public String getPrice() throws Exception {
//        String userInput = "I https://dl.flipkart.com/s/IrlqW3NNNN please find it's price history";
//
//        UserInputDetails userInputDetails = getUserInputDetails(userInput);
//        ProductIdAndLsp productIdAndLsp = null;
//        if(!userInputDetails.getIsUrlPresent()){
//            //TODO: handle this
//            System.out.println("Search the product using the input string");
//            return "URL Not found";
//        }
//        else{
//            productIdAndLsp = findProductIdAndPrice(userInputDetails);
//        }
//
//        if(productIdAndLsp == null) {
//            //TODO: handle this
//            return null;
//        }
//
//        ProductDetails productDetails = fetchProductDetailsFromES(productIdAndLsp.getPid());
//        if(productDetails == null){ //If the product detail is not alredy present in ES => add it
//            ProductDetails fetchedProductDetails = scrapper.getProductDetailsFromFlipkartCom(userInputDetails.getUrl());
//            fetchedProductDetails.setCreatedAt(new Date());
//            fetchedProductDetails.setCreatedBy(Constants.SYSTEM);
//            //addProductDetailsToES(fetchedProductDetails)
//        }
//        //If the product details are outdated => update it.
//        else if(productDetails.getUpdatedAt() == null || TimeUnit.MILLISECONDS.toDays(new Date().getTime() - productDetails.getUpdatedAt().getTime()) > Constants.UPDATE_INTERVAL){
//            ProductDetails fetchedProductDetails = scrapper.getProductDetailsFromFlipkartCom(userInputDetails.getUrl());
//            productDetails.setRating(fetchedProductDetails.getRating());
//            productDetails.setName(fetchedProductDetails.getName());
//            productDetails.setImageUrl(fetchedProductDetails.getImageUrl());
//            productDetails.setUpdatedAt(new Date());
//            productDetails.setUpdatedBy(Constants.SYSTEM);
//            //updateProductDetailsToES(productDetails)
//        }
//        return productDetails.toString();//TODO: complete this later
//    }



    private ProductDetails fetchProductDetailsFromES_using_pid_and_platform(String pid, Platform platform) throws IOException {
        System.out.println("Nikhil: " + pid + " and plat: " + platform.getUrl());

        SearchResponse<ProductDetails> searchResponse = esClient.search(s -> s
                        .index("product-details") //TODO: replace this value with variable
                        .query(q -> q
                                .bool(b -> b
                                        .must(m -> m
                                                .term(t -> t
                                                        .field("pid") //TODO: replace this value with variable
                                                        .value(v -> v.stringValue(pid))
                                                )
                                        )
                                        .must(m -> m
                                                .term(t -> t
                                                        .field("platform") //TODO: replace this value with variable
                                                        .value(v -> v.stringValue(platform.getUrl()))
                                                )
                                        )
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
//                                )),
//                ProductDetails.class);
//
//        for (Hit<ProductDetails> hit: searchResponse.hits().hits()) {
//            ProductDetails productDetails = hit.source();
//            productDetails.setId(hit.id());
//            return productDetails;
//        }
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
        for (Platform platform : Platform.values()) {
            startingIndex = userInput.indexOf(platform.getUrl());
            if(startingIndex != -1){
                userInputDetails.setIsUrlPresent(true);
                userInputDetails.setPlatform(platform);
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

    private ProductIdAndLsp findProductIdAndPrice(UserInputDetails userInputDetails) throws IOException {

        switch (userInputDetails.getPlatform()) {
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
