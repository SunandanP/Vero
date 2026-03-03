package com.blackbox.vero.rule_engine.engine.registry;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.blackbox.vero.rule_engine.engine.type_resolver.ValueTypeResolver;
import com.blackbox.vero.rule_engine.enums.ValueType;

import lombok.Data;

/**
 * Registry for ValueTypeResolver implementations.
 * Uses Spring's auto-discovery to automatically register all
 * ValueTypeResolver beans based on their supported type.
 */
@Component
@Data
public class ValueTypeResolverRegistry {
    
    private final Map<ValueType, ValueTypeResolver<?>> resolvers;

    /**
     * Constructor that auto-discovers and registers all ValueTypeResolver beans.
     *
     * @param resolverList all ValueTypeResolver beans discovered by Spring
     */
    public ValueTypeResolverRegistry(List<ValueTypeResolver<?>> resolverList) {
        this.resolvers = new EnumMap<>(ValueType.class);
        
        for (ValueTypeResolver<?> resolver : resolverList) {
            ValueType type = resolver.getSupportedType();
            if (resolvers.containsKey(type)) {
                throw new IllegalStateException(
                    "Duplicate ValueTypeResolver for type " + type + 
                    ": " + resolvers.get(type).getClass().getSimpleName() + 
                    " and " + resolver.getClass().getSimpleName()
                );
            }
            resolvers.put(type, resolver);
        }
    }

    /**
     * Gets the resolver for the specified value type.
     *
     * @param valueType the value type to resolve
     * @return the resolver for the specified type
     * @throws IllegalArgumentException if no resolver is registered for the type
     */
    public ValueTypeResolver<?> getResolver(ValueType valueType) {
        ValueTypeResolver<?> resolver = resolvers.get(valueType);
        if (resolver == null) {
            throw new IllegalArgumentException(
                "No ValueTypeResolver registered for type: " + valueType
            );
        }
        return resolver;
    }
    
    /**
     * Checks if a resolver is registered for the specified type.
     *
     * @param valueType the value type to check
     * @return true if a resolver is registered, false otherwise
     */
    public boolean hasResolver(ValueType valueType) {
        return resolvers.containsKey(valueType);
    }
}
