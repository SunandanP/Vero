package com.blackbox.vero.rule_engine.dto.request;

import java.util.Map;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for evaluating a rule against provided facts.
 * 
 * Example JSON:
 * {
 *   "ruleId": "550e8400-e29b-41d4-a716-446655440000",
 *   "facts": {
 *     "age": "25",
 *     "income": "50000",
 *     "country": "India",
 *     "hasDefaulted": "false"
 *   }
 * }
 * 
 * Or evaluate by slug:
 * {
 *   "ruleSlug": "loan-eligibility",
 *   "facts": {
 *     "age": "25",
 *     "income": "50000"
 *   }
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluateRuleRequest {

    private UUID ruleId;

    private String ruleSlug;

    @NotNull(message = "Facts are required")
    @NotEmpty(message = "At least one fact is required")
    private Map<String, Object> facts;
}
