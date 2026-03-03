package com.blackbox.vero.rule_engine.engine.type_resolver;

import org.springframework.stereotype.Component;

import com.blackbox.vero.rule_engine.enums.ValueType;

@Component
public class BooleanResolver implements ValueTypeResolver<Boolean> {
    
    @Override
    public Boolean resolve(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot resolve null value to Boolean");
        }
        return Boolean.parseBoolean(value);
    }
    
    @Override
    public ValueType getSupportedType() {
        return ValueType.BOOLEAN;
    }
}
