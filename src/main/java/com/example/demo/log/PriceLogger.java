package com.example.demo.log;

import com.example.demo.entity.AggregatedPrice;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.model.Binance;
import com.example.demo.model.Huobi;

import java.util.List;

public class PriceLogger {

    private static final Logger logger = LoggerFactory.getLogger(PriceLogger.class);
    private static final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    // Method to log the Huobi prices
    public void logHuobiPrices(List<Huobi> HuobiPrices) {
        try {
            String json = objectMapper.writeValueAsString(HuobiPrices);
            logger.info("Huobi Prices: {}", json);
        } catch (Exception e) {
            logger.error("Error logging Huobi prices: {}", e.getMessage());
        }
    }

    // Method to log the Binance prices
    public void logBinancePrices(List<Binance> BinancePrices) {
        try {
            String json = objectMapper.writeValueAsString(BinancePrices);
            logger.info("Binance Prices: {}", json);
        } catch (Exception e) {
            logger.error("Error logging Binance prices: {}", e.getMessage());
        }
    }

    // Method to log the aggregated prices
    public void logAggregatedPrices(List<AggregatedPrice> aggregatedPrices) {
        try {
            String json = objectMapper.writeValueAsString(aggregatedPrices);
            logger.info("Aggregated Prices: {}", json);
        } catch (Exception e) {
            logger.error("Error logging aggregated prices: {}", e.getMessage());
        }
    }
}
