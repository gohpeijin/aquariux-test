package com.example.demo.service;

import com.example.demo.DTO.TransactionHistoryDTO;
import com.example.demo.entity.TransactionHistory;
import com.example.demo.repository.TransactionHistoryRepository;
import com.example.demo.mapper.TransactionHistoryMapper;
import com.example.demo.mapper.TransactionHistoryMapperImpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
public class TransactionHistoryService {

    private final TransactionHistoryRepository transactionHistoryRepository;
    private final TransactionHistoryMapper transactionHistoryMapper;

    public TransactionHistoryService(TransactionHistoryRepository transactionHistoryRepository) {
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.transactionHistoryMapper = new TransactionHistoryMapperImpl();
    }

    // Method to create a new transaction history entry
    @Transactional
    public TransactionHistory createTransaction(TransactionHistory transactionHistory) {
        // Validate amounts before saving
        if (transactionHistory.getSellAmount().compareTo(BigDecimal.ZERO) < 0 ||
            transactionHistory.getBuyAmount().compareTo(BigDecimal.ZERO) < 0) {
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
    public List<TransactionHistoryDTO> findByUserId(Long userId) {
        List<TransactionHistory> transactionHistories = transactionHistoryRepository.findByUser_UserId(userId);
        return transactionHistories.stream()
                .map(transactionHistoryMapper::toDTO)
                .collect(Collectors.toList());
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
}
