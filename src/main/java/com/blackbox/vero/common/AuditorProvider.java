package com.blackbox.vero.common;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
public class AuditorProvider implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        // Option 1: Spring Security
        // return Optional.of(SecurityContextHolder.getContext().getAuthentication().getName());

        // Option 2: Temporary default
        return Optional.of("SYSTEM");
    }
}
