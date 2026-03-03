package com.blackbox.vero.ai.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.blackbox.vero.ai.dto.GeminiResponse;
import com.blackbox.vero.ai.exception.GeminiApiException;
import com.blackbox.vero.config.GeminiConfig;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    private final RestClient restClient;
    private final GeminiConfig config;

    public GeminiService(RestClient geminiRestClient, GeminiConfig config) {
        this.restClient = geminiRestClient;
        this.config = config;
    }

    public GeminiResponse generateContent(String prompt) {
        String url = config.getGenerateContentUrl();
        log.info("Calling Gemini API - URL: {}", url);

        Map<String, Object> body = buildRequestBody(prompt);

        try {
            GeminiResponse response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-goog-api-key", config.getApiKey())
                    .body(body)
                    .retrieve()
                    .body(GeminiResponse.class);

            if (response == null) {
                throw new GeminiApiException("Received null response from Gemini API");
            }

            if (response.isBlocked()) {
                throw new GeminiApiException("Request was blocked by Gemini safety filters");
            }

            log.debug("Received response from Gemini: {}", truncateForLog(response.getText()));
            return response;

        } catch (GeminiApiException e) {
            throw e;
        } catch (HttpClientErrorException e) {
            log.error("Gemini API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new GeminiApiException(
                    String.format("Gemini API returned %s: %s", e.getStatusCode(), e.getResponseBodyAsString()), e);
        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            throw new GeminiApiException("Failed to communicate with Gemini API: " + e.getMessage(), e);
        }
    }

    public String generateText(String prompt) {
        GeminiResponse response = generateContent(prompt);
        String text = response.getText();
        if (text == null || text.isBlank()) {
            throw new GeminiApiException("Gemini returned empty response");
        }
        return text;
    }

    public GeminiResponse generateContent(String prompt, Double temperature, Integer maxTokens) {
        String url = config.getGenerateContentUrl();

        Map<String, Object> body = buildRequestBodyWithConfig(prompt, temperature, maxTokens);

        try {
            GeminiResponse response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-goog-api-key", config.getApiKey())
                    .body(body)
                    .retrieve()
                    .body(GeminiResponse.class);

            if (response == null) {
                throw new GeminiApiException("Received null response from Gemini API");
            }

            return response;

        } catch (GeminiApiException e) {
            throw e;
        } catch (HttpClientErrorException e) {
            log.error("Gemini API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new GeminiApiException(
                    String.format("Gemini API returned %s: %s", e.getStatusCode(), e.getResponseBodyAsString()), e);
        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            throw new GeminiApiException("Failed to communicate with Gemini API: " + e.getMessage(), e);
        }
    }

    /**
     * Builds the request body matching the exact curl structure:
     * { "contents": [{ "parts": [{ "text": "..." }] }] }
     */
    private Map<String, Object> buildRequestBody(String prompt) {
        return Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)))));
    }

    private Map<String, Object> buildRequestBodyWithConfig(String prompt, Double temperature, Integer maxTokens) {
        return Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)))),
                "generationConfig", Map.of(
                        "temperature", temperature,
                        "maxOutputTokens", maxTokens));
    }

    private String truncateForLog(String text) {
        if (text == null) return "null";
        return text.length() > 200 ? text.substring(0, 200) + "..." : text;
    }
}
