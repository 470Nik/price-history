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
    @JsonProperty(Constants.PRODUCT_ID)
    @NotEmpty(message = Constants.PRODUCT_ID + Constants.CANT_BE_EMPTY)
    @NotNull(message = Constants.PRODUCT_ID + Constants.CANT_BE_NULL)
    private String productId;

    @NotNull(message = Constants.FROM_DATE + Constants.CANT_BE_NULL)
    @JsonProperty(Constants.FROM_DATE)
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private LocalDate fromDate;

    @NotNull(message = Constants.TO_DATE + Constants.CANT_BE_NULL)
    @JsonProperty(Constants.TO_DATE)
    @JsonFormat(pattern = Constants.DATE_FORMAT)
    private LocalDate toDate;

}
