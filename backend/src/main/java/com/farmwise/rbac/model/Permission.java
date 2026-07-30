package com.farmwise.rbac.model;

public record Permission(
        String code,
        String name,
        String module,
        String description) {
}
