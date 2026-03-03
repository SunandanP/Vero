package com.blackbox.vero.rule_engine.dto.request;

import com.blackbox.vero.rule_engine.enums.OperationType;
import com.blackbox.vero.rule_engine.enums.ValueType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for a single condition in a rule.
 * 
 * Example JSON:
 * {
 *   "field": "age",
 *   "operator": "GREATER_THAN",
 *   "value": "18",
 *   "valueType": "INTEGER"
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConditionRequest {

    @NotBlank(message = "Field name is required")
    private String field;

    @NotNull(message = "Operator is required")
    private OperationType operator;

    @NotBlank(message = "Value is required")
    private String value;

    @NotNull(message = "Value type is required")
    private ValueType valueType;
}
