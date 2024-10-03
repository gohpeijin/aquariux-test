package com.example.demo.service;

import com.example.demo.DTO.WalletDTO;
import com.example.demo.entity.Wallet;
import com.example.demo.mapper.WalletMapper;
import com.example.demo.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;

    public WalletService(WalletRepository walletRepository, WalletMapper walletMapper) {
        this.walletRepository = walletRepository;
        this.walletMapper = walletMapper;
    }

    // Method to create a new wallet
    @Transactional
    public Wallet createWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    // Method to get a wallet by ID
    @Transactional
    public Optional<Wallet> getWalletById(Long walletId) {
        return walletRepository.findById(walletId);
    }

    // Method to get all wallets
    @Transactional
    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }

    // Method to update a wallet
    @Transactional
    public Wallet updateWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    // Method to delete a wallet
    @Transactional
    public void deleteWallet(Long walletId) {
        walletRepository.deleteById(walletId);
    }

    // Method to find wallets by user ID
    @Transactional(readOnly = true)
    public WalletDTO findByUserId(Long userId) {
        Wallet wallet = walletRepository.findByUser_UserId(userId).orElse(null);

        if (wallet == null) {
            return null;
        }

        return walletMapper.toDTO(wallet);
    }
}
