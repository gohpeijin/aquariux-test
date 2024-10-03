package com.example.demo.mapper;

import com.example.demo.DTO.*;
import com.example.demo.entity.*;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionHistoryMapper {

    // @Mapping(source = "transactionId", target = "transactionId")
    @Mapping(source = "user.userId", target = "userId") // Assuming User has a userId field
    @Mapping(source = "wallet.walletId", target = "walletId") // Assuming Wallet has a walletId field
    @Mapping(source = "transactionType", target = "transactionType")
    @Mapping(source = "buyAmount", target = "buyAmount")
    @Mapping(source = "transactionDate", target = "transactionDate")
    @Mapping(source = "status", target = "status")
    TransactionHistoryDTO toDTO(TransactionHistory transactionHistory);

    // @Mapping(source = "transactionId", target = "transactionId")
    @Mapping(source = "userId", target = "user.userId") // Assuming User has a userId field
    @Mapping(source = "walletId", target = "wallet.walletId") // Assuming Wallet has a walletId field
    @Mapping(source = "transactionType", target = "transactionType")
    @Mapping(source = "buyAmount", target = "buyAmount")
    @Mapping(source = "transactionDate", target = "transactionDate")
    @Mapping(source = "status", target = "status")
    TransactionHistory toEntity(TransactionHistoryDTO transactionHistoryDTO);
}
