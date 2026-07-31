package com.farmwise.alert.model;

public record AlertBlockingState(
        boolean blocking,
        String alertId) {
    public static AlertBlockingState initial() {
        return new AlertBlockingState(false, null);
    }
}
