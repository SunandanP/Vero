package com.blackbox.vero.rule_engine.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blackbox.vero.rule_engine.dto.request.CreateRuleRequest;
import com.blackbox.vero.rule_engine.dto.request.EvaluateRuleRequest;
import com.blackbox.vero.rule_engine.dto.request.UpdateRuleRequest;
import com.blackbox.vero.rule_engine.dto.response.ApiResponse;
import com.blackbox.vero.rule_engine.dto.response.EvaluationResponse;
import com.blackbox.vero.rule_engine.dto.response.RuleResponse;
import com.blackbox.vero.rule_engine.engine.context.EvaluationContext;
import com.blackbox.vero.rule_engine.engine.evaluator.RuleEvaluator;
import com.blackbox.vero.rule_engine.entity.Rule;
import com.blackbox.vero.rule_engine.exception.RuleEngineValidationError;
import com.blackbox.vero.rule_engine.service.RuleBuilderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/rules")
@RequiredArgsConstructor
public class RuleController {

    private final RuleBuilderService ruleBuilderService;
    private final RuleEvaluator ruleEvaluator;

    /**
     * Create a new rule.
     * 
     * POST /api/v1/rules
     */
    @PostMapping
    public ResponseEntity<ApiResponse<RuleResponse>> createRule(
            @Valid @RequestBody CreateRuleRequest request) {
        RuleResponse response = ruleBuilderService.createRule(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Rule created successfully"));
    }

    /**
     * Get all rules.
     * 
     * GET /api/v1/rules
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<RuleResponse>>> getAllRules() {
        List<RuleResponse> rules = ruleBuilderService.getAllRules();
        return ResponseEntity.ok(ApiResponse.success(rules));
    }

    /**
     * Get a rule by ID.
     * 
     * GET /api/v1/rules/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RuleResponse>> getRuleById(@PathVariable UUID id) {
        RuleResponse response = ruleBuilderService.getRuleById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get a rule by slug.
     * 
     * GET /api/v1/rules/slug/{slug}
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<RuleResponse>> getRuleBySlug(@PathVariable String slug) {
        RuleResponse response = ruleBuilderService.getRuleBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Update a rule by ID.
     * 
     * PUT /api/v1/rules/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RuleResponse>> updateRule(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRuleRequest request) {
        RuleResponse response = ruleBuilderService.updateRule(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Rule updated successfully"));
    }

    /**
     * Update a rule by slug.
     * 
     * PUT /api/v1/rules/slug/{slug}
     */
    @PutMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<RuleResponse>> updateRuleBySlug(
            @PathVariable String slug,
            @Valid @RequestBody UpdateRuleRequest request) {
        RuleResponse response = ruleBuilderService.updateRuleBySlug(slug, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Rule updated successfully"));
    }

    /**
     * Delete a rule by ID.
     * 
     * DELETE /api/v1/rules/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRule(@PathVariable UUID id) {
        ruleBuilderService.deleteRule(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Rule deleted successfully"));
    }

    /**
     * Delete a rule by slug.
     * 
     * DELETE /api/v1/rules/slug/{slug}
     */
    @DeleteMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<Void>> deleteRuleBySlug(@PathVariable String slug) {
        ruleBuilderService.deleteRuleBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(null, "Rule deleted successfully"));
    }

    /**
     * Evaluate a rule against provided facts.
     * 
     * POST /api/v1/rules/evaluate
     * 
     * Request body can specify either ruleId or ruleSlug.
     */
    @PostMapping("/evaluate")
    public ResponseEntity<ApiResponse<EvaluationResponse>> evaluateRule(
            @Valid @RequestBody EvaluateRuleRequest request) {
        
        if (request.getRuleId() == null && request.getRuleSlug() == null) {
            throw new RuleEngineValidationError("Either ruleId or ruleSlug must be provided");
        }

        Rule rule;
        if (request.getRuleId() != null) {
            rule = ruleBuilderService.getRuleEntityById(request.getRuleId());
        } else {
            rule = ruleBuilderService.getRuleEntityBySlug(request.getRuleSlug());
        }

        EvaluationContext context = EvaluationContext.of(request.getFacts());

        long startTime = System.currentTimeMillis();
        boolean result = ruleEvaluator.evaluate(rule, context);
        long executionTime = System.currentTimeMillis() - startTime;

        EvaluationResponse response = EvaluationResponse.builder()
                .ruleId(rule.getId())
                .ruleName(rule.getName())
                .ruleSlug(rule.getRuleSlug())
                .result(result)
                .facts(request.getFacts())
                .evaluatedAt(LocalDateTime.now())
                .executionTimeMs(executionTime)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Evaluate a rule by ID with facts in request body.
     * 
     * POST /api/v1/rules/{id}/evaluate
     */
    @PostMapping("/{id}/evaluate")
    public ResponseEntity<ApiResponse<EvaluationResponse>> evaluateRuleById(
            @PathVariable UUID id,
            @Valid @RequestBody EvaluateRuleRequest request) {
        
        request.setRuleId(id);
        return evaluateRule(request);
    }

    /**
     * Evaluate a rule by slug with facts in request body.
     * 
     * POST /api/v1/rules/slug/{slug}/evaluate
     */
    @PostMapping("/slug/{slug}/evaluate")
    public ResponseEntity<ApiResponse<EvaluationResponse>> evaluateRuleBySlug(
            @PathVariable String slug,
            @Valid @RequestBody EvaluateRuleRequest request) {
        
        request.setRuleSlug(slug);
        return evaluateRule(request);
    }
}
