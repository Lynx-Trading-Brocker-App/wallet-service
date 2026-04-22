package com.lynx.wallet_service.wallet.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class WalletResponse {

    private UUID id;
    private UUID userId;
    private String currency;
    private BigDecimal availableBalance;
    private BigDecimal reservedBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive;
}