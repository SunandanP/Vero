package com.blackbox.vero.rule_engine.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blackbox.vero.rule_engine.entity.RuleNode;

public interface RuleNodeRepository extends JpaRepository<RuleNode, UUID>{

    List<RuleNode> findByParentRuleNode(RuleNode parentRuleNodeId);
    
}
