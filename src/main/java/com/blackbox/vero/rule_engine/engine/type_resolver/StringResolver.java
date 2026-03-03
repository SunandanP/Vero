package com.blackbox.vero.rule_engine.engine.type_resolver;

import org.springframework.stereotype.Component;

import com.blackbox.vero.rule_engine.enums.ValueType;

@Component
public class StringResolver implements ValueTypeResolver<String> {
    
    @Override
    public String resolve(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot resolve null value to String");
        }
        return value;
    }
    
    @Override
    public ValueType getSupportedType() {
        return ValueType.STRING;
    }
}
