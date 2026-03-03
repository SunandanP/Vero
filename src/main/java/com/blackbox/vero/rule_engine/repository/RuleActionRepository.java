package com.blackbox.vero.rule_engine.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blackbox.vero.rule_engine.entity.RuleAction;

public interface RuleActionRepository extends JpaRepository<RuleAction, UUID> {
    
}
