package com.blackbox.vero.rule_engine.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blackbox.vero.rule_engine.entity.RuleNode;
import com.blackbox.vero.rule_engine.repository.RuleNodeRepository;

@Service
public class RuleNodeService {

    @Autowired
    private RuleNodeRepository ruleNodeRepository;

    public List<RuleNode> getChildren(RuleNode ruleNode) {
        return ruleNodeRepository.findByParentRuleNode(ruleNode);
    }
    
}
