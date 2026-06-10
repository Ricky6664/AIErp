package com.erp.auth.validation;

/**
 * 密码强度等级.
 *
 * @author AI
 * @since 2026-06-04
 */
public enum PasswordStrength {

    WEAK("弱"),
    MEDIUM("中"),
    STRONG("强"),
    VERY_STRONG("极强");

    private final String label;

    PasswordStrength(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static PasswordStrength fromComplexity(int complexity) {
        if (complexity <= 1) {
            return WEAK;
        }
        if (complexity == 2) {
            return MEDIUM;
        }
        if (complexity == 3) {
            return STRONG;
        }
        return VERY_STRONG;
    }
}
