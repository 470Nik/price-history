package com.cocoiland.pricehistory.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInput {
    @NotNull(message = "User input can't be null")
    @NotEmpty(message = "User input can't be empty")
    @JsonProperty("input")
    private String userInput;
}