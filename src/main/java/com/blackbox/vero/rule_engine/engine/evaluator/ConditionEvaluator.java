package com.blackbox.vero.rule_engine.engine.evaluator;

import org.springframework.stereotype.Component;

import com.blackbox.vero.rule_engine.engine.context.EvaluationContext;
import com.blackbox.vero.rule_engine.engine.operators.comparision_operators.Operator;
import com.blackbox.vero.rule_engine.engine.registry.OperatorRegistry;
import com.blackbox.vero.rule_engine.engine.registry.ValueTypeResolverRegistry;
import com.blackbox.vero.rule_engine.engine.type_resolver.ValueTypeResolver;
import com.blackbox.vero.rule_engine.entity.RuleCondition;
import com.blackbox.vero.rule_engine.exception.RuleEngineValidationError;

import lombok.RequiredArgsConstructor;

/**
 * Evaluates rule conditions by resolving values and applying operators.
 */
@Component
@RequiredArgsConstructor
public class ConditionEvaluator {

    private final OperatorRegistry operatorRegistry;
    private final ValueTypeResolverRegistry valueTypeResolverRegistry;

    /**
     * Evaluates a condition against the evaluation context.
     *
     * @param condition the rule condition containing expected value and operator
     * @param context the evaluation context containing facts
     * @param <T> the comparable type
     * @return true if the condition is satisfied, false otherwise
     */
    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> boolean evaluate(RuleCondition condition, EvaluationContext context) {
        String fieldName = condition.getFieldName();
        String actualValue = context.getFact(fieldName);

        if (actualValue == null && !context.hasFact(fieldName)) {
            throw new RuleEngineValidationError(
                    "Missing fact for field: " + fieldName);
        }

        ValueTypeResolver<T> resolver = (ValueTypeResolver<T>) valueTypeResolverRegistry
                .getResolver(condition.getValueType());

        T expected = resolver.resolve(condition.getExpectedValue());
        T actual = resolver.resolve(actualValue);

        Operator operator = operatorRegistry.getOperator(condition.getOperationType());

        return operator.apply(expected, actual);
    }

    /**
     * Backward-compatible method for simple single-value evaluation.
     *
     * @param condition the rule condition
     * @param actualValue the actual value to compare
     * @param <T> the comparable type
     * @return true if the condition is satisfied
     * @deprecated Use {@link #evaluate(RuleCondition, EvaluationContext)} instead
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public <T extends Comparable<T>> boolean evaluate(RuleCondition condition, String actualValue) {
        ValueTypeResolver<T> resolver = (ValueTypeResolver<T>) valueTypeResolverRegistry
                .getResolver(condition.getValueType());

        T expected = resolver.resolve(condition.getExpectedValue());
        T actual = resolver.resolve(actualValue);

        Operator operator = operatorRegistry.getOperator(condition.getOperationType());

        return operator.apply(expected, actual);
    }
}
