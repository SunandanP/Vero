package com.blackbox.vero.rule_engine.engine.evaluator;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.blackbox.vero.rule_engine.engine.context.EvaluationContext;
import com.blackbox.vero.rule_engine.engine.operators.logical_operators.LogicalOperator;
import com.blackbox.vero.rule_engine.engine.registry.LogicalOperatorRegistry;
import com.blackbox.vero.rule_engine.entity.RuleCondition;
import com.blackbox.vero.rule_engine.entity.RuleNode;

import lombok.RequiredArgsConstructor;

/**
 * Evaluates rule nodes recursively with short-circuit evaluation support.
 * Validation should be performed once at the root level before calling evaluate.
 */
@Component
@RequiredArgsConstructor
public class NodeEvaluator {

    private final ConditionEvaluator conditionEvaluator;
    private final LogicalOperatorRegistry logicalOperatorRegistry;

    /**
     * Evaluates a rule node against the provided evaluation context.
     * This method assumes validation has already been performed on the tree.
     *
     * @param ruleNode the node to evaluate
     * @param context the evaluation context containing facts
     * @return true if the node evaluates to true, false otherwise
     */
    public boolean evaluate(RuleNode ruleNode, EvaluationContext context) {
        LogicalOperator operator = logicalOperatorRegistry.getLogicalOperator(
                ruleNode.getLogicalOperationType());

        Stream<Supplier<Boolean>> operandSuppliers = buildLazyOperandStream(ruleNode, context);

        return operator.apply(operandSuppliers);
    }

    /**
     * Builds a lazy stream of operand suppliers for short-circuit evaluation.
     * Operands are only evaluated when the supplier's get() method is called.
     */
    private Stream<Supplier<Boolean>> buildLazyOperandStream(RuleNode ruleNode, EvaluationContext context) {
        Stream<Supplier<Boolean>> conditionSuppliers = buildConditionSuppliers(ruleNode, context);
        Stream<Supplier<Boolean>> childSuppliers = buildChildSuppliers(ruleNode, context);

        return Stream.concat(conditionSuppliers, childSuppliers);
    }

    private Stream<Supplier<Boolean>> buildConditionSuppliers(RuleNode ruleNode, EvaluationContext context) {
        List<RuleCondition> conditions = ruleNode.getRuleConditions();
        if (conditions == null || conditions.isEmpty()) {
            return Stream.empty();
        }

        return conditions.stream()
                .map(condition -> (Supplier<Boolean>) () -> 
                        conditionEvaluator.evaluate(condition, context));
    }

    private Stream<Supplier<Boolean>> buildChildSuppliers(RuleNode ruleNode, EvaluationContext context) {
        List<RuleNode> children = ruleNode.getChildRuleNodes();
        if (children == null || children.isEmpty()) {
            return Stream.empty();
        }

        return children.stream()
                .map(child -> (Supplier<Boolean>) () -> evaluate(child, context));
    }

    /**
     * Backward-compatible method for simple single-field evaluation.
     *
     * @param ruleNode the node to evaluate
     * @param fieldName the field name
     * @param actualValue the actual value to compare
     * @return true if the node evaluates to true
     * @deprecated Use {@link #evaluate(RuleNode, EvaluationContext)} instead
     */
    @Deprecated
    public boolean evaluate(RuleNode ruleNode, String fieldName, String actualValue) {
        EvaluationContext context = EvaluationContext.of(fieldName, actualValue);
        return evaluate(ruleNode, context);
    }
}
