package com.cocoiland.pricehistory.util;

import com.cocoiland.pricehistory.dto.ProductIdAndLsp;
import com.cocoiland.pricehistory.entity.ProductDetails;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Scrapper {

//    public void fun(String userInput){
//        UserInputDetails userInputDetails = getUserInputDetails(userInput);
//        if(!userInputDetails.getIsUrlPresent()){
//            //TODO: handle this
//            System.out.println("Search the product using the input string");
//        }
//
//        switch (userInputDetails.getPlatform()) {
//            case FLIPKART_COM:
//                System.out.println("Selected site: Amazon India");
//                break;
//            case AMAZON_IN:
//                System.out.println("Selected site: Flipkart");
//                break;
//            default:
//                System.out.println("Invalid selection");
//        }
//    }
//    public UserInputDetails getUserInputDetails(String userInput){
//        UserInputDetails userInputDetails = new UserInputDetails();
//        if(userInput == null) {
//            //TODO: handle this
//            return userInputDetails;
//        }
//
//        // Check if userInput contains any supported URL
//        int startingIndex = -1;
//        for (Platform platform : Platform.values()) {
//            startingIndex = userInput.indexOf(platform.getUrl());
//            if(startingIndex != -1){
//                userInputDetails.setIsUrlPresent(true);
//                userInputDetails.setPlatform(platform);
//                userInputDetails.setUrl(cleanUrl(userInput, startingIndex));
//                return userInputDetails;
//            }
//        }
//
//        //No url found => Use the String
//        userInputDetails.setIsUrlPresent(false);
//        userInputDetails.setSearchText(userInput);
//        return userInputDetails;
//    }



    public Double getTodaysPriceHistory(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        Element element = document.getElementsByClass("_30jeq3 _16Jk6d").first();
        if (element != null && StringUtils.isNotEmpty(element.text())) {
            String price = element.text();
            price = price.substring(1);
            price = price.replaceAll(",", "");
            return Double.parseDouble(price);
        }
        return -1.0;
    }



    public ProductIdAndLsp getProductIdAndPriceFlipkartCom(String url) throws IOException {
        ProductIdAndLsp productIdAndLsp = new ProductIdAndLsp();
        if(url.length() > 0 && url.charAt(0) != 'h')//TODO: improve this line
            url = repairUrl(url);
        Document document = Jsoup.connect(url).get();

        //Scraping pid
        String pageUrl = document.location();
        String pid = extractProductIdFlipkart(pageUrl);
        productIdAndLsp.setPid(pid);

        //Scarping product price
        Element element = document.getElementsByClass("_30jeq3 _16Jk6d").first();
        if(element != null && StringUtils.isNotEmpty(element.text())) {
            String price = element.text();
            price = price.substring(1);
            price = price.replaceAll(",", "");
            productIdAndLsp.setLsp(Double.parseDouble(price));
            return productIdAndLsp;
        }
        return productIdAndLsp;
    }

    public ProductDetails getProductDetailsFromFlipkartCom(String url) throws IOException {
        if(url.length() > 0 && url.charAt(0) != 'h')
            url = repairUrl(url);
        ProductDetails  productDetails = new ProductDetails();
        Document document = Jsoup.connect(url).get();

        //Scarping product name
        Element nameElement = document.getElementsByClass("_1LJS6T _2whKao _1QoaG0").first();
        if(nameElement != null) {
            Element pElement = nameElement.select("p").first();
            if(pElement != null)
                productDetails.setName(pElement.text());
        }

        //Scraping product rating
        Element rating = document.getElementsByClass("_3LWZlK").first();
        if(rating != null){
            if(StringUtils.isNotEmpty(rating.text())){
                productDetails.setRating(Float.valueOf(rating.text()));
            }
        }


        //Scraping product image
        Element imgDiv = document.getElementsByClass("_3kidJX").first();
        if(imgDiv != null){
            Element img = imgDiv.select("img").first();
            if(img != null && StringUtils.isNotEmpty(img.attr("abs:src")))
                productDetails.setImageUrl(img.attr("abs:src"));
        }

        return productDetails;
    }
//COSCO kick Football - Size: 5 (Pack of 1)
    private String repairUrl(String url) {
        return "https://" + url;
    }

    private String extractProductIdFlipkart(String url){
        if(url!=null && url.contains("?pid=")){
            int startIndex = url.indexOf("?pid=");
            if(startIndex != -1){
                startIndex += 5;
                return url.substring(startIndex, startIndex+16);
            }
        }
        return "";
    }


//    private String cleanUrl(String input, int startingIndex) {
//        input = input.substring(startingIndex);
//        int garbage_index = input.indexOf(" ");
//        if(garbage_index == -1)
//            return input;
//        return input.substring(startingIndex, garbage_index-1);
//    }


}
































































