package com.example.demo.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.DTO.WalletDTO;
import com.example.demo.entity.Wallet;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    @Mapping(source = "user.userId", target = "userId") 
    @Mapping(source = "user.username", target = "username") 
    @Mapping(source = "usdt", target = "usdt")
    @Mapping(source = "btc", target = "btc")
    @Mapping(source = "eth", target = "eth")
    WalletDTO toDTO(Wallet wallet);

    @Mapping(source = "userId", target = "user.userId") 
    @Mapping(source = "username", target = "user.username") 
    @Mapping(source = "usdt", target = "usdt")
    @Mapping(source = "btc", target = "btc")
    @Mapping(source = "eth", target = "eth")
    Wallet toEntity(WalletDTO walletDTO);
}