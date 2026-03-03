package com.blackbox.vero.rule_engine.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blackbox.vero.rule_engine.entity.Rule;

@Repository
public interface RuleRepository extends JpaRepository<Rule, UUID> {

    Optional<Rule> findByRuleSlug(String ruleSlug);

    boolean existsByRuleSlug(String ruleSlug);
}
