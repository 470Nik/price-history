package com.cocoiland.pricehistory.service;

import com.cocoiland.pricehistory.util.Scrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class PriceHistoryService implements PriceHistoryServiceInterface{
    @Autowired
    Scrapper scrapper;

    @Override
    public Double getPrice() throws Exception {
        String url = "https://www.flipkart.com/apple-2020-macbook-air-m1-8-gb-256-gb-ssd-mac-os-big-sur-mgn93hn-a/p/itm6b02c9a9d9d28?pid=COMFXEKMXWUMGPHW&lid=LSTCOMFXEKMXWUMGPHWKF7Y8O&marketplace=FLIPKART&store=6bo%2Fb5g&spotlightTagId=FkPickId_6bo%2Fb5g&srno=b_1_1&otracker=browse&fm=organic&iid=bf56dfd7-34a9-44eb-b29d-e888d0fdaa51.COMFXEKMXWUMGPHW.SEARCH&ppt=sp&ppn=productListView&ssid=nwqt0v6m400000001695985863547";
        return scrapper.getTodaysPriceHistory(url);
    }
}
