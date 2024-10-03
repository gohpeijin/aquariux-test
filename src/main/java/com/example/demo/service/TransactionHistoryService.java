package com.example.demo.service;


import com.example.demo.entity.TransactionHistory;
import com.example.demo.repository.TransactionHistoryRepository;
import com.example.demo.mapper.TransactionHistoryMapper;
import com.example.demo.mapper.WalletMapper;

import com.example.demo.entity.AggregatedPrice;
import com.example.demo.entity.Wallet;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TransactionHistoryService {

    private final TransactionHistoryRepository transactionHistoryRepository;
    private final WalletService walletService;
    private final AggregationService aggregationService;

    public TransactionHistoryService(TransactionHistoryRepository transactionHistoryRepository, TransactionHistoryMapper transactionHistoryMapper, 
    WalletService walletService, AggregationService aggregationService, WalletMapper walletMapper) {
        this.transactionHistoryRepository = transactionHistoryRepository;

        this.walletService = walletService;
        this.aggregationService = aggregationService;
    }

    // Method to create a new transaction history entry
    @Transactional
    public TransactionHistory createTransaction(TransactionHistory transactionHistory) {
        // Validate amounts before saving
        if (transactionHistory.getBuyAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amounts must be non-negative.");
        }
        return transactionHistoryRepository.save(transactionHistory);
    }

    // Method to get a transaction history entry by ID
    @Transactional(readOnly = true)
    public Optional<TransactionHistory> getTransactionById(Long transactionId) {
        return transactionHistoryRepository.findById(transactionId);
    }

    // Method to get all transaction history entries
    @Transactional(readOnly = true)
    public List<TransactionHistory> getAllTransactions() {
        return transactionHistoryRepository.findAll();
    }

    // Method to update a transaction history entry
    @Transactional
    public TransactionHistory updateTransaction(TransactionHistory transactionHistory) {
        return transactionHistoryRepository.save(transactionHistory);
    }

    // Method to delete a transaction history entry
    @Transactional
    public void deleteTransaction(Long transactionId) {
        transactionHistoryRepository.deleteById(transactionId);
    }

    // Method to find transactions by user ID
    @Transactional(readOnly = true)
    public List<TransactionHistory> findByUserId(Long userId) {
        return transactionHistoryRepository.findByUser_UserId(userId);
    }

    // Method to find transactions by wallet ID
    @Transactional(readOnly = true)
    public List<TransactionHistory> findByWalletId(Long walletId) {
        return transactionHistoryRepository.findByWallet_WalletId(walletId);
    }

    // Method to find transactions by status
    @Transactional(readOnly = true)
    public List<TransactionHistory> findByStatus(String status) {
        return transactionHistoryRepository.findByStatus(status);
    }

    // Method to find transactions by transaction type
    @Transactional(readOnly = true)
    public List<TransactionHistory> findByTransactionType(String transactionType) {
        return transactionHistoryRepository.findByTransactionType(transactionType);
    }
    
    @Transactional
    public boolean convertToAnotherCrypto(TransactionHistory transaction) {
        boolean res = false;
    
        // Retrieve the wallet for the user
        Wallet wallet = walletService.findByUserId(transaction.getUser().getUserId());
        if (wallet == null) {
            throw new IllegalArgumentException("Wallet not found for user ID: " + transaction.getUser().getUserId());
        }
    
        List<AggregatedPrice> bestPrices = aggregationService.saveBestAggregatedPrice();
    
        AggregatedPrice ethUsdtPrice = bestPrices.stream()
            .filter(price -> "ethusdt".equalsIgnoreCase(price.getSymbol().toString())) // Case insensitive match
            .findFirst()
            .orElse(null); // Return null if not found
    
        AggregatedPrice btcUsdtPrice = bestPrices.stream()
            .filter(price -> "btcusdt".equalsIgnoreCase(price.getSymbol().toString())) // Case insensitive match
            .findFirst()
            .orElse(null); // Return null if not found
    
        switch (transaction.getTransactionType()) {
            case ETH_TO_USDT:
                // Buy amount is how many USDT the user wants to receive, calculate how much ETH to sell
                BigDecimal usdtToReceive = transaction.getBuyAmount();
                BigDecimal ethToSell = usdtToReceive.divide(ethUsdtPrice.getAskPrice(), 10, RoundingMode.HALF_UP);
    
                if (wallet.getEth().compareTo(ethToSell) >= 0) {
                    wallet.setEth(wallet.getEth().subtract(ethToSell)); // Sell ETH
                    wallet.setUsdt(wallet.getUsdt().add(usdtToReceive)); // Receive USDT
                    res = performTransaction(transaction, wallet); // Save transaction record
                } else {
                    throw new IllegalArgumentException("Insufficient ETH balance.");
                }
                break;
    
            case USDT_TO_ETH:
                // Buy amount is how many ETH the user wants to buy
                BigDecimal ethToBuy = transaction.getBuyAmount();
                BigDecimal usdtNeeded = ethToBuy.multiply(ethUsdtPrice.getBidPrice());
    
                if (wallet.getUsdt().compareTo(usdtNeeded) >= 0) {
                    wallet.setUsdt(wallet.getUsdt().subtract(usdtNeeded)); // Spend USDT
                    wallet.setEth(wallet.getEth().add(ethToBuy)); // Receive ETH
                    res = performTransaction(transaction, wallet); // Save transaction record
                } else {
                    throw new IllegalArgumentException("Insufficient USDT balance.");
                }
                break;
    
            case BTC_TO_USDT:
                // Buy amount is how many USDT the user wants to receive, calculate how much BTC to sell
                BigDecimal usdtToReceiveBTC = transaction.getBuyAmount();
                BigDecimal btcToSell = usdtToReceiveBTC.divide(btcUsdtPrice.getAskPrice(), 10, RoundingMode.HALF_UP);
    
                if (wallet.getBtc().compareTo(btcToSell) >= 0) {
                    wallet.setBtc(wallet.getBtc().subtract(btcToSell)); // Sell BTC
                    wallet.setUsdt(wallet.getUsdt().add(usdtToReceiveBTC)); // Receive USDT
                    res = performTransaction(transaction, wallet); // Save transaction record
                } else {
                    throw new IllegalArgumentException("Insufficient BTC balance.");
                }
                break;
    
            case USDT_TO_BTC:
                // Buy amount is how many BTC the user wants to buy
                BigDecimal btcToBuy = transaction.getBuyAmount();
                BigDecimal usdtNeededForBTC = btcToBuy.multiply(btcUsdtPrice.getBidPrice());
    
                if (wallet.getUsdt().compareTo(usdtNeededForBTC) >= 0) {
                    wallet.setUsdt(wallet.getUsdt().subtract(usdtNeededForBTC)); // Spend USDT
                    wallet.setBtc(wallet.getBtc().add(btcToBuy)); // Receive BTC
                    res = performTransaction(transaction, wallet); // Save transaction record
                } else {
                    throw new IllegalArgumentException("Insufficient USDT balance.");
                }
                break;
    
            default:
                throw new UnsupportedOperationException("Transaction type not supported: " + transaction.getTransactionType());
        }
    
        return res;
    }
    

    @Transactional
    public boolean performTransaction(TransactionHistory transaction, Wallet wallet) {
        try {
            boolean walletUpdated = walletService.updateWallet(wallet);
            if (!walletUpdated) {
                throw new RuntimeException("Failed to update wallet");
            }

            transaction = createTransaction(transaction);
            if (transaction == null) {
                throw new RuntimeException("Failed to save transaction");
            }
    
            return true; // Both operations succeeded
        } catch (Exception e) {
            // Handle the error (e.g., log it)
            System.err.println("Error occurred: " + e.getMessage());
            return false; // Return false if any operation failed
        }
    }
    
}
