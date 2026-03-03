package com.blackbox.vero.ai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.blackbox.vero.ai.dto.GeminiResponse;
import com.blackbox.vero.ai.dto.NaturalLanguageRuleRequest;
import com.blackbox.vero.ai.dto.NaturalLanguageRuleResponse;
import com.blackbox.vero.ai.exception.GeminiApiException;
import com.blackbox.vero.rule_engine.dto.request.CreateRuleRequest;
import com.blackbox.vero.rule_engine.dto.request.RuleNodeRequest;
import com.blackbox.vero.rule_engine.dto.response.RuleResponse;
import com.blackbox.vero.rule_engine.service.RuleBuilderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service for converting natural language descriptions into structured rules using Gemini AI.
 */
@Service
public class NaturalLanguageRuleService {

    private static final Logger log = LoggerFactory.getLogger(NaturalLanguageRuleService.class);

    private final GeminiService geminiService;
    private final RuleBuilderService ruleBuilderService;
    private final ObjectMapper objectMapper;

    public NaturalLanguageRuleService(
            GeminiService geminiService,
            RuleBuilderService ruleBuilderService,
            ObjectMapper objectMapper) {
        this.geminiService = geminiService;
        this.ruleBuilderService = ruleBuilderService;
        this.objectMapper = objectMapper;
    }

    /**
     * Parses a natural language description into a rule structure without saving.
     */
    public NaturalLanguageRuleResponse parseRule(NaturalLanguageRuleRequest request) {
        long startTime = System.currentTimeMillis();

        String prompt = buildPrompt(request);
        GeminiResponse geminiResponse = geminiService.generateContent(prompt);
        String responseText = geminiResponse.getText();

        if (responseText == null || responseText.isBlank()) {
            throw new GeminiApiException("Gemini returned empty response for rule parsing");
        }

        CreateRuleRequest parsedRule = parseGeminiResponse(responseText, request);
        
        long processingTime = System.currentTimeMillis() - startTime;
        
        return NaturalLanguageRuleResponse.builder()
                .originalDescription(request.getDescription())
                .interpretation(extractInterpretation(responseText))
                .parsedRule(parsedRule)
                .metadata(buildMetadata(parsedRule, processingTime, geminiResponse))
                .build();
    }

    /**
     * Parses a natural language description and creates the rule in the database.
     */
    public NaturalLanguageRuleResponse parseAndCreateRule(NaturalLanguageRuleRequest request) {
        NaturalLanguageRuleResponse response = parseRule(request);
        
        RuleResponse createdRule = ruleBuilderService.createRule(response.getParsedRule());
        
        return NaturalLanguageRuleResponse.builder()
                .originalDescription(response.getOriginalDescription())
                .interpretation(response.getInterpretation())
                .parsedRule(response.getParsedRule())
                .createdRule(createdRule)
                .metadata(response.getMetadata())
                .build();
    }

    private String buildPrompt(NaturalLanguageRuleRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("""
            You are a rule engine expert. Your task is to convert natural language descriptions into structured rule definitions.
            
            The rule engine supports the following:
            
            LOGICAL OPERATORS:
            - AND: All conditions must be true
            - OR: At least one condition must be true
            - NOT: Negates the condition/group
            
            COMPARISON OPERATORS:
            - EQUALS: Exact match
            - NOT_EQUALS: Not equal
            - GREATER_THAN: Greater than (for numbers/dates)
            - LESS_THAN: Less than (for numbers/dates)
            - GREATER_THAN_OR_EQUALS: Greater than or equal
            - LESS_THAN_OR_EQUALS: Less than or equal
            - CONTAINS: String contains substring
            - NOT_CONTAINS: String does not contain substring
            
            VALUE TYPES:
            - INTEGER: Whole numbers
            - DOUBLE: Decimal numbers
            - BOOLEAN: true/false
            - STRING: Text
            - DATE: Date (YYYY-MM-DD format)
            - DATETIME: Date and time (YYYY-MM-DDTHH:MM:SS format)
            
            OUTPUT FORMAT:
            You must respond with ONLY a valid JSON object in this exact structure:
            
            {
              "interpretation": "Brief explanation of how you understood the rule",
              "rule": {
                "name": "Rule name",
                "slug": "rule-slug-kebab-case",
                "description": "Rule description",
                "priority": 1,
                "expression": {
                  "operator": "AND|OR|NOT",
                  "conditions": [
                    {
                      "field": "fieldName",
                      "operator": "EQUALS|NOT_EQUALS|GREATER_THAN|LESS_THAN|GREATER_THAN_OR_EQUALS|LESS_THAN_OR_EQUALS|CONTAINS|NOT_CONTAINS",
                      "value": "value",
                      "valueType": "INTEGER|DOUBLE|BOOLEAN|STRING|DATE|DATETIME"
                    }
                  ],
                  "children": []
                }
              }
            }
            
            IMPORTANT RULES:
            1. Field names should be in camelCase (e.g., "monthlySpends", "userAge")
            2. For a single condition, you can omit the "operator" field (defaults to AND)
            3. Use nested "children" for complex expressions like (A AND B) OR (C AND D)
            4. NOT operator should have exactly one condition or one child group
            5. Infer appropriate value types from context (age is INTEGER, price is DOUBLE, etc.)
            6. Generate meaningful slug from the rule name (lowercase, hyphens)
            7. Output ONLY the JSON, no markdown, no explanation outside the JSON
            
            """);

        prompt.append("USER'S NATURAL LANGUAGE DESCRIPTION:\n");
        prompt.append(request.getDescription());
        prompt.append("\n\n");

        if (request.getRuleName() != null) {
            prompt.append("PREFERRED RULE NAME: ").append(request.getRuleName()).append("\n");
        }
        if (request.getRuleSlug() != null) {
            prompt.append("PREFERRED RULE SLUG: ").append(request.getRuleSlug()).append("\n");
        }
        if (request.getAdditionalContext() != null) {
            prompt.append("ADDITIONAL CONTEXT: ").append(request.getAdditionalContext()).append("\n");
        }

        prompt.append("\nGenerate the JSON rule structure now:");

        return prompt.toString();
    }

    private CreateRuleRequest parseGeminiResponse(String responseText, NaturalLanguageRuleRequest request) {
        try {
            String jsonContent = extractJson(responseText);
            log.debug("Extracted JSON: {}", jsonContent);

            GeminiRuleOutput output = objectMapper.readValue(jsonContent, GeminiRuleOutput.class);
            
            CreateRuleRequest ruleRequest = output.getRule();
            
            if (request.getRuleName() != null && !request.getRuleName().isBlank()) {
                ruleRequest.setName(request.getRuleName());
            }
            if (request.getRuleSlug() != null && !request.getRuleSlug().isBlank()) {
                ruleRequest.setSlug(request.getRuleSlug());
            }
            if (request.getPriority() != null) {
                ruleRequest.setPriority(request.getPriority());
            }

            return ruleRequest;

        } catch (JsonProcessingException e) {
            log.error("Failed to parse Gemini response as JSON: {}", responseText, e);
            throw new GeminiApiException("Failed to parse AI response into rule structure: " + e.getMessage(), e);
        }
    }

    private String extractJson(String text) {
        text = text.trim();
        
        if (text.startsWith("```json")) {
            text = text.substring(7);
        } else if (text.startsWith("```")) {
            text = text.substring(3);
        }
        
        if (text.endsWith("```")) {
            text = text.substring(0, text.length() - 3);
        }
        
        text = text.trim();
        
        int firstBrace = text.indexOf('{');
        int lastBrace = text.lastIndexOf('}');
        
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            return text.substring(firstBrace, lastBrace + 1);
        }
        
        return text;
    }

    private String extractInterpretation(String responseText) {
        try {
            String jsonContent = extractJson(responseText);
            GeminiRuleOutput output = objectMapper.readValue(jsonContent, GeminiRuleOutput.class);
            return output.getInterpretation();
        } catch (Exception e) {
            return null;
        }
    }

    private NaturalLanguageRuleResponse.ParsingMetadata buildMetadata(
            CreateRuleRequest rule, 
            long processingTime,
            GeminiResponse geminiResponse) {
        
        int conditionCount = countConditions(rule.getExpression());
        int groupCount = countGroups(rule.getExpression());
        String primaryOperator = rule.getExpression() != null && rule.getExpression().getOperator() != null
                ? rule.getExpression().getOperator().name()
                : "AND";
        
        Integer tokensUsed = null;
        if (geminiResponse.getUsageMetadata() != null) {
            tokensUsed = geminiResponse.getUsageMetadata().getTotalTokenCount();
        }

        return NaturalLanguageRuleResponse.ParsingMetadata.builder()
                .conditionsExtracted(conditionCount)
                .nestedGroupsCreated(groupCount)
                .primaryLogicalOperator(primaryOperator)
                .processingTimeMs(processingTime)
                .tokensUsed(tokensUsed)
                .build();
    }

    private int countConditions(RuleNodeRequest node) {
        if (node == null) return 0;
        
        int count = node.getConditions() != null ? node.getConditions().size() : 0;
        
        if (node.getChildren() != null) {
            for (RuleNodeRequest child : node.getChildren()) {
                count += countConditions(child);
            }
        }
        
        return count;
    }

    private int countGroups(RuleNodeRequest node) {
        if (node == null) return 0;
        
        int count = 0;
        
        if (node.getChildren() != null) {
            count = node.getChildren().size();
            for (RuleNodeRequest child : node.getChildren()) {
                count += countGroups(child);
            }
        }
        
        return count;
    }

    /**
     * Internal class to parse Gemini's JSON output.
     */
    @lombok.Data
    private static class GeminiRuleOutput {
        private String interpretation;
        private CreateRuleRequest rule;
    }
}
