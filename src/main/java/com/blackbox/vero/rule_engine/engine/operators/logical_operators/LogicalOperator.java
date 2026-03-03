package com.blackbox.vero.rule_engine.engine.operators.logical_operators;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.blackbox.vero.rule_engine.enums.LogicalOperationType;
import com.blackbox.vero.rule_engine.enums.LogicalOperatorType;

/**
 * Interface for logical operators in the rule engine.
 * Supports both unary (NOT) and binary (AND, OR) operations with short-circuit evaluation.
 */
public interface LogicalOperator {

    /**
     * Returns the logical operation type this operator handles.
     */
    LogicalOperationType getSupportedLogicalOperationType();

    /**
     * Returns the arity of this operator.
     */
    LogicalOperatorType getArity();

    /**
     * Applies the operator to a stream of lazy-evaluated boolean suppliers.
     * This enables short-circuit evaluation - operands are only evaluated as needed.
     *
     * @param operands stream of suppliers that produce boolean results when called
     * @return the result of applying this operator
     */
    boolean apply(Stream<Supplier<Boolean>> operands);

    /**
     * Applies unary operator to a single value.
     * Default implementation throws for binary operators.
     *
     * @param value the operand
     * @return the result
     */
    default boolean applyUnary(boolean value) {
        throw new UnsupportedOperationException(
                getSupportedLogicalOperationType() + " does not support unary operation");
    }

    /**
     * Applies binary operator to two values.
     * Default implementation throws for unary operators.
     *
     * @param left the left operand
     * @param right the right operand
     * @return the result
     */
    default boolean applyBinary(boolean left, boolean right) {
        throw new UnsupportedOperationException(
                getSupportedLogicalOperationType() + " does not support binary operation");
    }

    /**
     * Helper method to consume stream with short-circuit AND logic.
     */
    default boolean shortCircuitAnd(Stream<Supplier<Boolean>> operands) {
        Iterator<Supplier<Boolean>> iterator = operands.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().get()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper method to consume stream with short-circuit OR logic.
     */
    default boolean shortCircuitOr(Stream<Supplier<Boolean>> operands) {
        Iterator<Supplier<Boolean>> iterator = operands.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().get()) {
                return true;
            }
        }
        return false;
    }
}

