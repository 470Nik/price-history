package com.cocoiland.pricehistory.dto;

import com.cocoiland.pricehistory.enums.Platform;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInputDetails {
    private Boolean isUrlPresent;
    private Platform platform;
    private String url;
    private String searchText;
}
