package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "wallets")
public class Wallet extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    @JsonBackReference // Prevent circular reference, will not be serialized
    private User user;

    // One-to-Many relationship with TransactionHistory
    @OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TransactionHistory> transactions = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal usdt;

    @Column(nullable = false)
    private BigDecimal btc;

    @Column(nullable = false)
    private BigDecimal eth;

    // Constructor
    public Wallet() {
        this.usdt = BigDecimal.ZERO;
        this.btc = BigDecimal.ZERO;
        this.eth = BigDecimal.ZERO;
    }

    // Getters and Setters
    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getUsdt() {
        return usdt;
    }

    public void setUsdt(BigDecimal usdt) {
        this.usdt = usdt;
        this.lastModifiedDate = LocalDateTime.now(); // Update timestamp on change
    }

    public BigDecimal getBtc() {
        return btc;
    }

    public void setBtc(BigDecimal btc) {
        this.btc = btc;
        this.lastModifiedDate = LocalDateTime.now(); // Update timestamp on change
    }

    public BigDecimal getEth() {
        return eth;
    }

    public void setEth(BigDecimal eth) {
        this.eth = eth;
        this.lastModifiedDate = LocalDateTime.now(); // Update timestamp on change
    }
}
