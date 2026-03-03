package com.blackbox.vero.rule_engine.dto.response;

import java.util.List;
import java.util.UUID;

import com.blackbox.vero.rule_engine.enums.LogicalOperationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleNodeResponse {
    private UUID id;
    private LogicalOperationType operator;
    private List<ConditionResponse> conditions;
    private List<RuleNodeResponse> children;
}
