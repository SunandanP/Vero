package com.blackbox.vero.rule_engine.engine.operators.comparision_operators;

import org.springframework.stereotype.Component;

import com.blackbox.vero.rule_engine.enums.OperationType;

@Component
public class OperatorGreaterThanOrEqual implements Operator {
    
    @Override
    public <T extends Comparable<T>> boolean apply(T expectedValue, T actualValue) {
        return actualValue.compareTo(expectedValue) >= 0;
    }
    
    @Override
    public OperationType getSupportedOperation() {
        return OperationType.GREATER_THAN_OR_EQUALS;
    }
}
