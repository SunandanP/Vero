package com.blackbox.vero.rule_engine.engine.type_resolver;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Component;

import com.blackbox.vero.rule_engine.enums.ValueType;

@Component
public class DateTimeResolver implements ValueTypeResolver<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public LocalDateTime resolve(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Cannot resolve null or empty value to LocalDateTime");
        }
        try {
            return LocalDateTime.parse(value, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected ISO format: yyyy-MM-dd HH:mm:ss. Value: " + value, e);
        }
    }
    
    @Override
    public ValueType getSupportedType() {
        return ValueType.DATETIME;
    }
}
