package com.blackbox.vero.rule_engine.engine.operators.comparision_operators;

import com.blackbox.vero.rule_engine.enums.OperationType;

/**
 * Strategy interface for comparison operators.
 * Implementations should be annotated with @Component and provide
 * their supported OperationType via {@link #getSupportedOperation()}.
 */
public interface Operator {
    
    /**
     * Applies the operator to compare expected and actual values.
     *
     * @param expectedValue the expected value
     * @param actualValue the actual value to compare
     * @param <T> the comparable type
     * @return true if the comparison succeeds, false otherwise
     */
    <T extends Comparable<T>> boolean apply(T expectedValue, T actualValue);
    
    /**
     * Returns the OperationType this operator supports.
     * Used by the registry for auto-discovery.
     *
     * @return the supported OperationType
     */
    OperationType getSupportedOperation();
}
