package com.example.demo.service;

import com.example.demo.Enum.Source;
import com.example.demo.Enum.Symbol;
import com.example.demo.client.BinanceClient;
import com.example.demo.client.HuobiClient;
import com.example.demo.entity.AggregatedPrice;
import com.example.demo.mapper.AggregatedPriceMapper;
import com.example.demo.repository.AggregatedPriceRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Binance;
import com.example.demo.model.Huobi;

import com.example.demo.log.PriceLogger;

@Service
public class AggregationService {

    private final BinanceClient binanceClient;
    private final HuobiClient huobiClient;

    private final PriceLogger priceLogger;

    private final AggregatedPriceRepository aggregatedPriceRepository;

    private final AggregatedPriceMapper aggregatedPriceMapper;

    @Autowired
    public AggregationService(AggregatedPriceRepository aggregatedPriceRepository,
                              AggregatedPriceMapper aggregatedPriceMapper,
                              BinanceClient binanceClient,
                              HuobiClient huobiClient) {
        this.aggregatedPriceRepository = aggregatedPriceRepository;
        this.aggregatedPriceMapper = aggregatedPriceMapper;
        this.binanceClient = binanceClient;
        this.huobiClient = huobiClient;
        this.priceLogger = new PriceLogger();
    }

    public List<AggregatedPrice> getLatestPricesForAllSymbols() {
        return aggregatedPriceRepository.findLatestPricesForAllSymbols();
    }

    public Optional<AggregatedPrice> getLatestAggregatedPriceBySymbol(String symbol) {
        return aggregatedPriceRepository.findFirstBySymbolOrderByCreatedDateDesc(Symbol.fromString(symbol));
    }

    public List<AggregatedPrice> saveBestAggregatedPrice() {
        List<Huobi> huobiPrices = this.fetchHuobiPrices();
        List<Binance> binancePrices = this.fetchBinancePrices();
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

    public List<Binance> fetchBinancePrices() {
        List<Binance> response = binanceClient.getBookTicker();
        // Filtering based on symbol
        return response.stream()
                .filter(b -> isSupportedSymbol(b.getSymbol()))
                .collect(Collectors.toList());
    }

    public List<Huobi> fetchHuobiPrices() {
        List<Huobi> response = huobiClient.getMarketTickers().getData();
        // Filtering based on symbol
        return response.stream()
                .filter(b -> isSupportedSymbol(b.getSymbol()))
                .collect(Collectors.toList());
    }

    private boolean isSupportedSymbol(String symbol) {
        return symbol.equalsIgnoreCase(Symbol.ETHUSDT.name()) || symbol.equalsIgnoreCase(Symbol.BTCUSDT.name());
    }

}