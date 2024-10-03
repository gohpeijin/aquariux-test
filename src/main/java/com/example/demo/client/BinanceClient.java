package com.example.demo.client;

import com.example.demo.model.Binance;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "binance", url = "https://api.binance.com/api/v3")
public interface BinanceClient {
    @GetMapping("/ticker/bookTicker")
    List<Binance> getBookTicker();
}
