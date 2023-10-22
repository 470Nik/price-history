package com.cocoiland.pricehistory.response;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PricePacket {
    @JsonIgnore
    private String productId;
    @JsonProperty("date")
    private Timestamp date;
    @JsonProperty("price")
    private Float price;
}
