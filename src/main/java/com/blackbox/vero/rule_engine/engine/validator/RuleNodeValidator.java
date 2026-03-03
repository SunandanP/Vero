package com.blackbox.vero.rule_engine.engine.validator;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.blackbox.vero.rule_engine.entity.RuleNode;
import com.blackbox.vero.rule_engine.enums.LogicalOperationType;
import com.blackbox.vero.rule_engine.exception.RuleEngineValidationError;

/**
 * Validates rule node trees before evaluation.
 * Performs a single recursive validation pass over the entire tree.
 */
@Component
public class RuleNodeValidator {

    private static final Set<LogicalOperationType> BINARY_OPERATORS = Set.of(
            LogicalOperationType.AND,
            LogicalOperationType.OR
    );

    /**
     * Validates the entire rule node tree starting from the root.
     * Call this once before evaluation to avoid repeated validation during recursion.
     *
     * @param root the root node of the rule tree
     * @throws RuleEngineValidationError if validation fails
     */
    public void validateTree(RuleNode root) {
        if (root == null) {
            throw new RuleEngineValidationError("Rule node cannot be null");
        }
        validateNodeRecursive(root);
    }

    /**
     * Validates a single node (for backward compatibility or targeted validation).
     *
     * @param ruleNode the node to validate
     * @throws RuleEngineValidationError if validation fails
     */
    public void validate(RuleNode ruleNode) {
        validateNodeInternal(ruleNode);
    }

    private void validateNodeRecursive(RuleNode ruleNode) {
        validateNodeInternal(ruleNode);

        List<RuleNode> children = getChildNodes(ruleNode);
        for (RuleNode child : children) {
            validateNodeRecursive(child);
        }
    }

    private void validateNodeInternal(RuleNode ruleNode) {
        if (ruleNode.getLogicalOperationType() == null) {
            throw new RuleEngineValidationError("Logical operation type is required");
        }

        int conditionCount = getConditionCount(ruleNode);
        int childCount = getChildCount(ruleNode);
        int totalOperands = conditionCount + childCount;

        LogicalOperationType operationType = ruleNode.getLogicalOperationType();

        if (operationType == LogicalOperationType.NOT) {
            validateNotOperator(ruleNode, conditionCount, childCount, totalOperands);
        } else if (BINARY_OPERATORS.contains(operationType)) {
            validateBinaryOperator(ruleNode, totalOperands);
        }
    }

    private void validateNotOperator(RuleNode ruleNode, int conditionCount, int childCount, int totalOperands) {
        if (totalOperands != 1) {
            throw new RuleEngineValidationError(
                    "NOT operator requires exactly one operand (condition or child node), found: " + totalOperands);
        }

        if (childCount == 1) {
            RuleNode child = ruleNode.getChildRuleNodes().get(0);
            if (child.getLogicalOperationType() == LogicalOperationType.NOT) {
                throw new RuleEngineValidationError(
                        "NOT operator cannot directly negate another NOT operator");
            }
        }
    }

    private void validateBinaryOperator(RuleNode ruleNode, int totalOperands) {
        if (totalOperands < 1) {
            throw new RuleEngineValidationError(
                    ruleNode.getLogicalOperationType() + " operator requires at least one operand");
        }
    }

    private int getConditionCount(RuleNode ruleNode) {
        return ruleNode.getRuleConditions() == null ? 0 : ruleNode.getRuleConditions().size();
    }

    private int getChildCount(RuleNode ruleNode) {
        return ruleNode.getChildRuleNodes() == null ? 0 : ruleNode.getChildRuleNodes().size();
    }

    private List<RuleNode> getChildNodes(RuleNode ruleNode) {
        return ruleNode.getChildRuleNodes() == null ? Collections.emptyList() : ruleNode.getChildRuleNodes();
    }
}
