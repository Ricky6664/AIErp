package com.erp.auth.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 密码强度校验器单元测试.
 *
 * @author AI
 * @since 2026-06-04
 */
@DisplayName("密码强度校验器")
class PasswordValidatorTest {

    private final PasswordValidator validator = new PasswordValidator();

    @Nested
    @DisplayName("核心功能 — 合法密码")
    class ValidPasswords {

        @Test
        @DisplayName("包含四种字符类型的密码")
        void shouldAcceptPasswordWithAllFourTypes() {
            PasswordValidationResult result = validator.validate("Abc1234!", null);

            assertThat(result.isValid()).isTrue();
            assertThat(result.getErrors()).isEmpty();
            assertThat(result.getComplexity()).isEqualTo(4);
            assertThat(result.getStrength()).isEqualTo(PasswordStrength.VERY_STRONG);
        }

        @Test
        @DisplayName("四选三：大小写+数字")
        void shouldAcceptPasswordWithUpperLowerDigit() {
            PasswordValidationResult result = validator.validate("Abcdef12", null);

            assertThat(result.isValid()).isTrue();
            assertThat(result.getComplexity()).isEqualTo(3);
            assertThat(result.getStrength()).isEqualTo(PasswordStrength.STRONG);
        }

        @Test
        @DisplayName("四选三：大小写+特殊字符")
        void shouldAcceptPasswordWithUpperLowerSpecial() {
            PasswordValidationResult result = validator.validate("Abcdef!@", null);

            assertThat(result.isValid()).isTrue();
            assertThat(result.getComplexity()).isEqualTo(3);
            assertThat(result.getStrength()).isEqualTo(PasswordStrength.STRONG);
        }

        @Test
        @DisplayName("四选三：大写+数字+特殊字符")
        void shouldAcceptPasswordWithUpperDigitSpecial() {
            PasswordValidationResult result = validator.validate("ABCD1234!", null);

            assertThat(result.isValid()).isTrue();
            assertThat(result.getComplexity()).isEqualTo(3);
            assertThat(result.getStrength()).isEqualTo(PasswordStrength.STRONG);
        }

        @Test
        @DisplayName("四选三：小写+数字+特殊字符")
        void shouldAcceptPasswordWithLowerDigitSpecial() {
            PasswordValidationResult result = validator.validate("abcd1234!", null);

            assertThat(result.isValid()).isTrue();
            assertThat(result.getComplexity()).isEqualTo(3);
            assertThat(result.getStrength()).isEqualTo(PasswordStrength.STRONG);
        }

        @Test
        @DisplayName("恰好 8 位")
        void shouldAcceptMinLengthPassword() {
            PasswordValidationResult result = validator.validate("Abc1234!", null);

            assertThat(result.isValid()).isTrue();
        }

        @Test
        @DisplayName("恰好 32 位")
        void shouldAcceptMaxLengthPassword() {
            PasswordValidationResult result = validator.validate("Abc1234!Abc1234!Abc1234!Abc1234!", null);

            assertThat(result.isValid()).isTrue();
        }
    }

    @Nested
    @DisplayName("边界条件")
    class BoundaryConditions {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("空值或空字符串")
        void shouldRejectNullOrEmpty(String input) {
            PasswordValidationResult result = validator.validate(input, null);

            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrors()).contains("密码不能为空");
            assertThat(result.getComplexity()).isZero();
            assertThat(result.getStrength()).isEqualTo(PasswordStrength.WEAK);
        }

        @Test
        @DisplayName("7 位密码 — 低于最小长度")
        void shouldRejectTooShort() {
            PasswordValidationResult result = validator.validate("Abc123!", null);

            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrors()).contains("密码长度不能少于8位");
        }

        @Test
        @DisplayName("33 位密码 — 超过最大长度")
        void shouldRejectTooLong() {
            PasswordValidationResult result = validator.validate("Abc1234!Abc1234!Abc1234!Abc1234!X", null);

            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrors()).contains("密码长度不能超过32位");
        }

        @Test
        @DisplayName("仅两种字符类型")
        void shouldRejectOnlyTwoTypes() {
            PasswordValidationResult result = validator.validate("abcdefgh", null);

            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrors()).contains("密码必须包含大写字母、小写字母、数字、特殊字符中的至少三种");
            assertThat(result.getComplexity()).isEqualTo(1);
            assertThat(result.getStrength()).isEqualTo(PasswordStrength.WEAK);
        }

        @Test
        @DisplayName("username 为 null 时跳过用户名校验")
        void shouldSkipUsernameCheckWhenNull() {
            PasswordValidationResult result = validator.validate("Abc1234!", null);

            assertThat(result.isValid()).isTrue();
        }

        @Test
        @DisplayName("username 为空字符串时跳过用户名校验")
        void shouldSkipUsernameCheckWhenEmpty() {
            PasswordValidationResult result = validator.validate("Abc1234!", "");

            assertThat(result.isValid()).isTrue();
        }
    }

    @Nested
    @DisplayName("连续相同字符检测")
    class ConsecutiveChars {

        @Test
        @DisplayName("3 个连续相同字符")
        void shouldRejectThreeConsecutiveSameChars() {
            PasswordValidationResult result = validator.validate("Abc1112!", null);

            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrors()).contains("密码不能包含连续3位相同字符");
        }

        @Test
        @DisplayName("大于 3 个连续相同字符")
        void shouldRejectMoreThanThreeConsecutiveSameChars() {
            PasswordValidationResult result = validator.validate("Abc111112!", null);

            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrors()).contains("密码不能包含连续3位相同字符");
        }

        @Test
        @DisplayName("连续相同字母")
        void shouldRejectConsecutiveSameLetters() {
            PasswordValidationResult result = validator.validate("AAAbbc12!", null);

            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrors()).contains("密码不能包含连续3位相同字符");
        }

        @Test
        @DisplayName("2 个连续相同字符 — 允许")
        void shouldAllowTwoConsecutiveSameChars() {
            PasswordValidationResult result = validator.validate("Abc1123!", null);

            assertThat(result.isValid()).isTrue();
        }

        @Test
        @DisplayName("不连续相同字符 — 允许")
        void shouldAllowNonConsecutiveSameChars() {
            PasswordValidationResult result = validator.validate("A1b1c1d!", null);

            assertThat(result.isValid()).isTrue();
        }
    }

    @Nested
    @DisplayName("用户名匹配检测")
    class UsernameMatch {

        @Test
        @DisplayName("密码与用户名完全相同")
        void shouldRejectPasswordSameAsUsername() {
            PasswordValidationResult result = validator.validate("admin123", "admin123");

            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrors()).contains("密码不能与用户名相同");
        }

        @Test
        @DisplayName("密码与用户名忽略大小写相同")
        void shouldRejectPasswordCaseInsensitiveMatch() {
            PasswordValidationResult result = validator.validate("Admin123", "admin123");

            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrors()).contains("密码不能与用户名相同");
        }

        @Test
        @DisplayName("密码与用户名不同")
        void shouldAcceptPasswordDifferentFromUsername() {
            PasswordValidationResult result = validator.validate("MyP@ssw0rd", "admin123");

            assertThat(result.isValid()).isTrue();
        }
    }

    @Nested
    @DisplayName("强度等级映射")
    class StrengthMapping {

        @Test
        @DisplayName("complexity=0 → WEAK")
        void shouldMapZeroToWeak() {
            assertThat(PasswordStrength.fromComplexity(0)).isEqualTo(PasswordStrength.WEAK);
        }

        @Test
        @DisplayName("complexity=1 → WEAK")
        void shouldMapOneToWeak() {
            assertThat(PasswordStrength.fromComplexity(1)).isEqualTo(PasswordStrength.WEAK);
        }

        @Test
        @DisplayName("complexity=2 → MEDIUM")
        void shouldMapTwoToMedium() {
            assertThat(PasswordStrength.fromComplexity(2)).isEqualTo(PasswordStrength.MEDIUM);
        }

        @Test
        @DisplayName("complexity=3 → STRONG")
        void shouldMapThreeToStrong() {
            assertThat(PasswordStrength.fromComplexity(3)).isEqualTo(PasswordStrength.STRONG);
        }

        @Test
        @DisplayName("complexity=4 → VERY_STRONG")
        void shouldMapFourToVeryStrong() {
            assertThat(PasswordStrength.fromComplexity(4)).isEqualTo(PasswordStrength.VERY_STRONG);
        }
    }

    @Nested
    @DisplayName("多错误累积")
    class MultipleErrors {

        @Test
        @DisplayName("同时违反多个规则时累积所有错误")
        void shouldAccumulateAllErrors() {
            PasswordValidationResult result = validator.validate("abc", "abc");

            assertThat(result.isValid()).isFalse();
            assertThat(result.getErrors()).contains(
                    "密码长度不能少于8位",
                    "密码必须包含大写字母、小写字母、数字、特殊字符中的至少三种",
                    "密码不能与用户名相同"
            );
        }
    }

    @Nested
    @DisplayName("特殊字符检测")
    class SpecialCharDetection {

        @ParameterizedTest
        @ValueSource(strings = {"Abc1234!", "Abc1234@", "Abc1234#", "Abc1234$", "Abc1234%",
                "Abc1234^", "Abc1234&", "Abc1234*", "Abc1234(", "Abc1234)",
                "Abc1234_", "Abc1234+", "Abc1234-", "Abc1234=", "Abc1234[",
                "Abc1234]", "Abc1234{", "Abc1234}", "Abc1234|", "Abc1234;",
                "Abc1234:", "Abc1234,", "Abc1234.", "Abc1234<", "Abc1234>", "Abc1234?"})
        @DisplayName("各种特殊字符均可识别")
        void shouldDetectVariousSpecialChars(String password) {
            PasswordValidationResult result = validator.validate(password, null);

            assertThat(result.isValid()).isTrue();
            assertThat(result.getComplexity()).isEqualTo(4);
        }
    }
}
