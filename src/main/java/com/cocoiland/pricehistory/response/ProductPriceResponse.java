package com.cocoiland.pricehistory.response;

import com.cocoiland.pricehistory.constants.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPriceResponse {
    @JsonProperty(Constants.PRICE_HISTORY)
    List<PricePacket> priceHistory;
}
