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

    // Method to find transactions by user ID and return DTOs
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
                // Calculate how much ETH is needed to sell based on the buy amount of USDT
                BigDecimal ethToSell = transaction.getBuyAmount().divide(ethUsdtPrice.getBidPrice(), 10, RoundingMode.HALF_UP);

                if (wallet.getEth().compareTo(ethToSell) >= 0) {
                    wallet.setEth(wallet.getEth().subtract(ethToSell));
                    wallet.setUsdt(wallet.getUsdt().add(transaction.getBuyAmount())); // Receive USDT from sale
                    res = performTransaction(transaction, wallet); // Save transaction record
                } else {
                    throw new IllegalArgumentException("Insufficient ETH balance.");
                }
                break;
    
            case USDT_TO_ETH:
                if (wallet.getUsdt().compareTo(transaction.getBuyAmount()) >= 0) {
                    wallet.setUsdt(wallet.getUsdt().subtract(transaction.getBuyAmount())); // Spend USDT
                    BigDecimal ethToAdd = transaction.getBuyAmount().divide(ethUsdtPrice.getAskPrice(), 10, RoundingMode.HALF_UP);
                    wallet.setEth(wallet.getEth().add(ethToAdd)); // Receive ETH from purchase
                    res = performTransaction(transaction, wallet); // Save transaction record
                } else {
                    throw new IllegalArgumentException("Insufficient USDT balance.");
                }
                break;
    
            case BTC_TO_USDT:
                // Calculate how much BTC is needed to sell based on the buy amount of USDT
                BigDecimal btcToSell = transaction.getBuyAmount().divide(btcUsdtPrice.getBidPrice(), 10, RoundingMode.HALF_UP);
                if (wallet.getBtc().compareTo(btcToSell) >= 0) {
                    wallet.setBtc(wallet.getBtc().subtract(btcToSell)); // Sell BTC
                    wallet.setUsdt(wallet.getUsdt().add(transaction.getBuyAmount())); // Receive USDT from sale
                    res = performTransaction(transaction, wallet); // Save transaction record
                } else {
                    throw new IllegalArgumentException("Insufficient BTC balance.");
                }
                break;
    
            case USDT_TO_BTC:
                if (wallet.getUsdt().compareTo(transaction.getBuyAmount()) >= 0) {
                    wallet.setUsdt(wallet.getUsdt().subtract(transaction.getBuyAmount())); // Spend USDT
                    BigDecimal btcToAdd = transaction.getBuyAmount().divide(btcUsdtPrice.getAskPrice(), 10, RoundingMode.HALF_UP);
                    wallet.setBtc(wallet.getBtc().add(btcToAdd)); // Receive BTC from purchase
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
