package com.blackbox.vero.ai.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Gemini API.
 * 
 * Example response:
 * {
 *   "candidates": [
 *     {
 *       "content": {
 *         "parts": [
 *           { "text": "Response text here" }
 *         ],
 *         "role": "model"
 *       },
 *       "finishReason": "STOP"
 *     }
 *   ],
 *   "usageMetadata": {
 *     "promptTokenCount": 10,
 *     "candidatesTokenCount": 50,
 *     "totalTokenCount": 60
 *   }
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiResponse {

    private List<Candidate> candidates;
    private UsageMetadata usageMetadata;
    private String modelVersion;
    private String responseId;
    private PromptFeedback promptFeedback;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Candidate {
        private Content content;
        private String finishReason;
        private Integer index;
        private List<SafetyRating> safetyRatings;
    }

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
        private String thoughtSignature;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsageMetadata {
        private Integer promptTokenCount;
        private Integer candidatesTokenCount;
        private Integer totalTokenCount;
        private Integer thoughtsTokenCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SafetyRating {
        private String category;
        private String probability;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PromptFeedback {
        private String blockReason;
        private List<SafetyRating> safetyRatings;
    }

    /**
     * Extracts the text from the first candidate's first part.
     */
    public String getText() {
        if (candidates != null && !candidates.isEmpty()) {
            Candidate candidate = candidates.get(0);
            if (candidate.getContent() != null && 
                candidate.getContent().getParts() != null && 
                !candidate.getContent().getParts().isEmpty()) {
                return candidate.getContent().getParts().get(0).getText();
            }
        }
        return null;
    }

    /**
     * Checks if the response was blocked.
     */
    public boolean isBlocked() {
        return promptFeedback != null && promptFeedback.getBlockReason() != null;
    }

    /**
     * Checks if the generation completed successfully.
     */
    public boolean isComplete() {
        return candidates != null && 
               !candidates.isEmpty() && 
               "STOP".equals(candidates.get(0).getFinishReason());
    }
}
