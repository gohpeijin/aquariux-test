package com.example.demo.controller;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.DTO.WalletDTO;
import com.example.demo.entity.Wallet;
import com.example.demo.mapper.WalletMapper;
import com.example.demo.service.WalletService;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {


    private WalletService walletService;

    private WalletMapper walletMapper;

    public WalletController(WalletService walletService, WalletMapper walletMapper) {
        this.walletService = walletService;
        this.walletMapper = walletMapper;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<WalletDTO> getWalletByUserId(@PathVariable Long userId) {
        Wallet wallet = this.walletService.findByUserId(userId);
        return ResponseEntity.ok(walletMapper.toDTO( wallet) );
    }
}