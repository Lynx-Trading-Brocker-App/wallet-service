package com.lynx.wallet_service.wallet.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TransactionHistoryResponse {

    private List<TransactionResponse> transactions;
    private long totalRecords;
    private int currentPage;
    private int totalPages;
    private int limit;
}