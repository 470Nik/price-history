package com.cocoiland.pricehistory.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetailsResponse {
    @JsonProperty("product_id")
    private String id;
    private String platform;
    private String name;
    private String description;
    private Float rating;
    @JsonProperty("image_url")
    private String imageUrl;
}
