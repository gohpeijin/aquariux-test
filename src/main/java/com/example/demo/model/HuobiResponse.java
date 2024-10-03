package com.example.demo.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class HuobiResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("ts")
    private long timestamp;

    @JsonProperty("data")
    private List<Huobi> data;
}
