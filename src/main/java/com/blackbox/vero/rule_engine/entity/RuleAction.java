package com.blackbox.vero.rule_engine.entity;

import java.util.UUID;

import com.blackbox.vero.common.ChildEntity;
import com.blackbox.vero.rule_engine.enums.ActionType;

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
public class RuleAction extends ChildEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Enumerated(EnumType.STRING)
    private ActionType actionType;
    private String actionValue;

    @ManyToOne(fetch = FetchType.LAZY)
    private Rule rule;
}
