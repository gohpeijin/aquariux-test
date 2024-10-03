package com.example.demo.service;

import com.example.demo.entity.Wallet;
import com.example.demo.repository.WalletRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
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

    @Transactional
    public boolean updateWallet(Wallet wallet) throws JsonProcessingException {
        // Check if the wallet exists first
        Optional<Wallet> existingWalletOpt = walletRepository.findById(wallet.getWalletId());
        
        if (existingWalletOpt.isPresent()) {
            // Update the existing wallet's properties
            Wallet existingWallet = existingWalletOpt.get();

            // Assuming Wallet class has appropriate setter methods
            existingWallet.setBtc(wallet.getBtc());
            existingWallet.setEth(wallet.getEth());
            existingWallet.setUsdt(wallet.getUsdt());
            existingWallet.setLastModifiedDate(LocalDateTime.now()); // Update the timestamp or any other necessary fields

            walletRepository.saveAndFlush(existingWallet);
            return true; // Indicate that the update was successful
        } else {
            // Handle the case where the wallet does not exist
            System.err.println("Wallet with ID " + wallet.getWalletId() + " does not exist.");
            return false; // Indicate that the update failed because the wallet does not exist
        }
    }
    

    // Method to delete a wallet
    @Transactional
    public void deleteWallet(Long walletId) {
        walletRepository.deleteById(walletId);
    }

    // Method to find wallets by user ID
    @Transactional(readOnly = true)
    public Wallet findByUserId(Long userId) {
        Wallet wallet = walletRepository.findByUser_UserId(userId).orElse(null);

        if (wallet == null) {
            return null;
        }

        return wallet;
    }
}
