package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Huobi {

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("open")
    private BigDecimal open;

    @JsonProperty("high")
    private BigDecimal high;

    @JsonProperty("low")
    private BigDecimal low;

    @JsonProperty("close")
    private BigDecimal close;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("vol")
    private BigDecimal vol;

    @JsonProperty("count")
    private BigDecimal count;

    @JsonProperty("bid")
    private BigDecimal bid;

    @JsonProperty("bidSize")
    private BigDecimal bidSize;

    @JsonProperty("ask")
    private BigDecimal ask;

    @JsonProperty("askSize")
    private BigDecimal askSize;

}
