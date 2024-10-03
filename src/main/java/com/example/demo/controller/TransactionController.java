package com.example.demo.controller;

import com.example.demo.entity.AggregatedPrice;
import com.example.demo.model.AggregatedPriceDTO;
import com.example.demo.service.AggregationService;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.DTO.TransactionHistoryDTO;
import com.example.demo.service.TransactionHistoryService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private TransactionHistoryService transactionHistoryService;
    private AggregationService aggregationService;

    public TransactionController(TransactionHistoryService transactionHistoryService, AggregationService aggregationService) {
        this.aggregationService = aggregationService;
        this.transactionHistoryService = transactionHistoryService;
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<TransactionHistoryDTO>> getTransactionHistoryByUserId(@PathVariable Long userId) {
        List<TransactionHistoryDTO> transactionHistory = this.transactionHistoryService.findByUserId(userId);
        return ResponseEntity.ok(transactionHistory);
    }

    @GetMapping("/price/get-latest-best-aggregated-price")
    public ResponseEntity<List<AggregatedPrice>> getLatestBestAggregatedPrice() {
        List<AggregatedPrice> latestPrices = aggregationService.getLatestPricesForAllSymbols();
        return latestPrices.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(latestPrices);
    }
}