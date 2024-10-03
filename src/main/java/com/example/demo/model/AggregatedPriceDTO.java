package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregatedPriceDTO {
    @JsonProperty("bidSource")
    private String bidSource;

    @JsonProperty("askSource")
    private String askSource;

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("bidPrice")
    private BigDecimal bidPrice;

    @JsonProperty("bidQty")
    private BigDecimal bidQty;

    @JsonProperty("askPrice")
    private BigDecimal askPrice;

    @JsonProperty("askQty")
    private BigDecimal askQty;
}
