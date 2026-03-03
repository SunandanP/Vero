package com.blackbox.vero.rule_engine.engine.registry;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.blackbox.vero.rule_engine.engine.operators.comparision_operators.Operator;
import com.blackbox.vero.rule_engine.enums.OperationType;

import lombok.Data;

/**
 * Registry for Operator implementations.
 * Uses Spring's auto-discovery to automatically register all
 * Operator beans based on their supported operation type.
 */
@Component
@Data
public class OperatorRegistry {

    private final Map<OperationType, Operator> operators;

    /**
     * Constructor that auto-discovers and registers all Operator beans.
     *
     * @param operatorList all Operator beans discovered by Spring
     */
    public OperatorRegistry(List<Operator> operatorList) {
        this.operators = new EnumMap<>(OperationType.class);
        
        for (Operator operator : operatorList) {
            OperationType type = operator.getSupportedOperation();
            if (operators.containsKey(type)) {
                throw new IllegalStateException(
                    "Duplicate Operator for operation " + type + 
                    ": " + operators.get(type).getClass().getSimpleName() + 
                    " and " + operator.getClass().getSimpleName()
                );
            }
            operators.put(type, operator);
        }
    }

    /**
     * Gets the operator for the specified operation type.
     *
     * @param operationType the operation type
     * @return the operator for the specified type
     * @throws IllegalArgumentException if no operator is registered for the type
     */
    public Operator getOperator(OperationType operationType) {
        Operator operator = operators.get(operationType);
        if (operator == null) {
            throw new IllegalArgumentException(
                "No Operator registered for operation: " + operationType
            );
        }
        return operator;
    }
    
    /**
     * Checks if an operator is registered for the specified operation type.
     *
     * @param operationType the operation type to check
     * @return true if an operator is registered, false otherwise
     */
    public boolean hasOperator(OperationType operationType) {
        return operators.containsKey(operationType);
    }
}
