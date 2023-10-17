package com.cocoiland.pricehistory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductIdAndLsp {
    private String pid; //product id
    private Double lsp; //last selling price
}
