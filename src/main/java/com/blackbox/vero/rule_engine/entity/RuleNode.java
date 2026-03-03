package com.blackbox.vero.rule_engine.entity;

import java.util.List;
import java.util.UUID;

import com.blackbox.vero.common.ChildEntity;
import com.blackbox.vero.rule_engine.enums.LogicalOperationType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleNode extends ChildEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private LogicalOperationType logicalOperationType;

    @ManyToOne(fetch = FetchType.LAZY)
    private Rule rule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_rule_node_id")
    private RuleNode parentRuleNode;

    @OneToMany(mappedBy = "parentRuleNode", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "child_order")
    private List<RuleNode> childRuleNodes;

    @OneToMany(mappedBy = "ruleNode", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RuleCondition> ruleConditions;
}