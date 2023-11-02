package com.cocoiland.pricehistory.response;


import com.cocoiland.pricehistory.constants.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class PricePacket {
    @JsonIgnore
    private String productId;
    @JsonProperty(Constants.DATE)
    private Date date;
    @JsonProperty(Constants.PRICE)
    private Double price;
}
