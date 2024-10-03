package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Binance {
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
