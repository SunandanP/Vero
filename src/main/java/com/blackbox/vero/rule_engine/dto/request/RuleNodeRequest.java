package com.blackbox.vero.rule_engine.dto.request;

import java.util.List;

import com.blackbox.vero.rule_engine.enums.LogicalOperationType;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for a rule node (expression tree node).
 * Supports recursive nesting for complex expressions.
 * 
 * SIMPLE SINGLE CONDITION (operator defaults to AND):
 * {
 *   "conditions": [
 *     { "field": "age", "operator": "EQUALS", "value": "18", "valueType": "INTEGER" }
 *   ]
 * }
 * 
 * MULTIPLE CONDITIONS with AND:
 * {
 *   "operator": "AND",
 *   "conditions": [
 *     { "field": "age", "operator": "GREATER_THAN", "value": "18", "valueType": "INTEGER" },
 *     { "field": "country", "operator": "EQUALS", "value": "India", "valueType": "STRING" }
 *   ]
 * }
 * 
 * NOT operator:
 * {
 *   "operator": "NOT",
 *   "conditions": [
 *     { "field": "status", "operator": "EQUALS", "value": "inactive", "valueType": "STRING" }
 *   ]
 * }
 * 
 * NESTED: (A AND B) OR (C AND D)
 * {
 *   "operator": "OR",
 *   "children": [
 *     {
 *       "operator": "AND",
 *       "conditions": [
 *         { "field": "fieldA", "operator": "EQUALS", "value": "A", "valueType": "STRING" },
 *         { "field": "fieldB", "operator": "EQUALS", "value": "B", "valueType": "STRING" }
 *       ]
 *     },
 *     {
 *       "operator": "AND",
 *       "conditions": [
 *         { "field": "fieldC", "operator": "EQUALS", "value": "C", "valueType": "STRING" },
 *         { "field": "fieldD", "operator": "EQUALS", "value": "D", "valueType": "STRING" }
 *       ]
 *     }
 *   ]
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleNodeRequest {

    /**
     * Logical operator for combining conditions/children.
     * Optional - defaults to AND if not specified (useful for single conditions).
     */
    private LogicalOperationType operator;

    @Valid
    private List<ConditionRequest> conditions;

    @Valid
    private List<RuleNodeRequest> children;

    /**
     * Returns the effective operator, defaulting to AND if not specified.
     */
    public LogicalOperationType getEffectiveOperator() {
        return operator != null ? operator : LogicalOperationType.AND;
    }
}
