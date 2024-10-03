package com.example.demo.data;

import com.example.demo.DTO.TransactionHistoryDTO;
import com.example.demo.DTO.WalletDTO;
import com.example.demo.Enum.TransactionType;
import com.example.demo.entity.TransactionHistory;
import com.example.demo.entity.User;
import com.example.demo.entity.Wallet;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.WalletService;
import com.example.demo.service.TransactionHistoryService;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WalletService walletService;
    private final TransactionHistoryService transactionHistoryService;
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final ObjectMapper objectMapper; // Jackson ObjectMapper

    public DataInitializer(UserRepository userRepository, WalletService walletService, TransactionHistoryService transactionHistoryService, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.transactionHistoryService = transactionHistoryService;
        this.objectMapper = objectMapper;
       // Enable pretty printing
       this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Injecting initial user data
        User user = new User();
        user.setUsername("john");
        user.setEmail("john@gmail.com");
        user.setPassword("password");
        userRepository.save(user);

        // Injecting initial wallet data for the user
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setUsdt(BigDecimal.valueOf(50000));
        wallet.setBtc(BigDecimal.valueOf(0));
        wallet.setEth(BigDecimal.valueOf(0));
        walletService.createWallet(wallet);

        // Injecting initial transaction data for the user
        TransactionHistory transaction = new TransactionHistory();
        transaction.setUser(user);
        transaction.setWallet(wallet);
        transaction.setTransactionType(TransactionType.BTC_TO_USDT); // Example transaction type
        transaction.setSellAmount(new BigDecimal("0.5")); // Selling 0.5 BTC
        transaction.setBuyAmount(new BigDecimal("10000")); // Buying 10000 USDT
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setStatus("SUCCESS");

        // Save the transaction history (assuming you have a method for this)
        transactionHistoryService.createTransaction(transaction); // Assuming a save method exists

        System.out.println("Sample data has been injected.");

        System.out.println("Sample data retrieval.");
        // Retrieve and log the saved user and wallet data
        User savedUser = userRepository.findById(user.getUserId()).orElse(null);
        WalletDTO savedWallet = walletService.findByUserId(user.getUserId());

        // Correctly use the injected instance to call the method
        List<TransactionHistoryDTO> transactionHistoryDTO = transactionHistoryService.findByUserId(user.getUserId());

        // Convert to JSON and log
        logger.info("Sample User Data: {}", objectMapper.writeValueAsString(savedUser));
        logger.info("Sample Wallet Data: {}", objectMapper.writeValueAsString(savedWallet));
        logger.info("Transaction History DTO : {}", objectMapper.writeValueAsString(transactionHistoryDTO)); // Log transaction history

        System.out.println("Sample data has been injected and retrieved.");
    }
}
