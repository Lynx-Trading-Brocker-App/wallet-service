package com.lynx.wallet_service.wallet.controller;

import com.lynx.wallet_service.wallet.dto.request.*;
import com.lynx.wallet_service.wallet.dto.response.*;
import com.lynx.wallet_service.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

import java.util.UUID;

@RestController
@RequestMapping("/funds")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @Value("${internal.api-key}")
    private String internalApiKey;

    private void validateKey(String key){
        if (!Objects.equals(internalApiKey, key)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid secret API key");
        }
    }

    // ─── User-facing endpoints (called via API Gateway) ───────────────────────

    @PostMapping("/deposit")
    public ResponseEntity<DepositResponse> deposit(
            @RequestHeader("X-INTERNAL-KEY") String key,
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody DepositRequest request) {
        validateKey(key);
        return ResponseEntity.ok(walletService.deposit(userId, request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawalResponse> withdraw(
            @RequestHeader("X-INTERNAL-KEY") String key,
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody WithdrawalRequest request) {
        validateKey(key);
        return ResponseEntity.ok(walletService.withdraw(userId, request));
    }

    @GetMapping
    public ResponseEntity<WalletResponse> getWallet(
            @RequestHeader("X-INTERNAL-KEY") String key,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam String currency) {
        validateKey(key);
        return ResponseEntity.ok(walletService.getWallet(userId, currency));
    }

    @GetMapping("/transactions")
    public ResponseEntity<TransactionHistoryResponse> getTransactionHistory(
            @RequestHeader("X-INTERNAL-KEY") String key,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam String currency,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        validateKey(key);
        return ResponseEntity.ok(walletService.getTransactionHistory(userId, currency, page, limit));
    }

    // ─── Internal endpoints (called by Order Service) ─────────────────────────

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveFunds(
            @RequestHeader("X-INTERNAL-KEY") String key,
            @Valid @RequestBody ReserveFundsRequest request) {
        validateKey(key);
        walletService.reserveFunds(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/release")
    public ResponseEntity<Void> releaseFunds(
            @RequestHeader("X-INTERNAL-KEY") String key,
            @Valid @RequestBody ReleaseFundsRequest request) {
        validateKey(key);
        walletService.releaseFunds(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/capture")
    public ResponseEntity<Void> captureFunds(
            @RequestHeader("X-INTERNAL-KEY") String key,
            @Valid @RequestBody CaptureFundsRequest request) {
        validateKey(key);
        walletService.captureFunds(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create-wallet")
    public ResponseEntity<Void> createWallet(
            @RequestHeader("X-INTERNAL-KEY") String key,
            @Valid @RequestBody CreateWalletRequest request){
        validateKey(key);
        walletService.createWalletforUser(request);
        return ResponseEntity.ok().build();
    }
}