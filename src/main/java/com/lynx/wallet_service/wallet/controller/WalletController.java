package com.lynx.wallet_service.wallet.controller;

import com.lynx.wallet_service.wallet.dto.request.*;
import com.lynx.wallet_service.wallet.dto.response.*;
import com.lynx.wallet_service.wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/funds")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    // ─── User-facing endpoints (called via API Gateway) ───────────────────────

    @PostMapping("/deposit")
    public ResponseEntity<DepositResponse> deposit(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody DepositRequest request) {
        return ResponseEntity.ok(walletService.deposit(userId, request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawalResponse> withdraw(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody WithdrawalRequest request) {
        return ResponseEntity.ok(walletService.withdraw(userId, request));
    }

    @GetMapping
    public ResponseEntity<WalletResponse> getWallet(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam String currency) {
        return ResponseEntity.ok(walletService.getWallet(userId, currency));
    }

    // ─── Internal endpoints (called by Order Service) ─────────────────────────

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveFunds(
            @Valid @RequestBody ReserveFundsRequest request) {
        walletService.reserveFunds(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/release")
    public ResponseEntity<Void> releaseFunds(
            @Valid @RequestBody ReleaseFundsRequest request) {
        walletService.releaseFunds(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/capture")
    public ResponseEntity<Void> captureFunds(
            @Valid @RequestBody CaptureFundsRequest request) {
        walletService.captureFunds(request);
        return ResponseEntity.ok().build();
    }
}