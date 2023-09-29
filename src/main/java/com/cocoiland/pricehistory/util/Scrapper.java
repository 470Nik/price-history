package com.cocoiland.pricehistory.util;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Scrapper {

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
}
