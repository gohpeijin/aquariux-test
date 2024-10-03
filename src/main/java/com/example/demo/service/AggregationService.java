package com.example.demo.service;

import com.example.demo.Enum.Source;
import com.example.demo.Enum.Symbol;
import com.example.demo.entity.AggregatedPrice;
import com.example.demo.mapper.AggregatedPriceMapper;
import com.example.demo.repository.AggregatedPriceRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.Binance;
import com.example.demo.model.Huobi;
import com.example.demo.model.HuobiResponse;

import com.example.demo.log.PriceLogger;

@Service
public class AggregationService {

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private final PriceLogger priceLogger;

    private final AggregatedPriceRepository aggregatedPriceRepository;

    private final AggregatedPriceMapper aggregatedPriceMapper;

    @Autowired
    public AggregationService(AggregatedPriceRepository aggregatedPriceRepository, AggregatedPriceMapper aggregatedPriceMapper) {
        this.aggregatedPriceRepository = aggregatedPriceRepository;
        this.aggregatedPriceMapper = aggregatedPriceMapper;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper(); // Initialize ObjectMapper
        this.priceLogger = new PriceLogger();
    }

    public List<AggregatedPrice> getLatestPricesForAllSymbols() {
        return aggregatedPriceRepository.findLatestPricesForAllSymbols();
    }

    public Optional<AggregatedPrice> getLatestAggregatedPriceBySymbol(String symbol) {
        return aggregatedPriceRepository.findFirstBySymbolOrderByCreatedDateDesc(symbol);
    }

    public List<AggregatedPrice> saveBestAggregatedPrice() {
        List<Huobi> huobiPrices = this.HuobiPairs();
        List<Binance> binancePrices = this.BinancePairs();
        // Map each Huobi symbol to the best price by finding the corresponding Binance price
        List<AggregatedPrice> entities = huobiPrices.stream()
                .map(HuobiPrice -> {
                    // Find corresponding Binance price by symbol
                    Optional<Binance> matchingBinancePrice = binancePrices.stream()
                            .filter(BinancePrice -> BinancePrice.getSymbol().equalsIgnoreCase(HuobiPrice.getSymbol()))
                            .findFirst();

                    // If a matching Binance price is found, compare prices
                    return matchingBinancePrice.map(BinancePrice -> comparePrices(HuobiPrice, BinancePrice, HuobiPrice.getSymbol()))
                            .orElse(null);  // Return null if no matching Binance price is found
                })
                .filter(bestPrice -> bestPrice != null)  // Filter out nulls
                .collect(Collectors.toList());  // Collect the best aggregated prices into a list

        aggregatedPriceRepository.saveAll(entities);
        // Log each set of prices
        priceLogger.logHuobiPrices(huobiPrices);
        priceLogger.logBinancePrices(binancePrices);
        priceLogger.logAggregatedPrices(entities);
        return entities;
    }


    public AggregatedPrice comparePrices(Huobi huobiPrice, Binance binancePrice, String symbol) {
        AggregatedPrice bestPrice = new AggregatedPrice();
        bestPrice.setSymbol(Symbol.fromString(symbol));

        // Ensure both symbols are the same
        if (huobiPrice.getSymbol().equalsIgnoreCase(symbol) && binancePrice.getSymbol().equalsIgnoreCase(symbol)) {
            // Compare bid prices (higher is better for selling)
            if (huobiPrice.getBid().compareTo(binancePrice.getBidPrice()) > 0) {
                bestPrice.setBidPrice(huobiPrice.getBid());
                bestPrice.setBidQty(huobiPrice.getBidSize());
                bestPrice.setBidSource(Source.HUOBI);
            } else {
                bestPrice.setBidPrice(binancePrice.getBidPrice());
                bestPrice.setBidQty(binancePrice.getBidQty());
                bestPrice.setBidSource(Source.BINANCE);
            }

            // Compare ask prices (lower is better for buying)
            if (huobiPrice.getAsk().compareTo(binancePrice.getAskPrice()) < 0) {
                bestPrice.setAskPrice(huobiPrice.getAsk());
                bestPrice.setAskQty(huobiPrice.getAskSize());
                bestPrice.setAskSource(Source.HUOBI);
            } else {
                bestPrice.setAskPrice(binancePrice.getAskPrice());
                bestPrice.setAskQty(binancePrice.getAskQty());
                bestPrice.setAskSource(Source.BINANCE);
            }
        }

        return bestPrice;
    }

    public List<Binance> BinancePairs() {
        List<Binance> all = this.BinanceCall();
        // Filtering based on symbol
        return all.stream()
                .filter(b -> b.getSymbol().equalsIgnoreCase(Symbol.ETHUSDT.name()) || b.getSymbol().equalsIgnoreCase(Symbol.BTCUSDT.name()))
                .collect(Collectors.toList());
    }

    public List<Huobi> HuobiPairs() {
        List<Huobi> all = this.HuobiCall();
        // Filtering based on symbol
        return all.stream()
                .filter(b -> b.getSymbol().equalsIgnoreCase(Symbol.ETHUSDT.name()) || b.getSymbol().equalsIgnoreCase(Symbol.BTCUSDT.name()))
                .collect(Collectors.toList());
    }

    public List<Binance> BinanceCall() {
        String url = "https://api.Binance.com/api/v3/ticker/bookTicker";

        // Make the API call and get the response as a String
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Convert the JSON response to a List of BinanceTicker
        try {
            return objectMapper.readValue(response.getBody(), new TypeReference<List<Binance>>() {
            });
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions as appropriate
            return null; // Or handle in a way that suits your application
        }
    }

    public List<Huobi> HuobiCall() {
        String url = "https://api.Huobi.pro/market/tickers"; // Replace with the correct API endpoint
        try {
            // Make the API call and map the response
            HuobiResponse response = restTemplate.getForObject(url, HuobiResponse.class);
            return response.getData(); // Return the list of Huobi objects
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle exceptions appropriately
        }
    }
}