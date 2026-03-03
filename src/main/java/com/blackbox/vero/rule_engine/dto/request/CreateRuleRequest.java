package com.blackbox.vero.rule_engine.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new rule via API.
 * 
 * Example JSON:
 * {
 *   "name": "Loan Eligibility Rule",
 *   "slug": "loan-eligibility",
 *   "description": "Determines if a user is eligible for a loan",
 *   "priority": 1,
 *   "expression": {
 *     "operator": "AND",
 *     "conditions": [
 *       { "field": "age", "operator": "GREATER_THAN", "value": "18", "valueType": "INTEGER" },
 *       { "field": "income", "operator": "GREATER_THAN", "value": "30000", "valueType": "INTEGER" }
 *     ],
 *     "children": [
 *       {
 *         "operator": "NOT",
 *         "conditions": [
 *           { "field": "hasDefaulted", "operator": "EQUALS", "value": "true", "valueType": "BOOLEAN" }
 *         ]
 *       }
 *     ]
 *   }
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRuleRequest {

    @NotBlank(message = "Rule name is required")
    private String name;

    @NotBlank(message = "Rule slug is required")
    private String slug;

    private String description;

    private Integer priority;

    @NotNull(message = "Rule expression is required")
    @Valid
    private RuleNodeRequest expression;
}
