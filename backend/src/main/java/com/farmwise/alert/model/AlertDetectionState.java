package com.farmwise.alert.model;

public record AlertDetectionState(
        int abnormalCount,
        int normalCount) {
    public static AlertDetectionState initial() {
        return new AlertDetectionState(
                0,
                0);
    }
}
