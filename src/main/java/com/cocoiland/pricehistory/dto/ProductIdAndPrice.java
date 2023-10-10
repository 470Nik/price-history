package com.cocoiland.pricehistory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductIdAndPrice {
    private String pid;
    private Double price;
}
