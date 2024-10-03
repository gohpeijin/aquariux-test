package com.example.demo.DTO;

import com.example.demo.Enum.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryDTO {

    private Long userId; // Reference to the user ID
    private Long walletId; // Reference to the wallet ID
    private TransactionType transactionType;
    private BigDecimal buyAmount; // Amount being bought (e.g., USDT)
    private LocalDateTime transactionDate;
    private String status;
}