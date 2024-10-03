package com.example.demo.repository;

import com.example.demo.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    // Custom query method to find all transactions for a specific user
    List<TransactionHistory> findByUser_UserId(Long userId);
    
    // Custom query method to find transactions by wallet ID
    List<TransactionHistory> findByWallet_WalletId(Long walletId);

    // Custom query method to find transactions by status
    List<TransactionHistory> findByStatus(String status);

    // Custom query method to find transactions by transaction type
    List<TransactionHistory> findByTransactionType(String transactionType);
    
    // Custom query method to find transactions within a specific date range
    List<TransactionHistory> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}
