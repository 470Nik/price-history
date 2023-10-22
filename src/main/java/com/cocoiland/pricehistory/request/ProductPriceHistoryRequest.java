package com.cocoiland.pricehistory.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class ProductPriceHistoryRequest {
    @NotEmpty
    @NotNull
    @JsonProperty("product_id")
    private String productId;

    @NotNull
    @JsonProperty("from_date")
    private LocalDate fromDate;

    @JsonProperty("to_date")
    @NotNull
    private LocalDate toDate;

}
