package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Binance {
    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("bidPrice")
    private double bidPrice;

    @JsonProperty("bidQty")
    private double bidQty;

    @JsonProperty("askPrice")
    private double askPrice;

    @JsonProperty("askQty")
    private double askQty;

    // Getters and Setters
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public double getBidQty() {
        return bidQty;
    }

    public void setBidQty(double bidQty) {
        this.bidQty = bidQty;
    }

    public double getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(double askPrice) {
        this.askPrice = askPrice;
    }

    public double getAskQty() {
        return askQty;
    }

    public void setAskQty(double askQty) {
        this.askQty = askQty;
    }
}
