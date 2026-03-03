package com.blackbox.vero.rule_engine.engine.type_resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.blackbox.vero.rule_engine.enums.ValueType;

@Component
public class NumberResolver implements ValueTypeResolver<Integer> {

    private final Logger logger = LoggerFactory.getLogger(NumberResolver.class);

    @Override
    public Integer resolve(String value) {
        if (value == null) {
            logger.error("Cannot resolve null value to Integer as it is null or empty");
            throw new IllegalArgumentException("Cannot resolve null value to Integer");
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cannot parse '" + value + "' as Integer", e);
        }
    }
    
    @Override
    public ValueType getSupportedType() {
        return ValueType.INTEGER;
    }
}
