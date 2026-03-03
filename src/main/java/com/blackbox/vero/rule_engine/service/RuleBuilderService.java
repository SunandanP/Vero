package com.blackbox.vero.rule_engine.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blackbox.vero.rule_engine.dto.request.ConditionRequest;
import com.blackbox.vero.rule_engine.dto.request.CreateRuleRequest;
import com.blackbox.vero.rule_engine.dto.request.RuleNodeRequest;
import com.blackbox.vero.rule_engine.dto.request.UpdateRuleRequest;
import com.blackbox.vero.rule_engine.dto.response.ConditionResponse;
import com.blackbox.vero.rule_engine.dto.response.RuleNodeResponse;
import com.blackbox.vero.rule_engine.dto.response.RuleResponse;
import com.blackbox.vero.rule_engine.engine.validator.RuleNodeValidator;
import com.blackbox.vero.rule_engine.entity.Rule;
import com.blackbox.vero.rule_engine.entity.RuleCondition;
import com.blackbox.vero.rule_engine.entity.RuleNode;
import com.blackbox.vero.rule_engine.exception.RuleEngineValidationError;
import com.blackbox.vero.rule_engine.repository.RuleRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for building and persisting rules from frontend-friendly DTOs.
 * Validates rules at creation time.
 */
@Service
@RequiredArgsConstructor
public class RuleBuilderService {

    private final RuleRepository ruleRepository;
    private final RuleNodeValidator ruleNodeValidator;

    /**
     * Creates a new rule from the request DTO.
     * Validates the rule structure before persisting.
     *
     * @param request the create rule request
     * @return the created rule response
     */
    @Transactional
    public RuleResponse createRule(CreateRuleRequest request) {
        if (ruleRepository.existsByRuleSlug(request.getSlug())) {
            throw new RuleEngineValidationError("Rule with slug '" + request.getSlug() + "' already exists");
        }

        Rule rule = Rule.builder()
                .name(request.getName())
                .ruleSlug(request.getSlug())
                .description(request.getDescription())
                .priority(request.getPriority() != null ? request.getPriority() : 0)
                .status("ACTIVE")
                .version(1)
                .build();

        RuleNode rootNode = buildRuleNode(request.getExpression(), rule, null);

        ruleNodeValidator.validateTree(rootNode);

        rule.setRuleNodes(List.of(rootNode));

        Rule savedRule = ruleRepository.save(rule);

        return toRuleResponse(savedRule);
    }

    /**
     * Gets a rule by ID.
     *
     * @param ruleId the rule ID
     * @return the rule response
     */
    @Transactional(readOnly = true)
    public RuleResponse getRuleById(UUID ruleId) {
        Rule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuleEngineValidationError("Rule not found with ID: " + ruleId));
        return toRuleResponse(rule);
    }

    /**
     * Gets a rule by slug.
     *
     * @param slug the rule slug
     * @return the rule response
     */
    @Transactional(readOnly = true)
    public RuleResponse getRuleBySlug(String slug) {
        Rule rule = ruleRepository.findByRuleSlug(slug)
                .orElseThrow(() -> new RuleEngineValidationError("Rule not found with slug: " + slug));
        return toRuleResponse(rule);
    }

    /**
     * Gets all rules.
     *
     * @return list of rule responses
     */
    @Transactional(readOnly = true)
    public List<RuleResponse> getAllRules() {
        return ruleRepository.findAll().stream()
                .map(this::toRuleResponse)
                .toList();
    }

    /**
     * Gets a rule entity by ID (for evaluation).
     *
     * @param ruleId the rule ID
     * @return the rule entity
     */
    @Transactional(readOnly = true)
    public Rule getRuleEntityById(UUID ruleId) {
        return ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuleEngineValidationError("Rule not found with ID: " + ruleId));
    }

    /**
     * Gets a rule entity by slug (for evaluation).
     *
     * @param slug the rule slug
     * @return the rule entity
     */
    @Transactional(readOnly = true)
    public Rule getRuleEntityBySlug(String slug) {
        return ruleRepository.findByRuleSlug(slug)
                .orElseThrow(() -> new RuleEngineValidationError("Rule not found with slug: " + slug));
    }

    /**
     * Updates an existing rule.
     * Only provided fields will be updated.
     *
     * @param ruleId the rule ID
     * @param request the update request
     * @return the updated rule response
     */
    @Transactional
    public RuleResponse updateRule(UUID ruleId, UpdateRuleRequest request) {
        Rule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new RuleEngineValidationError("Rule not found with ID: " + ruleId));

        if (request.getName() != null) {
            rule.setName(request.getName());
        }

        if (request.getSlug() != null && !request.getSlug().equals(rule.getRuleSlug())) {
            if (ruleRepository.existsByRuleSlug(request.getSlug())) {
                throw new RuleEngineValidationError("Rule with slug '" + request.getSlug() + "' already exists");
            }
            rule.setRuleSlug(request.getSlug());
        }

        if (request.getDescription() != null) {
            rule.setDescription(request.getDescription());
        }

        if (request.getPriority() != null) {
            rule.setPriority(request.getPriority());
        }

        if (request.getStatus() != null) {
            rule.setStatus(request.getStatus());
        }

        if (request.getExpression() != null) {
            rule.getRuleNodes().clear();
            
            RuleNode rootNode = buildRuleNode(request.getExpression(), rule, null);
            ruleNodeValidator.validateTree(rootNode);
            
            rule.getRuleNodes().add(rootNode);
            rule.setVersion(rule.getVersion() + 1);
        }

        Rule savedRule = ruleRepository.save(rule);
        return toRuleResponse(savedRule);
    }

    /**
     * Updates a rule by slug.
     *
     * @param slug the rule slug
     * @param request the update request
     * @return the updated rule response
     */
    @Transactional
    public RuleResponse updateRuleBySlug(String slug, UpdateRuleRequest request) {
        Rule rule = ruleRepository.findByRuleSlug(slug)
                .orElseThrow(() -> new RuleEngineValidationError("Rule not found with slug: " + slug));
        return updateRule(rule.getId(), request);
    }

    /**
     * Deletes a rule by ID.
     *
     * @param ruleId the rule ID
     */
    @Transactional
    public void deleteRule(UUID ruleId) {
        if (!ruleRepository.existsById(ruleId)) {
            throw new RuleEngineValidationError("Rule not found with ID: " + ruleId);
        }
        ruleRepository.deleteById(ruleId);
    }

    /**
     * Deletes a rule by slug.
     *
     * @param slug the rule slug
     */
    @Transactional
    public void deleteRuleBySlug(String slug) {
        Rule rule = ruleRepository.findByRuleSlug(slug)
                .orElseThrow(() -> new RuleEngineValidationError("Rule not found with slug: " + slug));
        ruleRepository.delete(rule);
    }

    private RuleNode buildRuleNode(RuleNodeRequest request, Rule rule, RuleNode parent) {
        validateNodeRequest(request);

        RuleNode node = RuleNode.builder()
                .logicalOperationType(request.getEffectiveOperator())
                .rule(parent == null ? rule : null)
                .parentRuleNode(parent)
                .build();

        if (request.getConditions() != null && !request.getConditions().isEmpty()) {
            List<RuleCondition> conditions = new ArrayList<>();
            for (ConditionRequest condReq : request.getConditions()) {
                RuleCondition condition = RuleCondition.builder()
                        .fieldName(condReq.getField())
                        .operationType(condReq.getOperator())
                        .expectedValue(condReq.getValue())
                        .valueType(condReq.getValueType())
                        .ruleNode(node)
                        .build();
                conditions.add(condition);
            }
            node.setRuleConditions(conditions);
        }

        if (request.getChildren() != null && !request.getChildren().isEmpty()) {
            List<RuleNode> children = new ArrayList<>();
            for (RuleNodeRequest childReq : request.getChildren()) {
                RuleNode childNode = buildRuleNode(childReq, rule, node);
                children.add(childNode);
            }
            node.setChildRuleNodes(children);
        }

        return node;
    }

    private void validateNodeRequest(RuleNodeRequest request) {
        int conditionCount = request.getConditions() != null ? request.getConditions().size() : 0;
        int childCount = request.getChildren() != null ? request.getChildren().size() : 0;
        int totalOperands = conditionCount + childCount;

        if (totalOperands == 0) {
            throw new RuleEngineValidationError(
                    "Rule node must have at least one condition or child node");
        }

        if (request.getOperator() == null && totalOperands > 1) {
            throw new RuleEngineValidationError(
                    "Logical operator is required when there are multiple conditions or children. " +
                    "Found " + totalOperands + " operands. Please specify 'operator': 'AND', 'OR', or 'NOT'");
        }

        if (request.getOperator() == null && childCount > 0) {
            throw new RuleEngineValidationError(
                    "Logical operator is required when using nested child nodes");
        }
    }

    private RuleResponse toRuleResponse(Rule rule) {
        RuleNodeResponse expressionResponse = null;
        if (rule.getRuleNodes() != null && !rule.getRuleNodes().isEmpty()) {
            expressionResponse = toRuleNodeResponse(rule.getRuleNodes().get(0));
        }

        return RuleResponse.builder()
                .id(rule.getId())
                .name(rule.getName())
                .slug(rule.getRuleSlug())
                .description(rule.getDescription())
                .priority(rule.getPriority())
                .status(rule.getStatus())
                .version(rule.getVersion())
                .expression(expressionResponse)
                .createdAt(rule.getCreatedAt())
                .updatedAt(rule.getUpdatedAt())
                .build();
    }

    private RuleNodeResponse toRuleNodeResponse(RuleNode node) {
        List<ConditionResponse> conditions = null;
        if (node.getRuleConditions() != null && !node.getRuleConditions().isEmpty()) {
            conditions = node.getRuleConditions().stream()
                    .map(this::toConditionResponse)
                    .toList();
        }

        List<RuleNodeResponse> children = null;
        if (node.getChildRuleNodes() != null && !node.getChildRuleNodes().isEmpty()) {
            children = node.getChildRuleNodes().stream()
                    .map(this::toRuleNodeResponse)
                    .toList();
        }

        return RuleNodeResponse.builder()
                .id(node.getId())
                .operator(node.getLogicalOperationType())
                .conditions(conditions)
                .children(children)
                .build();
    }

    private ConditionResponse toConditionResponse(RuleCondition condition) {
        return ConditionResponse.builder()
                .id(condition.getId())
                .field(condition.getFieldName())
                .operator(condition.getOperationType())
                .value(condition.getExpectedValue())
                .valueType(condition.getValueType())
                .build();
    }
}
