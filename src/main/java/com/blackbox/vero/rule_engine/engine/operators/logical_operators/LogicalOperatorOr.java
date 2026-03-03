package com.blackbox.vero.rule_engine.engine.operators.logical_operators;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.blackbox.vero.rule_engine.enums.LogicalOperationType;
import com.blackbox.vero.rule_engine.enums.LogicalOperatorType;

@Component
public class LogicalOperatorOr implements LogicalOperator {

    @Override
    public LogicalOperationType getSupportedLogicalOperationType() {
        return LogicalOperationType.OR;
    }

    @Override
    public LogicalOperatorType getArity() {
        return LogicalOperatorType.BINARY;
    }

    @Override
    public boolean apply(Stream<Supplier<Boolean>> operands) {
        return shortCircuitOr(operands);
    }

    @Override
    public boolean applyBinary(boolean left, boolean right) {
        return left || right;
    }
}
