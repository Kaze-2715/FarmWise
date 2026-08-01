package com.farmwise.ai.tool;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.farmwise.ai.dto.ReferenceResponse;

public final class ReferenceCollector {
    private final Map<String, ReferenceResponse> references = new LinkedHashMap<>();

    public synchronized void add(ReferenceResponse reference) {
        String key = "%s:%s:%s".formatted(
                reference.type(),
                reference.sourceId(),
                reference.label());
        references.putIfAbsent(key, reference);
    }

    public synchronized List<ReferenceResponse> snapshot() {
        return List.copyOf(references.values());
    }
}
