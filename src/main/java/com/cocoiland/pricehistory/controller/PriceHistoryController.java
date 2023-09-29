package com.cocoiland.pricehistory.controller;

import com.cocoiland.pricehistory.service.PriceHistoryServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class PriceHistoryController {
    @Autowired
    PriceHistoryServiceInterface priceHistoryService;

    @GetMapping("/test")
    public Double test() throws Exception {
        return priceHistoryService.getPrice();
    }
}
