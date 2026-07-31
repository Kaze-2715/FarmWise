package com.farmwise.device.event;

import java.util.List;

import com.farmwise.device.model.SensorReading;

public record SensorReadingsSavedEvent(
    String messageId,
    List<SensorReading> readings
) {
    public SensorReadingsSavedEvent {
        readings = List.copyOf(readings);
    }
}
