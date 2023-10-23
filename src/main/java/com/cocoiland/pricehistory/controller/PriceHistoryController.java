package com.cocoiland.pricehistory.controller;

import com.cocoiland.pricehistory.request.ProductPriceHistoryRequest;
import com.cocoiland.pricehistory.request.UserInput;
import com.cocoiland.pricehistory.service.PriceHistoryServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Validated
@RequestMapping("/v1")
public class PriceHistoryController {
    @Autowired
    PriceHistoryServiceInterface priceHistoryService;

    @GetMapping("/test")
    public String test() throws Exception {
        return "Application is live!";
    }


    @GetMapping(value = "product-details")
    public @ResponseBody
    ResponseEntity<Object> getProductDetails(@Validated @RequestBody UserInput eqChartData) throws Exception {
        return new ResponseEntity<>(priceHistoryService.getProductDetails(eqChartData.getUserInput()), HttpStatus.OK);
    }

    @GetMapping(value = "product-price-history")
    public @ResponseBody
    ResponseEntity<Object> getProductPriceHistory(@Validated @RequestBody ProductPriceHistoryRequest productPriceHistoryRequest) throws Exception {
        return new ResponseEntity<>(priceHistoryService.getProductPriceHistory(productPriceHistoryRequest), HttpStatus.OK);
    }
}
