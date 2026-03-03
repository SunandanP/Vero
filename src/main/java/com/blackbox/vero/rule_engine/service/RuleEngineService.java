package com.blackbox.vero.rule_engine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackbox.vero.rule_engine.entity.Rule;
import com.blackbox.vero.rule_engine.entity.RuleCondition;
import com.blackbox.vero.rule_engine.entity.RuleNode;
import com.blackbox.vero.rule_engine.repository.RuleConditionRepository;
import com.blackbox.vero.rule_engine.repository.RuleNodeRepository;
import com.blackbox.vero.rule_engine.repository.RuleRepository;

@Service
public class RuleEngineService {

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private RuleNodeRepository ruleNodeRepository;

    @Autowired
    private RuleConditionRepository ruleConditionRepository;
    
    private final Logger logger = LoggerFactory.getLogger(RuleEngineService.class);

    public String getRulesHome() {
        return "<h1>Rules Home</h1>";
    }

    public void createRule(Rule rule) {
        logger.info("Creating rule: {}", rule);
        ruleRepository.save(rule);
        logger.info("Rule created successfully {}", rule.getId());
    }

    public void createRuleNode(RuleNode ruleNode) {
        logger.info("Creating Rule Node : {}", ruleNode);
        ruleNodeRepository.save(ruleNode);
        logger.info("Rule Node created successfully {}", ruleNode.getId());

    }

    public void createRuleCondition(RuleCondition ruleCondition) {
        logger.info("Creating Rule Condition : {}", ruleCondition);
        ruleConditionRepository.save(ruleCondition);
        logger.info("Rule Condition created successfully {}", ruleCondition.getId());
    }


    
}
