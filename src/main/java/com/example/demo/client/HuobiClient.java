package com.example.demo.client;

import com.example.demo.model.HuobiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "huobi", url = "https://api.huobi.pro/market")
public interface HuobiClient {
    @GetMapping("/tickers")
    HuobiResponse getMarketTickers();
}