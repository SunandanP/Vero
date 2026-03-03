package com.blackbox.vero.rule_engine.enums;

import java.time.LocalDate;
import java.time.LocalDateTime;

public enum ValueType {

    INTEGER(Integer.class),
    DOUBLE(Double.class),
    BOOLEAN(Boolean.class),
    DATE(LocalDate.class),
    DATETIME(LocalDateTime.class),
    STRING(String.class);

    private final Class<?> type;

    ValueType(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }
}
