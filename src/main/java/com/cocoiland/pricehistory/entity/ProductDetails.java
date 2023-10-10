package com.cocoiland.pricehistory.entity;

import com.cocoiland.pricehistory.enums.EcommerceSite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "products")
public class ProductDetails {
    @Id
    private String id;
    private EcommerceSite ecommerceSite;
    private String pid; //Ecommerce Site id
    private String name; //product_name
    private String description;
//    private Float rating;
    private Date createAt;
    private String createdBy;
    private Date updatedAt;
    private String updatedBy;
}
