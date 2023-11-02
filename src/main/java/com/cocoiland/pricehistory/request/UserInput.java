package com.cocoiland.pricehistory.request;

import com.cocoiland.pricehistory.constants.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class UserInput {
    @NotNull(message = Constants.INPUT + Constants.CANT_BE_NULL)
    @NotEmpty(message = Constants.INPUT + Constants.CANT_BE_EMPTY)
    @JsonProperty(Constants.INPUT)
    private String userInput;
}
