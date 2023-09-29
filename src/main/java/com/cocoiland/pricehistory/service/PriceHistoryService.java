package com.cocoiland.pricehistory.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PriceHistoryService implements PriceHistoryServiceInterface{

    @Override
    public Double getPrice() {
        return 0.0;
    }
}
