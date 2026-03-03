package com.blackbox.vero.rule_engine.engine.type_resolver;

import org.springframework.stereotype.Component;

import com.blackbox.vero.rule_engine.enums.ValueType;

@Component
public class DecimalResolver implements ValueTypeResolver<Double> {
    
    @Override
    public Double resolve(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot resolve null value to Double");
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cannot parse '" + value + "' as Double", e);
        }
    }
    
    @Override
    public ValueType getSupportedType() {
        return ValueType.DOUBLE;
    }
}
