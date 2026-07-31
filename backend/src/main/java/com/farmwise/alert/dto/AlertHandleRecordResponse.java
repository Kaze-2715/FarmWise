package com.farmwise.alert.dto;

import java.time.LocalDateTime;

public record AlertHandleRecordResponse(
        String measure,
        LocalDateTime handledAt,
        String result,
        String remark,
        String operator) {
}
