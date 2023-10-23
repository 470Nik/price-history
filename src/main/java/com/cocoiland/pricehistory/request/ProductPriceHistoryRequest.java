package com.cocoiland.pricehistory.request;

import com.cocoiland.pricehistory.constants.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonProperty(Constants.PRODUCT_ID)
    private String productId;

    @NotNull
    @JsonProperty(Constants.FROM_DATE)
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private LocalDate fromDate;

    @NotNull
    @JsonProperty(Constants.TO_DATE)
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private LocalDate toDate;

}
