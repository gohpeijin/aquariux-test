package com.example.demo.DTO;

import com.example.demo.Enum.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionHistoryDTO {

    private Long transactionId;
    private Long userId; // Reference to the user ID
    private Long walletId; // Reference to the wallet ID
    private TransactionType transactionType;
    private BigDecimal sellAmount; // Amount being sold (e.g., BTC)
    private BigDecimal buyAmount; // Amount being bought (e.g., USDT)
    private LocalDateTime transactionDate;
    private String status;

    // Default constructor
    public TransactionHistoryDTO() {
    }

    // Getters and Setters
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getSellAmount() {
        return sellAmount;
    }

    public void setSellAmount(BigDecimal sellAmount) {
        this.sellAmount = sellAmount;
    }

    public BigDecimal getBuyAmount() {
        return buyAmount;
    }

    public void setBuyAmount(BigDecimal buyAmount) {
        this.buyAmount = buyAmount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}