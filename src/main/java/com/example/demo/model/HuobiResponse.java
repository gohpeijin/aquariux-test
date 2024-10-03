package com.example.demo.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HuobiResponse {
    
    @JsonProperty("status")
    private String status;

    @JsonProperty("ts")
    private long timestamp;

    @JsonProperty("data")
    private List<Huobi> data;

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<Huobi> getData() {
        return data;
    }

    public void setData(List<Huobi> data) {
        this.data = data;
    }
}
