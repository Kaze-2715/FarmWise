package com.farmwise.irrigation.dto;

import java.util.List;

public record IrrigationBatchResponse(
        String batchId, String landId, List<IrrigationRecordResponse> records) {}
