package com.farmwise.common.util;

import java.util.Set;

import org.springframework.http.HttpStatus;

import com.farmwise.common.exception.BizException;

public final class ValidationUtil {
    private ValidationUtil() {
    }

    public static String validateRequired(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new BizException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        return value.strip();
    }

    public static String validateOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.strip();
    }

    public static String validateFilter(
            String value,
            Set<String> allowedValues,
            String errorMessage) {
        value = validateOptional(value);
        if (value != null && !allowedValues.contains(value)) {
            throw new BizException(HttpStatus.BAD_REQUEST, errorMessage + ": " + value);
        }
        return value;
    }
}
