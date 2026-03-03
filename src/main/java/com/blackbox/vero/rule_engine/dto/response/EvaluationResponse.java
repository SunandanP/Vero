package com.blackbox.vero.rule_engine.dto.response;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponse {
    private UUID ruleId;
    private String ruleName;
    private String ruleSlug;
    private boolean result;
    private Map<String, Object> facts;
    private LocalDateTime evaluatedAt;
    private Long executionTimeMs;
}
