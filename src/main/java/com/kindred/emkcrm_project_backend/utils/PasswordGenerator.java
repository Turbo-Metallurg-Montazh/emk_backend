package com.kindred.emkcrm_project_backend.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PasswordGenerator {

    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String ALL_CHARS = UPPER_CASE + LOWER_CASE + DIGITS;
    
    private static final int DEFAULT_LENGTH = 12;
    private final SecureRandom random;

    public PasswordGenerator() {
        this.random = new SecureRandom();
    }

    /**
     * Генерирует безопасный пароль заданной длины.
     * Пароль содержит минимум по одному символу из каждой категории:
     * заглавные буквы, строчные буквы, цифры.
     */
    public String generatePassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters");
        }

        StringBuilder password = new StringBuilder(length);
        
        // Гарантируем наличие хотя бы одного символа из каждой категории
        password.append(UPPER_CASE.charAt(random.nextInt(UPPER_CASE.length())));
        password.append(LOWER_CASE.charAt(random.nextInt(LOWER_CASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        
        // Заполняем оставшиеся позиции случайными символами
        for (int i = password.length(); i < length; i++) {
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }
        
        // Перемешиваем символы для случайного порядка
        return shuffleString(password.toString());
    }

    /**
     * Генерирует пароль стандартной длины (12 символов).
     */
    public String generatePassword() {
        return generatePassword(DEFAULT_LENGTH);
    }

    /**
     * Перемешивает символы в строке случайным образом.
     */
    private String shuffleString(String str) {
        char[] chars = str.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}

