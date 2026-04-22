package com.lynx.wallet_service.common;

import java.util.Map;

public record ErrorResponse(
        String code,
        String message,
        Map<String, String> details
) {}