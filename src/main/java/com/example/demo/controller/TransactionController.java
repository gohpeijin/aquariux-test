package com.example.demo.controller;

import com.example.demo.DTO.TransactionHistoryDTO;
import com.example.demo.entity.AggregatedPrice;
import com.example.demo.entity.TransactionHistory;
import com.example.demo.mapper.TransactionHistoryMapper;
import com.example.demo.service.AggregationService;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.TransactionHistoryService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private TransactionHistoryService transactionHistoryService;
    private AggregationService aggregationService;
    private final TransactionHistoryMapper transactionHistoryMapper;

    public TransactionController(TransactionHistoryService transactionHistoryService, AggregationService aggregationService, TransactionHistoryMapper transactionHistoryMapper) {
        this.aggregationService = aggregationService;
        this.transactionHistoryService = transactionHistoryService;
        this.transactionHistoryMapper = transactionHistoryMapper;
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<TransactionHistory>> getTransactionHistoryByUserId(@PathVariable Long userId) {
        List<TransactionHistory> transactionHistory = this.transactionHistoryService.findByUserId(userId);
        return ResponseEntity.ok(transactionHistory);
    }

    @PostMapping("/convert")
    public ResponseEntity<Boolean> convertToAnotherCrypto(@RequestBody TransactionHistoryDTO transactionHistoryDTO) {
        boolean res = this.transactionHistoryService.convertToAnotherCrypto(transactionHistoryMapper.toEntity(transactionHistoryDTO));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/get-latest-best-aggregated-price")
    public ResponseEntity<List<AggregatedPrice>> getLatestBestAggregatedPrice() {
        List<AggregatedPrice> latestPrices = aggregationService.getLatestPricesForAllSymbols();
        return latestPrices.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(latestPrices);
    }

    @GetMapping("/get-latest-best-aggregated-price/{symbol}")
    public ResponseEntity<AggregatedPrice> getLatestPriceBySymbol(@PathVariable String symbol) {
        Optional<AggregatedPrice> latestPrice = aggregationService.getLatestAggregatedPriceBySymbol(symbol);
        return latestPrice.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}