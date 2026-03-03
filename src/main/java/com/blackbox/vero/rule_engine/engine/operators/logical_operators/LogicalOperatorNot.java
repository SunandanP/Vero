package com.blackbox.vero.rule_engine.engine.operators.logical_operators;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.blackbox.vero.rule_engine.enums.LogicalOperationType;
import com.blackbox.vero.rule_engine.enums.LogicalOperatorType;
import com.blackbox.vero.rule_engine.exception.RuleEngineValidationError;

@Component
public class LogicalOperatorNot implements LogicalOperator {

    @Override
    public LogicalOperationType getSupportedLogicalOperationType() {
        return LogicalOperationType.NOT;
    }

    @Override
    public LogicalOperatorType getArity() {
        return LogicalOperatorType.UNARY;
    }

    @Override
    public boolean apply(Stream<Supplier<Boolean>> operands) {
        Supplier<Boolean> operand = operands.findFirst()
                .orElseThrow(() -> new RuleEngineValidationError("NOT operator requires exactly one operand"));
        return !operand.get();
    }

    @Override
    public boolean applyUnary(boolean value) {
        return !value;
    }
}
