package com.blackbox.vero.rule_engine.dto.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing rule.
 * All fields are optional - only provided fields will be updated.
 * 
 * Example JSON (partial update - only name and description):
 * {
 *   "name": "Updated Rule Name",
 *   "description": "Updated description"
 * }
 * 
 * Example JSON (full update including expression):
 * {
 *   "name": "Updated Rule Name",
 *   "slug": "updated-slug",
 *   "description": "Updated description",
 *   "priority": 2,
 *   "status": "INACTIVE",
 *   "expression": {
 *     "operator": "AND",
 *     "conditions": [
 *       { "field": "age", "operator": "GREATER_THAN", "value": "21", "valueType": "INTEGER" }
 *     ]
 *   }
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRuleRequest {

    private String name;

    private String slug;

    private String description;

    private Integer priority;

    private String status;

    @Valid
    private RuleNodeRequest expression;
}
