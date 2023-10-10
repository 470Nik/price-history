package com.cocoiland.pricehistory.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPrice {
    private String id;
    private Float price;
    private Date updatedAt;
    private String updatedBy;
}
