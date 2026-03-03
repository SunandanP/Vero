package com.blackbox.vero.rule_engine.repository;

import java.util.UUID;

import com.blackbox.vero.rule_engine.entity.RuleCondition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RuleConditionRepository extends JpaRepository<RuleCondition, UUID> {
    
}
