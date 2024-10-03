package com.example.demo.Enum;

import java.util.Arrays;

public enum Symbol {
    ETHUSDT,
    BTCUSDT;

    public static Symbol fromString(String symbolString) {
        for (Symbol symbol : Symbol.values()) {
            if (symbol.name().equalsIgnoreCase(symbolString)) {
                return symbol;
            }
        }
        throw new IllegalArgumentException("Invalid symbol: " + symbolString + ". Valid options are: " + Arrays.toString(Symbol.values()));
    }
}
