package com.example.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletDTO {

    private BigDecimal usdt;

    private BigDecimal btc;

    private BigDecimal eth;

    private Long userId;

    private Long walletId;
    
    private String username;
}
