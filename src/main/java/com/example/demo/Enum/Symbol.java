package com.example.demo.Enum;

public enum Symbol {
    ETHUSDT,
    BTCUSDT;

    public static Symbol fromString(String symbolString) {
        for (Symbol symbol : Symbol.values()) {
            if (symbol.name().equalsIgnoreCase(symbolString)) {
                return symbol;
            }
        }
        throw new IllegalArgumentException("No enum constant for string: " + symbolString);
    }
}
