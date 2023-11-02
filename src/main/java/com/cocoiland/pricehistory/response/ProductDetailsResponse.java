package com.cocoiland.pricehistory.response;

import com.cocoiland.pricehistory.constants.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDetailsResponse {
    @JsonProperty(Constants.PRODUCT_ID)
    private String id;
    private String platform;
    private String name;
    private String description;
    private Float rating;
    @JsonProperty(Constants.IMAGE_URL)
    private String imageUrl;
}
