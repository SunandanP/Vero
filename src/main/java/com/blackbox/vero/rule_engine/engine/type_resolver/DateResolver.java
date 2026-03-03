package com.blackbox.vero.rule_engine.engine.type_resolver;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Component;

import com.blackbox.vero.rule_engine.enums.ValueType;

@Component
public class DateResolver implements ValueTypeResolver<LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate resolve(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Cannot resolve null or empty value to LocalDateTime");
        }

        try {
            return LocalDate.parse(value, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid date format. Expected ISO format: yyyy-MM-dd. Value: " + value,
                    e
            );
        }
    }

    @Override
    public ValueType getSupportedType() {
        return ValueType.DATE;
    }
}
