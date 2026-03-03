package com.blackbox.vero.rule_engine.engine.evaluator;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.blackbox.vero.rule_engine.engine.context.EvaluationContext;
import com.blackbox.vero.rule_engine.entity.Rule;
import com.blackbox.vero.rule_engine.entity.RuleNode;
import com.blackbox.vero.rule_engine.exception.RuleEngineValidationError;

import lombok.RequiredArgsConstructor;

/**
 * Main entry point for rule evaluation.
 * Assumes rules have been validated at creation time.
 */
@Component
@RequiredArgsConstructor
public class RuleEvaluator {

    private final NodeEvaluator nodeEvaluator;

    /**
     * Evaluates a rule against the provided evaluation context.
     * Assumes the rule has been validated at creation time.
     *
     * @param rule the rule to evaluate
     * @param context the evaluation context containing facts
     * @return true if the rule evaluates to true, false otherwise
     */
    public boolean evaluate(Rule rule, EvaluationContext context) {
        List<RuleNode> rootNodes = rule.getRuleNodes();

        if (rootNodes == null || rootNodes.isEmpty()) {
            throw new RuleEngineValidationError("Rule has no root nodes - was it created properly?");
        }

        return rootNodes.stream()
                .allMatch(node -> nodeEvaluator.evaluate(node, context));
    }

    /**
     * Evaluates a single rule node.
     * Assumes the node has been validated at creation time.
     *
     * @param ruleNode the root node to evaluate
     * @param context the evaluation context
     * @return true if the node evaluates to true
     */
    public boolean evaluateNode(RuleNode ruleNode, EvaluationContext context) {
        return nodeEvaluator.evaluate(ruleNode, context);
    }

    /**
     * Convenience method for simple single-field evaluation.
     *
     * @param rule the rule to evaluate
     * @param fieldName the field name
     * @param value the value to evaluate against
     * @return true if the rule evaluates to true
     */
    public boolean evaluate(Rule rule, String fieldName, Object value) {
        EvaluationContext context = EvaluationContext.of(fieldName, value);
        return evaluate(rule, context);
    }

    /**
     * Convenience method for multi-field evaluation.
     *
     * @param rule the rule to evaluate
     * @param facts map of field names to values
     * @return true if the rule evaluates to true
     */
    public boolean evaluate(Rule rule, Map<String, Object> facts) {
        EvaluationContext context = EvaluationContext.of(facts);
        return evaluate(rule, context);
    }
}
