package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.AggregationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.DTO.WalletDTO;
import com.example.demo.service.WalletService;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private AggregationService aggregationService;

    private WalletService walletService;

    public WalletController(AggregationService aggregationService, WalletService walletService) {
        this.aggregationService = aggregationService;
        this.walletService = walletService;
    }
//
//
////    @GetMapping("Binance")
////    public List<Binance> getBinance() {
////        return aggregationService.BinanceCall();
////    }
////
////    @GetMapping("Huobi")
////    public List<Huobi> getHuobi() {
////        return aggregationService.HuobiCall();
////    }
//
//    @GetMapping("binanceBtcEth")
//    public List<Binance> BinanceTradingPairs() {
//        return aggregationService.fetchBinancePrices();
//    }
//
//    @GetMapping("huobiBtcEth")
//    public List<Huobi> HuobiTradingPairs() {
//        return aggregationService.fetchHuobiPrices();
//    }

    @GetMapping("/{userId}")
    public ResponseEntity<WalletDTO> getWalletByUserId(@PathVariable Long userId) {
        WalletDTO wallet = this.walletService.findByUserId(userId);
        return ResponseEntity.ok(wallet);
    }
}