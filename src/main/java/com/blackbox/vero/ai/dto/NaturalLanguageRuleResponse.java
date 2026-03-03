package com.blackbox.vero.ai.dto;

import com.blackbox.vero.rule_engine.dto.request.CreateRuleRequest;
import com.blackbox.vero.rule_engine.dto.response.RuleResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for natural language rule creation.
 * Contains both the parsed rule structure and the created rule (if saved).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaturalLanguageRuleResponse {

    private String originalDescription;
    
    private String interpretation;
    
    private CreateRuleRequest parsedRule;
    
    private RuleResponse createdRule;
    
    private ParsingMetadata metadata;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParsingMetadata {
        private Integer conditionsExtracted;
        private Integer nestedGroupsCreated;
        private String primaryLogicalOperator;
        private Long processingTimeMs;
        private Integer tokensUsed;
    }
}
