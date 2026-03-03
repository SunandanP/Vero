package com.blackbox.vero.rule_engine.engine.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Builder;
import lombok.Data;

/**
 * Context object for rule evaluation containing facts and metadata.
 * Provides extensible access to multiple fields, caching, and external lookups.
 */
@Data
@Builder
public class EvaluationContext {

    @Builder.Default
    private final Map<String, Object> facts = new HashMap<>();

    @Builder.Default
    private final Map<String, Object> metadata = new HashMap<>();

    @Builder.Default
    private final Map<String, Boolean> cache = new HashMap<>();

    /**
     * Gets a fact value by field name.
     *
     * @param fieldName the field name
     * @return the fact value as String, or null if not found
     */
    public String getFact(String fieldName) {
        Object value = facts.get(fieldName);
        return value != null ? value.toString() : null;
    }

    /**
     * Gets a fact value with type casting.
     *
     * @param fieldName the field name
     * @param type the expected type
     * @param <T> the type parameter
     * @return Optional containing the value if present and of correct type
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getFact(String fieldName, Class<T> type) {
        Object value = facts.get(fieldName);
        if (value != null && type.isInstance(value)) {
            return Optional.of((T) value);
        }
        return Optional.empty();
    }

    /**
     * Sets a fact value.
     *
     * @param fieldName the field name
     * @param value the value
     * @return this context for chaining
     */
    public EvaluationContext withFact(String fieldName, Object value) {
        facts.put(fieldName, value);
        return this;
    }

    /**
     * Sets multiple facts from a map.
     *
     * @param facts the facts map
     * @return this context for chaining
     */
    public EvaluationContext withFacts(Map<String, Object> facts) {
        this.facts.putAll(facts);
        return this;
    }

    /**
     * Checks if a fact exists.
     *
     * @param fieldName the field name
     * @return true if the fact exists
     */
    public boolean hasFact(String fieldName) {
        return facts.containsKey(fieldName);
    }

    /**
     * Gets a cached evaluation result.
     *
     * @param cacheKey the cache key
     * @return Optional containing the cached result if present
     */
    public Optional<Boolean> getCached(String cacheKey) {
        return Optional.ofNullable(cache.get(cacheKey));
    }

    /**
     * Caches an evaluation result.
     *
     * @param cacheKey the cache key
     * @param result the result to cache
     */
    public void cache(String cacheKey, Boolean result) {
        cache.put(cacheKey, result);
    }

    /**
     * Gets metadata value.
     *
     * @param key the metadata key
     * @return the metadata value or null
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    /**
     * Sets metadata value.
     *
     * @param key the metadata key
     * @param value the metadata value
     * @return this context for chaining
     */
    public EvaluationContext withMetadata(String key, Object value) {
        metadata.put(key, value);
        return this;
    }

    /**
     * Creates a simple context with a single fact.
     *
     * @param fieldName the field name
     * @param value the value
     * @return new EvaluationContext
     */
    public static EvaluationContext of(String fieldName, Object value) {
        return EvaluationContext.builder()
                .build()
                .withFact(fieldName, value);
    }

    /**
     * Creates a context from a facts map.
     *
     * @param facts the facts map
     * @return new EvaluationContext
     */
    public static EvaluationContext of(Map<String, Object> facts) {
        return EvaluationContext.builder()
                .build()
                .withFacts(facts);
    }
}
