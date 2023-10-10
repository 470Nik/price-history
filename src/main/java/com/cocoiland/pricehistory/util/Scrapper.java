package com.cocoiland.pricehistory.util;

import com.cocoiland.pricehistory.dto.UserInputDetails;
import com.cocoiland.pricehistory.enums.EcommerceSite;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Scrapper {

    public void fun(String userInput){
        UserInputDetails userInputDetails = getUserInputDetails(userInput);
        switch (userInputDetails.getEcommerceSite()) {}

        switch (userInputDetails.getEcommerceSite()) {
            case AMAZON_IN:
                System.out.println("Selected site: Flipkart");
                break;
            case FLIPKART_COM:
                System.out.println("Selected site: Amazon India");
                break;
            default:
                System.out.println("Invalid selection");
        }
    }
    public UserInputDetails getUserInputDetails(String userInput){
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

    private String cleanUrl(String input, int startingIndex) {
        input = input.substring(startingIndex);
        int garbage_index = input.indexOf(" ");
        if(garbage_index == -1)
            return input;
        return input.substring(garbage_index);
    }
}
































































