package com.lynx.wallet_service.wallet.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TransactionResponse {

    private UUID id;
    private UUID walletId;
    private UUID referenceId;
    private String transactionType;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}