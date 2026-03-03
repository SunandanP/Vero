package com.blackbox.vero.rule_engine.dto.response;

import java.util.UUID;

import com.blackbox.vero.rule_engine.enums.OperationType;
import com.blackbox.vero.rule_engine.enums.ValueType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConditionResponse {
    private UUID id;
    private String field;
    private OperationType operator;
    private String value;
    private ValueType valueType;
}
