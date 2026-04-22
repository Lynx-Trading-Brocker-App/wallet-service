package com.lynx.wallet_service.wallet.service;

import com.lynx.wallet_service.wallet.dto.request.*;
import com.lynx.wallet_service.wallet.dto.response.*;
import com.lynx.wallet_service.wallet.entity.Wallet;
import com.lynx.wallet_service.wallet.entity.WalletTransaction;
import com.lynx.wallet_service.wallet.entity.TransactionType;
import com.lynx.wallet_service.wallet.exception.InsufficientFundsException;
import com.lynx.wallet_service.wallet.exception.WalletNotFoundException;
import com.lynx.wallet_service.wallet.repository.WalletRepository;
import com.lynx.wallet_service.wallet.repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    // ─── User-facing operations ───────────────────────────────────────────────

    @Transactional
    public DepositResponse deposit(UUID userId, DepositRequest request) {
        Wallet wallet = walletRepository
                .findByUserIdAndCurrency(userId, request.getCurrency())
                .orElseGet(() -> createWallet(userId, request.getCurrency()));

        wallet.setAvailableBalance(wallet.getAvailableBalance().add(request.getAmount()));
        walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .walletId(wallet.getId())
                .transactionType(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .build();
        walletTransactionRepository.save(transaction);

        return DepositResponse.builder()
                .message("Deposit successful")
                .wallet(toWalletResponse(wallet))
                .transaction(toTransactionResponse(transaction))
                .build();
    }

    @Transactional
    public WithdrawalResponse withdraw(UUID userId, WithdrawalRequest request) {
        Wallet wallet = findWalletByUserAndCurrency(userId, request.getCurrency());

        if (wallet.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Not enough available balance to withdraw.");
        }

        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .walletId(wallet.getId())
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .build();
        walletTransactionRepository.save(transaction);

        return WithdrawalResponse.builder()
                .message("Withdrawal successful")
                .wallet(toWalletResponse(wallet))
                .transaction(toTransactionResponse(transaction))
                .build();
    }

    public WalletResponse getWallet(UUID userId, String currency) {
        Wallet wallet = findWalletByUserAndCurrency(userId, currency);
        return toWalletResponse(wallet);
    }

    // ─── Internal operations (called by Order Service) ────────────────────────

    @Transactional
    public void reserveFunds(ReserveFundsRequest request) {
        Wallet wallet = findWalletByUserAndCurrency(request.getUserId(), request.getCurrency());

        if (wallet.getAvailableBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException("Not enough available balance to place this order.");
        }

        wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(request.getAmount()));
        wallet.setReservedBalance(wallet.getReservedBalance().add(request.getAmount()));
        walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .walletId(wallet.getId())
                .transactionType(TransactionType.ORDER_HOLD)
                .amount(request.getAmount())
                .build();
        walletTransactionRepository.save(transaction);
    }

    @Transactional
    public void releaseFunds(ReleaseFundsRequest request) {
        Wallet wallet = findWalletByUserAndCurrency(request.getUserId(), request.getCurrency());

        wallet.setReservedBalance(wallet.getReservedBalance().subtract(request.getAmount()));
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(request.getAmount()));
        walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .walletId(wallet.getId())
                .transactionType(TransactionType.ORDER_RELEASE)
                .amount(request.getAmount())
                .build();
        walletTransactionRepository.save(transaction);
    }

    @Transactional
    public void captureFunds(CaptureFundsRequest request) {
        Wallet wallet = findWalletByUserAndCurrency(request.getUserId(), request.getCurrency());

        wallet.setReservedBalance(wallet.getReservedBalance().subtract(request.getReservedAmount()));

        BigDecimal remainder = request.getReservedAmount().subtract(request.getActualCost());
        if (remainder.compareTo(BigDecimal.ZERO) > 0) {
            wallet.setAvailableBalance(wallet.getAvailableBalance().add(remainder));
        }

        walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .walletId(wallet.getId())
                .transactionType(TransactionType.ORDER_HOLD)
                .amount(request.getActualCost())
                .build();
        walletTransactionRepository.save(transaction);
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private Wallet createWallet(UUID userId, String currency) {
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .currency(currency)
                .availableBalance(BigDecimal.ZERO)
                .reservedBalance(BigDecimal.ZERO)
                .build();
        return walletRepository.save(wallet);
    }

    private Wallet findWalletByUserAndCurrency(UUID userId, String currency) {
        return walletRepository.findByUserIdAndCurrency(userId, currency)
                .orElseThrow(() -> new WalletNotFoundException(
                        "Wallet not found for user " + userId + " with currency " + currency));
    }

    private WalletResponse toWalletResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .currency(wallet.getCurrency())
                .availableBalance(wallet.getAvailableBalance())
                .reservedBalance(wallet.getReservedBalance())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .isActive(wallet.isActive())
                .build();
    }

    private TransactionResponse toTransactionResponse(WalletTransaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .walletId(transaction.getWalletId())
                .referenceId(transaction.getReferenceId())
                .transactionType(transaction.getTransactionType().name())
                .amount(transaction.getAmount())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}