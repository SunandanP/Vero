package com.blackbox.vero.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a rule from natural language.
 * 
 * Example:
 * {
 *   "description": "I'm creating a rule for fraud detection so I need to identify that on the following conditions: Age > 18 and along with that the monthly spends should be at least 800 to buy my product",
 *   "ruleName": "Fraud Detection Rule",
 *   "ruleSlug": "fraud-detection"
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaturalLanguageRuleRequest {

    @NotBlank(message = "Natural language description is required")
    private String description;

    private String ruleName;
    
    private String ruleSlug;
    
    private String additionalContext;
    
    private Integer priority;
}
