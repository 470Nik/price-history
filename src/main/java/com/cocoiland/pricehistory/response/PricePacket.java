package com.cocoiland.pricehistory.response;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PricePacket {
    @JsonIgnore
    private String productId;
    @JsonProperty("date")
    private Date date;
    @JsonProperty("price")
    private Double price;
}
