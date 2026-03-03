package com.blackbox.vero.rule_engine.enums;

public enum LogicalOperationType {
    AND(LogicalOperatorType.BINARY),
    OR(LogicalOperatorType.BINARY),
    NOT(LogicalOperatorType.UNARY);

    private final LogicalOperatorType logicalOperatorType;

    LogicalOperationType(LogicalOperatorType logicalOperatorType) {
        this.logicalOperatorType = logicalOperatorType;
    }

    public LogicalOperatorType getLogicalOperatorType() {
        return logicalOperatorType;
    }
}
