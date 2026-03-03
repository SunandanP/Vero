package com.blackbox.vero.rule_engine.engine.type_resolver;

import com.blackbox.vero.rule_engine.enums.ValueType;

/**
 * Strategy interface for resolving string values to typed objects.
 * Implementations should be annotated with @Component and provide
 * their supported ValueType via {@link #getSupportedType()}.
 *
 * @param <T> the target type this resolver produces
 */
public interface ValueTypeResolver<T> {
    
    /**
     * Resolves a string value to the target type.
     *
     * @param value the string value to resolve
     * @return the resolved typed value
     * @throws IllegalArgumentException if the value cannot be resolved
     */
    T resolve(String value);
    
    /**
     * Returns the ValueType this resolver supports.
     * Used by the registry for auto-discovery.
     *
     * @return the supported ValueType
     */
    ValueType getSupportedType();
}
