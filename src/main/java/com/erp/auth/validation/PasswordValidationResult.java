package com.erp.auth.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 密码校验结果.
 *
 * @author AI
 * @since 2026-06-04
 */
public class PasswordValidationResult {

    private final boolean valid;

    private final List<String> errors;

    private final PasswordStrength strength;

    private final int complexity;

    public PasswordValidationResult(boolean valid, List<String> errors, int complexity) {
        this.valid = valid;
        this.errors = Collections.unmodifiableList(new ArrayList<>(errors));
        this.complexity = complexity;
        this.strength = PasswordStrength.fromComplexity(complexity);
    }

    public static PasswordValidationResult success(int complexity) {
        return new PasswordValidationResult(true, Collections.emptyList(), complexity);
    }

    public static PasswordValidationResult failure(List<String> errors, int complexity) {
        return new PasswordValidationResult(false, errors, complexity);
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public PasswordStrength getStrength() {
        return strength;
    }

    public int getComplexity() {
        return complexity;
    }
}
