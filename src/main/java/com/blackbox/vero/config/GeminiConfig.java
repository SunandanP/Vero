package com.blackbox.vero.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "gemini")
@Data
public class GeminiConfig {

    private String apiKey;
    private String baseUrl = "https://generativelanguage.googleapis.com/v1beta";
    private String model = "gemini-2.0-flash";
    private Double temperature = 0.2;
    private Integer maxOutputTokens = 2048;

    public String getGenerateContentUrl() {
        return String.format("%s/models/%s:generateContent", baseUrl, model);
    }
}
