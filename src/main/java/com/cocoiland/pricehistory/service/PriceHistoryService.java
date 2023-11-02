package com.cocoiland.pricehistory.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.CreateResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.cocoiland.pricehistory.constants.Constants;
import com.cocoiland.pricehistory.dto.ProductIdAndLsp;
import com.cocoiland.pricehistory.dto.ProductPriceDto;
import com.cocoiland.pricehistory.dto.UserInputDetails;
import com.cocoiland.pricehistory.entity.ProductDetails;
import com.cocoiland.pricehistory.enums.Platform;
import com.cocoiland.pricehistory.exceptions.ESException;
import com.cocoiland.pricehistory.exceptions.EntityNotFoundException;
import com.cocoiland.pricehistory.exceptions.ServiceException;
import com.cocoiland.pricehistory.request.ProductPriceHistoryRequest;
import com.cocoiland.pricehistory.response.PricePacket;
import com.cocoiland.pricehistory.response.ProductDetailsResponse;
import com.cocoiland.pricehistory.response.ProductPriceResponse;
import com.cocoiland.pricehistory.util.EcommerceSiteFactory;
import com.cocoiland.pricehistory.util.ecom.EcommerceSite;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PriceHistoryService implements PriceHistoryServiceInterface{

    @Value("${es.product.details.index}")
    public String productDetailsIndex;
    @Value("${es.product.price.details.index}")
    public String productPriceIndex;

    @Autowired
    ElasticsearchClient esClient;

    /**
     * Fetches the product details from web (using scraper, based on ecommerce site) and Elasticsearch
     * and servers the user with that data.
     * The method is also responsible to add the price of the product to the ES index if there's any change
     *
     * @param userInput request
     * @return ProductDetailsResponse => details of product like name, rating, image url, etc
     * @throws ServiceException => throws generic service exception
     */
    @Override
    public ProductDetailsResponse getProductDetails(String userInput) throws ServiceException {
        try {
            ProductDetailsResponse productDetailsResponse = new ProductDetailsResponse();
            //Breaking down the user input
            UserInputDetails userInputDetails = getUserInputDetails(userInput);
            EcommerceSite ecommerceSite = null;
            ProductIdAndLsp productIdAndLsp = null;

            //If user's input doesn't have a valid URL => search for product using String.
            if (userInputDetails == null || !userInputDetails.getIsUrlPresent()) {
                //TODO: implement search on product details
                throw new EntityNotFoundException(Constants.COULD_NOT_FIND_PRODUCT_DETAILS);
            }
            //If user's input has URL
            else {
                //using factory method pattern for handling type of ecommerce site
                ecommerceSite = EcommerceSiteFactory.getEcommerceSite(userInputDetails);
                //fetching product's id and product's Last selling price
                productIdAndLsp = ecommerceSite.findProductIdAndPrice();
            }
            if (productIdAndLsp == null) {
                //TODO: improve this part
                throw new EntityNotFoundException(Constants.COULD_NOT_FIND_PRODUCT_DETAILS);
            }

            ProductDetails productDetails = fetchProductDetailsFromES_using_pid_and_platform(productIdAndLsp.getPid(), userInputDetails.getPlatform());
            //If the product detail is not already present in ES => add it
            if (productDetails == null) {
                return scrapeProductDetailsAndAddToEs(ecommerceSite, productIdAndLsp, productDetailsResponse);
            }
            //If the product details are outdated => update it.
            else if (productDetails.getUpdatedAt() == null || TimeUnit.MILLISECONDS.toDays(new Date().getTime() - productDetails.getUpdatedAt().getTime()) >= Constants.PRODUCT_DETAILS_UPDATE_INTERVAL_IN_DAYS) {

                return scrapeProductDetailsAndUpdateToES(ecommerceSite, productDetails, productIdAndLsp, productDetailsResponse);
            }

            //if the product price has changed => update it.
            if (!Objects.equals(productDetails.getLsp(), productIdAndLsp.getLsp())) {
                //adding last selling price to the price history index
                boolean isPriceUpdated = addProductCurrentPriceToES(productIdAndLsp.getLsp(), productDetails.getId());
                //adding the updated price to product details index for tracking purpose
                if (isPriceUpdated) {
                    HashMap<String, Double> jsonMap = new HashMap<String, Double>();
                    jsonMap.put(Constants.LSP, productIdAndLsp.getLsp());

                    UpdateResponse<ProductDetails> response = esClient.update(u -> u
                                    .index(productDetailsIndex)
                                    .id(productDetails.getId())
                                    .doc(jsonMap),
                            ProductDetails.class);
                }
            }
            BeanUtils.copyProperties(productDetails, productDetailsResponse);
            return productDetailsResponse;
        }
        catch (Exception e){
            throw new ServiceException(Constants.ERROR_FETCHING_PROCESSING_PRODUCT_DETAILS, e);
        }
    }

    /**
     * Breaks down the user input to extract url, platform, etc.
     *
     * @param userInput user's request
     * @return UserInputDetails => extracted data from userInput
     */
    private UserInputDetails getUserInputDetails(String userInput) {
        UserInputDetails userInputDetails = new UserInputDetails();
        if(userInput == null || userInput.isEmpty()) {
            return null;
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

    /**
     * Fetches the product details from Elasticsearch based on pid and type of platform
     *
     * @param pid product id
     * @param platform type of platform (e.g. flipkart, amazon, etc)
     * @return ProductDetails => details of product like name, rating, image url, etc
     * @throws ESException => throws ES exception
     */
    private ProductDetails fetchProductDetailsFromES_using_pid_and_platform(String pid, Platform platform) throws ESException {
        try {
            SearchResponse<ProductDetails> searchResponse = esClient.search(s -> s
                            .index(productDetailsIndex)
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m
                                                    .term(t -> t
                                                            .field(Constants.PID)
                                                            .value(v -> v.stringValue(pid))))
                                            .must(m -> m
                                                    .term(t -> t
                                                            .field(Constants.PLATFORM)
                                                            .value(v -> v.stringValue(platform.getUrl())))))),
                    ProductDetails.class);

            for (Hit<ProductDetails> hit : searchResponse.hits().hits()) {
                ProductDetails productDetails = hit.source();
                productDetails.setId(hit.id());
                return productDetails;
            }
            return null;
        }
        catch (Exception e){
            throw new ESException(Constants.ERROR_FETCHING_PRODUCT_DETAILS, e);
        }
    }

    /**
     * Fetches the product details from Web based on ecommerce website type and pid
     * and adds it to ES
     *
     * @param ecommerceSite EcommerceSite implementation class (flipkart, amazon, etc.)
     * @param productIdAndLsp product id and it's last selling price
     * @param productDetailsResponse partial product details response
     * @return ProductDetailsResponse => details of product like name, rating, image url, etc
     * @throws Exception => throws generic Exception
     */
    private ProductDetailsResponse scrapeProductDetailsAndAddToEs(EcommerceSite ecommerceSite, ProductIdAndLsp productIdAndLsp,
                                                                  ProductDetailsResponse productDetailsResponse) throws Exception {
        //scrape product details from ecommerceSite
        ProductDetails fetchedProductDetails = ecommerceSite.getProductDetailsFromEcommerceSite();
        fetchedProductDetails.setPlatform(Platform.FLIPKART_COM.getUrl());
        fetchedProductDetails.setPid(productIdAndLsp.getPid());
        fetchedProductDetails.setId(fetchedProductDetails.getPlatform() + Constants.UNDERSCORE + fetchedProductDetails.getPid());
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
        return productDetailsResponse;
    }

    /**
     * Fetches the product details from Web based on ecommerce website type and pid
     * and updates it to ES
     *
     * @param ecommerceSite EcommerceSite implementation class (flipkart, amazon, etc.)
     * @param productDetails old product details
     * @param productIdAndLsp product id and it's last selling price
     * @param productDetailsResponse partial product details response
     * @return ProductDetailsResponse => details of product like name, rating, image url, etc
     * @throws Exception => throws generic Exception
     */
    private ProductDetailsResponse scrapeProductDetailsAndUpdateToES(EcommerceSite ecommerceSite, ProductDetails productDetails,
                                                                     ProductIdAndLsp productIdAndLsp,
                                                                     ProductDetailsResponse productDetailsResponse) throws Exception {
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
            if (isPriceUpdated)
                productDetails.setLsp(productIdAndLsp.getLsp());
        }
        updateProductDetailsToES(productDetails);
        BeanUtils.copyProperties(productDetails, productDetailsResponse);
        return productDetailsResponse;
    }

    /**
     * Updates the product details to ES
     *
     * @param fetchedProductDetails scraped product details
     * @return boolean => returns if product details updated successfully to ES
     * @throws ESException => throws ESException
     */
    private boolean updateProductDetailsToES(ProductDetails fetchedProductDetails) throws ESException {
        try {
            UpdateResponse<ProductDetails> response = esClient.update(u -> u
                            .index(productDetailsIndex)
                            .id(fetchedProductDetails.getId())
                            .doc(fetchedProductDetails),
                    ProductDetails.class);
            return response.result().equals(Result.Updated);
        }catch (Exception e){
            throw new ESException(Constants.ERROR_UPDATING_PRODUCT_DETAILS_TO_ES, e);
        }
    }

    /**
     * Adds the product price details to ES
     *
     * @return boolean => returns if product price document has been successfully added to ES
     * @throws ESException => throws ES exception
     */
    private boolean addProductCurrentPriceToES(Double price, String product_id) throws ESException {
        try {
            long currentTimeInMillis = java.lang.System.currentTimeMillis();
            Date date = new Date(currentTimeInMillis);
            ProductPriceDto productPriceDto = ProductPriceDto.builder()
                    .price(price)
                    .productId(product_id)
                    .date(date).build();

            CreateResponse response = esClient.create(c -> c
                    .index(productPriceIndex)
                    .id(product_id + Constants.UNDERSCORE + currentTimeInMillis)
                    .document(productPriceDto));

            return response.result().equals(Result.Created);
        }catch (Exception e){
            throw new ESException(Constants.ERROR_ADDING_PRODUCT_PRICE_DETAILS_TO_ES, e);
        }
    }

    /**
     * Adds the product details to ES
     *
     * @param fetchedProductDetails scraped product details
     * @return boolean => returns true if product details document has been successfully added to ES
     * @throws ESException => throws ES exception
     */
    private boolean addProductDetailsToES(ProductDetails fetchedProductDetails) throws ESException {
        try {
            CreateResponse response = esClient.create(c -> c
                    .index(productDetailsIndex)
                    .id(fetchedProductDetails.getId())
                    .document(fetchedProductDetails));
            return response.result().equals(Result.Created);
        }catch (Exception e){
            throw new ESException(Constants.ERROR_ADDING_PRODUCT_DETAILS_TO_ES, e);
        }
    }


    /**
     * Cleans the input url. i.e. removes any unwanted text, before and after the url
     * If input is: "Hi flipkart.com/abcd bye"
     * then, it will be converted to: "flipkart.com/abcd"
     *
     * @param input part of user input
     * @param startingIndex starting index of the product url in the input string
     * @return String => clean url
     */
    private String cleanUrl(String input, int startingIndex) {
        input = input.substring(startingIndex);
        int garbage_index = input.indexOf(Constants.SPACE);
        if(garbage_index == -1)
            return input;
        return input.substring(0, garbage_index);
    }

    /**
     * Fetches the product price details from Elasticsearch
     * and servers the user with that data.
     *
     * @return ProductPriceResponse => list of objects containing product price & date
     * @throws ServiceException => throws ServiceException
     */
    @Override
    public ProductPriceResponse getProductPriceHistory(ProductPriceHistoryRequest productPriceHistoryRequest) throws ServiceException {
        ProductPriceResponse productPriceResponse = new ProductPriceResponse();
        try {
            SearchResponse<PricePacket> search = getProductPriceHistoryFromES(productPriceHistoryRequest);
            if (search != null) {
                List<PricePacket> list = new ArrayList<>();
                for (Hit<PricePacket> hit : search.hits().hits()) {
                    list.add(hit.source());
                }
                productPriceResponse.setPriceHistory(list);
            }
        }
        catch (Exception e) {
            throw new ServiceException(Constants.ERROR_FETCHING_PRODUCT_PRICE_HISTORY_DATA, e);
        }

        return productPriceResponse;
    }

    /**
     * Queries ES for the product price data
     *
     * @param productPriceHistoryRequest user's request to fetch product's price history
     * @return SearchResponse<PricePacket> => SearchResponse from elasticsearch query
     * @throws ESException => throws ES exception
     */
    private SearchResponse<PricePacket> getProductPriceHistoryFromES(ProductPriceHistoryRequest productPriceHistoryRequest) throws ESException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);
            SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder()
                    .index(productPriceIndex)
                    .query(q -> q
                            .bool(b -> b
                                    .must(m -> m
                                            .term(t -> t
                                                    .field(Constants.PRODUCTID)
                                                    .value(v -> v.stringValue(productPriceHistoryRequest.getProductId()))))
                                    .filter(f -> f
                                            .range(r -> r
                                                    .field(Constants.DATE)
                                                    .gte(JsonData.of(formatter.format(productPriceHistoryRequest.getFromDate())))
                                                    .lte(JsonData.of(formatter.format(productPriceHistoryRequest.getToDate())))))))
                    .source(sc -> sc
                            .filter(sf -> sf
                                    .includes(List.of(Constants.DATE, Constants.PRICE))))
                    .sort(p -> p
                            .field(FieldSort.of(f -> f
                                    .field(Constants.DATE)
                                    .order(SortOrder.Desc))));

            SearchRequest searchRequest = searchRequestBuilder.build();
            SearchResponse<PricePacket> search = esClient.search(searchRequest, PricePacket.class);
            return search;
        }
        catch (Exception e){
            throw new ESException(Constants.ERROR_FETCHING_PRICE_HISTORY_FROM_ES, e);
        }
    }
}
