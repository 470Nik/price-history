package com.cocoiland.pricehistory.request;

import com.cocoiland.pricehistory.constants.Constants;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class UserInput {
    @NotNull(message = Constants.INPUT_NULL)
    @NotEmpty(message = Constants.INPUT_EMPTY)
    @JsonProperty(Constants.INPUT)
    private String userInput;
}
