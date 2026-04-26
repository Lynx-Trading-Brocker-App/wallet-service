package com.lynx.wallet_service.wallet.exception;

public class InsufficientReservedBalanceException extends RuntimeException {
    public InsufficientReservedBalanceException(String message) {
        super(message);
    }
}