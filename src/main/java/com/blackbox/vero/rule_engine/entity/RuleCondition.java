package com.blackbox.vero.rule_engine.entity;

import java.util.UUID;

import com.blackbox.vero.common.ChildEntity;
import com.blackbox.vero.rule_engine.enums.OperationType;
import com.blackbox.vero.rule_engine.enums.ValueType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleCondition extends ChildEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String fieldName;

    @Enumerated(EnumType.STRING)
    private ValueType valueType;

    @Enumerated(EnumType.STRING)
    private OperationType operationType;
    private String expectedValue;

    @ManyToOne(fetch = FetchType.LAZY)
    private RuleNode ruleNode;
}
