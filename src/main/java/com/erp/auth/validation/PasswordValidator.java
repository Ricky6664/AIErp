package com.erp.auth.validation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * 密码强度校验器.
 *
 * <p>校验规则:
 * <ul>
 *   <li>长度: 8-32 位</li>
 *   <li>复杂度: 大写字母、小写字母、数字、特殊字符四选三</li>
 *   <li>禁止连续 3 位相同字符</li>
 *   <li>禁止与用户名相同（忽略大小写）</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-06-04
 */
@Component
public class PasswordValidator {

    private static final int MIN_LENGTH = 8;

    private static final int MAX_LENGTH = 32;

    private static final String SPECIAL_CHARS = "!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?";

    public PasswordValidationResult validate(String password, String username) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.isEmpty()) {
            errors.add("密码不能为空");
            return PasswordValidationResult.failure(errors, 0);
        }

        if (password.length() < MIN_LENGTH) {
            errors.add("密码长度不能少于8位");
        }
        if (password.length() > MAX_LENGTH) {
            errors.add("密码长度不能超过32位");
        }

        int complexity = 0;
        if (password.matches(".*[A-Z].*")) {
            complexity++;
        }
        if (password.matches(".*[a-z].*")) {
            complexity++;
        }
        if (password.matches(".*\\d.*")) {
            complexity++;
        }
        if (containsSpecialChar(password)) {
            complexity++;
        }

        if (complexity < 3) {
            errors.add("密码必须包含大写字母、小写字母、数字、特殊字符中的至少三种");
        }

        if (hasConsecutiveIdenticalChars(password, 3)) {
            errors.add("密码不能包含连续3位相同字符");
        }

        if (username != null && !username.isEmpty() && password.equalsIgnoreCase(username)) {
            errors.add("密码不能与用户名相同");
        }

        if (errors.isEmpty()) {
            return PasswordValidationResult.success(complexity);
        }
        return PasswordValidationResult.failure(errors, complexity);
    }

    private boolean containsSpecialChar(String password) {
        for (char c : password.toCharArray()) {
            if (SPECIAL_CHARS.indexOf(c) >= 0) {
                return true;
            }
        }
        return false;
    }

    private boolean hasConsecutiveIdenticalChars(String password, int threshold) {
        if (password.length() < threshold) {
            return false;
        }
        int count = 1;
        for (int i = 1; i < password.length(); i++) {
            if (password.charAt(i) == password.charAt(i - 1)) {
                count++;
                if (count >= threshold) {
                    return true;
                }
            } else {
                count = 1;
            }
        }
        return false;
    }
}
