package com.example.demo.DTO;

import java.math.BigDecimal;

public class WalletDTO {

    private BigDecimal usdt;

    private BigDecimal btc;

    private BigDecimal eth;

    private Long userId;
    
    private String username;

    // Getters and Setters

    public BigDecimal getUsdt() {
        return usdt;
    }

    public void setUsdt(BigDecimal usdt) {
        this.usdt = usdt;
    }

    public BigDecimal getBtc() {
        return btc;
    }

    public void setBtc(BigDecimal btc) {
        this.btc = btc;
    }

    public BigDecimal getEth() {
        return eth;
    }

    public void setEth(BigDecimal eth) {
        this.eth = eth;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
