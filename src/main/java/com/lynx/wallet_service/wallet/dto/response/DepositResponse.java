package com.lynx.wallet_service.wallet.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepositResponse {

    private String message;
    private WalletResponse wallet;
    private TransactionResponse transaction;
}