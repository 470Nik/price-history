package com.cocoiland.pricehistory.entity;

import com.cocoiland.pricehistory.enums.Platform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetails {
    private String id; //unique system identifier
    private String platform; //ecommerce site base url
    private String pid; //Ecommerce Site's product id
    private String name; //product_name
    private String description;
    private Float rating;
    private String imageUrl;
    private Date createdAt;
    private String createdBy;
    private Date updatedAt;
    private String updatedBy;

}