package com.blackbox.vero.rule_engine.exception;

public class RuleEngineValidationError extends RuntimeException {
    public RuleEngineValidationError(String message) {
        super(message);
    }

    public RuleEngineValidationError(String message, Throwable cause) {
        super(message, cause);
    }

    public RuleEngineValidationError(Throwable cause) {
        super(cause);
    }
}
