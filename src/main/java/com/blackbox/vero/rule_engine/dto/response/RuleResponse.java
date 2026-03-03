package com.blackbox.vero.rule_engine.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleResponse {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private Integer priority;
    private String status;
    private Integer version;
    private RuleNodeResponse expression;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
