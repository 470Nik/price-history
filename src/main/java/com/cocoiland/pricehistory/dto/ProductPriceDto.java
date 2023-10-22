package com.cocoiland.pricehistory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductPriceDto {
    private String productId;
    private Date date;
    private Double price;
}
