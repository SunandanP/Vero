package com.blackbox.vero.rule_engine.engine.registry;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.blackbox.vero.rule_engine.engine.operators.logical_operators.LogicalOperator;
import com.blackbox.vero.rule_engine.enums.LogicalOperationType;

import lombok.Data;

/**
 * Registry for Operator implementations.
 * Uses Spring's auto-discovery to automatically register all
 * Operator beans based on their supported operation type.
 */
@Component
@Data
public class LogicalOperatorRegistry {

    private final Map<LogicalOperationType, LogicalOperator> logicalOperators;

    /**
     * Constructor that auto-discovers and registers all Operator beans.
     *
     * @param logicalOperationTypeList all LogicalOperationType beans discovered by Spring
     */
    public LogicalOperatorRegistry(List<LogicalOperator> logicalOperatorList) {
        this.logicalOperators = new EnumMap<>(LogicalOperationType.class);
        
        for (LogicalOperator logicalOperator : logicalOperatorList) {
            LogicalOperationType type = logicalOperator.getSupportedLogicalOperationType();
            if (logicalOperators.containsKey(type)) {
                throw new IllegalStateException(
                    "Duplicate LogicalOperator for operation " + type + 
                    ": " + logicalOperators.get(type).getClass().getSimpleName() + 
                    " and " + logicalOperator.getClass().getSimpleName()
                );
            }
            logicalOperators.put(type, logicalOperator);
        }
    }

    /**
     * Gets the logical operator for the specified logical operation type.
     *
     * @param logicalOperationType the logical operation type
     * @return the logical operator for the specified type
     * @throws IllegalArgumentException if no logical operator is registered for the type
     */
    public LogicalOperator getLogicalOperator(LogicalOperationType logicalOperationType) {
        LogicalOperator logicalOperator = logicalOperators.get(logicalOperationType);
        if (logicalOperator == null) {
            throw new IllegalArgumentException(
                "No LogicalOperator registered for operation: " + logicalOperationType
            );
        }
        return logicalOperator;
    }
    
    /**
     * Checks if a logical operator is registered for the specified logical operation type.
     *
     * @param logicalOperationType the logical operation type to check
     * @return true if a logical operator is registered, false otherwise
     */
    public boolean hasLogicalOperator(LogicalOperationType logicalOperationType) {
        return logicalOperators.containsKey(logicalOperationType);
    }
}
