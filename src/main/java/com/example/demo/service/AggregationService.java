package com.example.demo.service;

import com.example.demo.Enum.Source;
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
import com.example.demo.model.AggregatedPriceDTO;

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

    // Method to find and compare pairs from the two lists
    public List<AggregatedPriceDTO> saveBestAggregatedPrice() {
        List<Huobi> HuobiPrices = this.HuobiPairs();
        List<Binance> BinancePrices = this.BinancePairs();
        // Map each Huobi symbol to the best price by finding the corresponding Binance price
        List<AggregatedPriceDTO> res = HuobiPrices.stream()
                .map(HuobiPrice -> {
                    // Find corresponding Binance price by symbol
                    Optional<Binance> matchingBinancePrice = BinancePrices.stream()
                            .filter(BinancePrice -> BinancePrice.getSymbol().equalsIgnoreCase(HuobiPrice.getSymbol()))
                            .findFirst();

                    // If a matching Binance price is found, compare prices
                    return matchingBinancePrice.map(BinancePrice -> comparePrices(HuobiPrice, BinancePrice, HuobiPrice.getSymbol()))
                            .orElse(null);  // Return null if no matching Binance price is found
                })
                .filter(bestPrice -> bestPrice != null)  // Filter out nulls
                .collect(Collectors.toList());  // Collect the best aggregated prices into a list

        // Use the mapper to convert DTO to Entity
// Map each AggregatedPriceDTO to AggregatedPrice entity
        List<AggregatedPrice> entities = res.stream()
                .map(aggregatedPriceMapper::toEntity)
                .collect(Collectors.toList());
// Save all entities to the database
        aggregatedPriceRepository.saveAll(entities);
        // Log each set of prices
        priceLogger.logHuobiPrices(HuobiPrices);
        priceLogger.logBinancePrices(BinancePrices);
        priceLogger.logAggregatedPrices(res);
        return res;
    }


    public AggregatedPriceDTO comparePrices(Huobi HuobiPrice, Binance BinancePrice, String symbol) {
        AggregatedPriceDTO bestPrice = new AggregatedPriceDTO();
        bestPrice.setSymbol(symbol);

        // Ensure both symbols are the same
        if (HuobiPrice.getSymbol().equalsIgnoreCase(symbol) && BinancePrice.getSymbol().equalsIgnoreCase(symbol)) {
            // Compare bid prices (higher is better for selling)
            if (HuobiPrice.getBid() > BinancePrice.getBidPrice()) {
                bestPrice.setBidPrice(HuobiPrice.getBid());
                bestPrice.setBidQty(HuobiPrice.getBidSize());
                bestPrice.setBidSource(Source.HUOBI.name());
            } else {
                bestPrice.setBidPrice(BinancePrice.getBidPrice());
                bestPrice.setBidQty(BinancePrice.getBidQty());
                bestPrice.setBidSource(Source.BINANCE.name());
            }

            // Compare ask prices (lower is better for buying)
            if (HuobiPrice.getAsk() < BinancePrice.getAskPrice()) {
                bestPrice.setAskPrice(HuobiPrice.getAsk());
                bestPrice.setAskQty(HuobiPrice.getAskSize());
                bestPrice.setAskSource(Source.HUOBI.name());
            } else {
                bestPrice.setAskPrice(BinancePrice.getAskPrice());
                bestPrice.setAskQty(BinancePrice.getAskQty());
                bestPrice.setAskSource(Source.BINANCE.name());
            }
        }

        return bestPrice;
    }

    public List<Binance> BinancePairs() {
        List<Binance> all = this.BinanceCall();
        // Filtering based on symbol
        return all.stream()
                .filter(b -> b.getSymbol().equalsIgnoreCase("ethusdt") || b.getSymbol().equalsIgnoreCase("btcusdt"))
                .collect(Collectors.toList());
    }

    public List<Huobi> HuobiPairs() {
        List<Huobi> all = this.HuobiCall();
        // Filtering based on symbol
        return all.stream()
                .filter(b -> b.getSymbol().equalsIgnoreCase("ethusdt") || b.getSymbol().equalsIgnoreCase("btcusdt"))
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