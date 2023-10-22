package com.cocoiland.pricehistory.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.cocoiland.pricehistory.constants.Constants;
import com.cocoiland.pricehistory.dto.ProductIdAndLsp;
import com.cocoiland.pricehistory.dto.ProductPriceDto;
import com.cocoiland.pricehistory.dto.UserInputDetails;
import com.cocoiland.pricehistory.entity.ProductDetails;
import com.cocoiland.pricehistory.enums.Platform;
import com.cocoiland.pricehistory.request.ProductPriceHistoryRequest;
import com.cocoiland.pricehistory.response.PricePacket;
import com.cocoiland.pricehistory.response.ProductDetailsResponse;
import com.cocoiland.pricehistory.response.ProductPriceResponse;
import com.cocoiland.pricehistory.util.EcommerceSiteFactory;
import com.cocoiland.pricehistory.util.Scrapper;
import com.cocoiland.pricehistory.util.ecom.EcommerceSite;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PriceHistoryService implements PriceHistoryServiceInterface{

    @Autowired
    Scrapper scrapper;
    @Autowired
    ElasticsearchClient esClient;

    /**
     * This method fetches the product details from web (using scraper, based on ecommerce site) and Elasticsearch
     * and servers the user with that data.
     *
     * @return TODO: write documented comments
     * @throws IOException
     */
    @Override
    public ProductDetailsResponse getProductDetails(String userInput) throws IOException { //TODO: handle exceptions & write advisor
        ProductDetailsResponse productDetailsResponse = new ProductDetailsResponse();
        //Breaking down the user input
        UserInputDetails userInputDetails = getUserInputDetails(userInput);
        EcommerceSite ecommerceSite = null;
        ProductIdAndLsp productIdAndLsp = null;

        //If user's input doesn't have a valid URL => search for product using String.
        if(!userInputDetails.getIsUrlPresent()){
            //TODO: handle this in future => implement search on product name
            System.out.println("Search the product using the input string");
            throw new RuntimeException("No supported URL found! Please check your input");
        }
        //If user's input has URL
        else{
            //using factory method pattern for handling type of ecommerce site
            ecommerceSite = EcommerceSiteFactory.getEcommerceSite(userInputDetails);
            //fetching product's id and product's Last selling price
            productIdAndLsp = ecommerceSite.findProductIdAndPrice();
        }
        if(productIdAndLsp == null) {
            //TODO: handle this
            return null;
        }

//        ProductDetails productDetails = fetchProductDetailsFromES_using_pid_and_platform(productIdAndLsp.getPid(), userInputDetails.getPlatform());
//        //If the product detail is not already present in ES => add it
//        if(productDetails == null){
//            return scrapeProductDetailsAndAddToEs(ecommerceSite, productIdAndLsp, productDetailsResponse);
//        }
//        //If the product details are outdated => update it.
//        else if(productDetails.getUpdatedAt() == null || TimeUnit.MILLISECONDS.toDays(new Date().getTime() - productDetails.getUpdatedAt().getTime()) >= Constants.PRODUCT_DETAILS_UPDATE_INTERVAL_IN_DAYS){
//            return scrapeProductDetailsAndUpdateToES(ecommerceSite, productDetails, productIdAndLsp, productDetailsResponse);
//        }
//        BeanUtils.copyProperties(productDetails, productDetailsResponse);
//        if(productDetails.getLspUpdatedAt() == null || TimeUnit.MILLISECONDS.toHours(new Date().getTime() - productDetails.getLspUpdatedAt().getTime()) >= Constants.PRICE_UPDATE_INTERVAL_IN_HOURS) {
//            addProductCurrentPriceToES(productIdAndLsp.getLsp(), productDetails.getId());
//            if(productDetails.getLspUpdatedAt() != null)
//                System.out.println("Sent from site 5: " + TimeUnit.MILLISECONDS.toHours(new Date().getTime() - productDetails.getLspUpdatedAt().getTime()));
//            //TODO: update should aslo be done here => see this //update lspUpdate time to ES
//        }


        ProductDetails productDetails = fetchProductDetailsFromES_using_pid_and_platform(productIdAndLsp.getPid(), userInputDetails.getPlatform());
        System.out.println("Main fetched productdetails from ES: " + productDetails.toString());
        //If the product detail is not already present in ES => add it
        if(productDetails == null){
            return scrapeProductDetailsAndAddToEs(ecommerceSite, productIdAndLsp, productDetailsResponse);
        }
        //If the product details are outdated => update it.
        else if(productDetails.getUpdatedAt() == null || TimeUnit.MILLISECONDS.toDays(new Date().getTime() - productDetails.getUpdatedAt().getTime()) >= Constants.PRODUCT_DETAILS_UPDATE_INTERVAL_IN_DAYS){
            return scrapeProductDetailsAndUpdateToES(ecommerceSite, productDetails, productIdAndLsp, productDetailsResponse);
        }

        //if the product price has changed => update it.
        if(!Objects.equals(productDetails.getLsp(), productIdAndLsp.getLsp())) {
            //adding last selling price to the price history index
            boolean isPriceUpdated = addProductCurrentPriceToES(productIdAndLsp.getLsp(), productDetails.getId());
            //adding the updated price to product details index for tracking purpose
            if (isPriceUpdated) {
                HashMap jsonMap = new HashMap();
                jsonMap.put("lsp", productIdAndLsp.getLsp());

                UpdateResponse response = esClient.update(u -> u
                                .index("product-details") //TODO: replace this value with variable
                                .id(productDetails.getId())
                                .doc(jsonMap),
                        ProductDetails.class);
            }
        }
        System.out.println("Nikhil see this: " + productDetails.toString());
        BeanUtils.copyProperties(productDetails, productDetailsResponse);
        return productDetailsResponse;
    }

    private UserInputDetails getUserInputDetails(String userInput) {
        UserInputDetails userInputDetails = new UserInputDetails();
        if(userInput == null || userInput.isEmpty()) {
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
//                System.out.println("Nikhil Here's the url: " + cleanUrl(userInput, startingIndex));
                return userInputDetails;
            }
        }

        //No url found => Use the String
        userInputDetails.setIsUrlPresent(false);
        userInputDetails.setSearchText(userInput);
        return userInputDetails;
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
    private ProductDetails fetchProductDetailsFromES_using_pid_and_platform(String pid, Platform platform) throws IOException {
//        System.out.println("Nikhil: " + pid + " and plat: " + platform.getUrl());

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
        return null;
    }
    private ProductDetailsResponse scrapeProductDetailsAndAddToEs(EcommerceSite ecommerceSite, ProductIdAndLsp productIdAndLsp, ProductDetailsResponse productDetailsResponse) throws IOException {
        //scrape product details from ecommerceSite
        ProductDetails fetchedProductDetails = ecommerceSite.getProductDetailsFromEcommerceSite();
        System.out.println("fetched product details: " + fetchedProductDetails.toString());
        fetchedProductDetails.setPlatform(Platform.FLIPKART_COM.getUrl());
//          TODO: see if builder pattern can be used here
        fetchedProductDetails.setPid(productIdAndLsp.getPid());
        fetchedProductDetails.setId(fetchedProductDetails.getPlatform() + "_" + fetchedProductDetails.getPid());
        fetchedProductDetails.setDescription("No Description Available");
        fetchedProductDetails.setCreatedAt(new Date());
        fetchedProductDetails.setCreatedBy(Constants.SYSTEM);

        //adding last selling price to the price history index
        boolean isPriceUpdated = addProductCurrentPriceToES(productIdAndLsp.getLsp(), fetchedProductDetails.getId());
        //adding the updated price to product details index for tracking purpose
        if(isPriceUpdated){
            fetchedProductDetails.setLsp(productIdAndLsp.getLsp());
        }
        addProductDetailsToES(fetchedProductDetails);
        BeanUtils.copyProperties(fetchedProductDetails, productDetailsResponse);
        System.out.println("Nikhil: Sent from site 3");
        return productDetailsResponse;
    }
    private ProductDetailsResponse scrapeProductDetailsAndUpdateToES(EcommerceSite ecommerceSite, ProductDetails productDetails, ProductIdAndLsp productIdAndLsp,ProductDetailsResponse productDetailsResponse) throws IOException {
        ProductDetails fetchedProductDetails = ecommerceSite.getProductDetailsFromEcommerceSite();
        productDetails.setRating(fetchedProductDetails.getRating());
        productDetails.setName(fetchedProductDetails.getName());
        productDetails.setImageUrl(fetchedProductDetails.getImageUrl());
        productDetails.setUpdatedAt(new Date());
        productDetails.setUpdatedBy(Constants.SYSTEM);

        //if the product price has changed => update it.
        if(!Objects.equals(productDetails.getLsp(), productIdAndLsp.getLsp())) {
            //adding last selling price to the price history index
            boolean isPriceUpdated = addProductCurrentPriceToES(productIdAndLsp.getLsp(), productDetails.getId());
            //adding the updated price to product details index for tracking purpose
            System.out.println("Midway price update status: " + isPriceUpdated);
            if (isPriceUpdated)
                productDetails.setLsp(productIdAndLsp.getLsp());
        }
        System.out.println("Midway: " + productDetails.toString() + "\n and Midway: lsp curr: " + productIdAndLsp.toString());
        updateProductDetailsToES(productDetails);
        BeanUtils.copyProperties(productDetails, productDetailsResponse);
        return productDetailsResponse;
    }
    private void updateProductDetailsToES(ProductDetails fetchedProductDetails) throws IOException {
        UpdateResponse response = esClient.update(u -> u
                .index("product-details") //TODO: replace this value with variable
                .id(fetchedProductDetails.getId())
                .doc(fetchedProductDetails),
                ProductDetails.class);

        System.out.println("NikStatus update: " + response.id() + " and: " + response.toString());
    }

    private boolean addProductCurrentPriceToES(Double price, String product_id) throws IOException {
        long currentTimeInMillis = java.lang.System.currentTimeMillis();
        Date date = new Date(currentTimeInMillis);
        ProductPriceDto productPriceDto = ProductPriceDto.builder()
                .price(price)
                .productId(product_id)
                .date(date).build();

        CreateResponse response = esClient.create(c -> c
                .index("product-price-history") //TODO: replace this value with variable
                .id(product_id + "_" + currentTimeInMillis)
                .document(productPriceDto));

        System.out.println("NikStatus update price: " + response.id() + " and: " + response.result().equals(Result.Created));
        return response.result().equals(Result.Created);
    }

    private void addProductDetailsToES(ProductDetails fetchedProductDetails) throws IOException {
        CreateResponse response = esClient.create(c -> c
                .index("product-details") //TODO: replace this value with variable
                .id(fetchedProductDetails.getId())
                .document(fetchedProductDetails));

        System.out.println("NikStatus add: " + response.id() + " and: " + response.toString());
    }

    //If input is: "Hi I am flipkart.com/abcd how are you"
    //then, it will be converted to: "flipkart.com/abcd"
    private String cleanUrl(String input, int startingIndex) {
        input = input.substring(startingIndex);
        int garbage_index = input.indexOf(" ");
        if(garbage_index == -1)
            return input;
        return input.substring(0, garbage_index);
    }
    @Override
    public ProductPriceResponse getProductPriceHistory(ProductPriceHistoryRequest productPriceHistoryRequest) throws IOException {
        ProductPriceResponse productPriceResponse = new ProductPriceResponse();

        SearchResponse<PricePacket> search = esClient.search(s -> s
                        .index("product-price-history")
                        .query(q -> q
                                .bool(b -> b
                                        .must( m -> m
                                                .term(t -> t
                                                        .field("productId")
                                                        .value(v -> v.stringValue(productPriceHistoryRequest.getProductId()))))
                                        .filter(f -> f
                                        .range(r -> r
                                                .field("date")
                                                .gte(JsonData.of("2022-10-22"))
                                                .lte(JsonData.of("2023-10-22"))))))
                        .source(sc -> sc
                                .filter(sf -> sf
                                        .includes(List.of("date", "price"))))
                        .sort(p -> p
                                .field(FieldSort.of(f -> f
                                        .field("date")
                                        .order(SortOrder.Desc)))),
                PricePacket.class);

        List<PricePacket> list = new ArrayList<>();
        for (Hit<PricePacket> hit: search.hits().hits()) {
            list.add(hit.source());
        }
        productPriceResponse.setPriceHistory(list);
        return productPriceResponse;
    }
}
