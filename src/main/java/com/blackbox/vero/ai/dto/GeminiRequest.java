package com.blackbox.vero.ai.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for Gemini API.
 * 
 * Example:
 * {
 *   "contents": [
 *     {
 *       "parts": [
 *         { "text": "Your prompt here" }
 *       ]
 *     }
 *   ],
 *   "generationConfig": {
 *     "temperature": 0.2,
 *     "maxOutputTokens": 2048
 *   }
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiRequest {

    private List<Content> contents;
    private GenerationConfig generationConfig;
    private List<SafetySetting> safetySettings;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        private List<Part> parts;
        private String role;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String text;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerationConfig {
        private Double temperature;
        private Integer maxOutputTokens;
        private Double topP;
        private Integer topK;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SafetySetting {
        private String category;
        private String threshold;
    }

    /**
     * Creates a simple text request with the given prompt.
     */
    public static GeminiRequest ofText(String prompt, Double temperature, Integer maxTokens) {
        return GeminiRequest.builder()
                .contents(List.of(
                        Content.builder()
                                .parts(List.of(Part.builder().text(prompt).build()))
                                .build()))
                .generationConfig(GenerationConfig.builder()
                        .temperature(temperature)
                        .maxOutputTokens(maxTokens)
                        .build())
                .build();
    }
}
