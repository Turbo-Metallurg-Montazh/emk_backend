package com.kindred.emkcrm_project_backend.utils;

import com.kindred.emkcrm_project_backend.db.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UsernameGenerator {

    private final UserRepository userRepository;

    public UsernameGenerator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Генерирует уникальный username на основе ФИО.
     * Стратегия:
     *  - base:  firstInitial + "." + lastName  (например, m.nachinkin)
     *  - если занят и есть отчество: firstInitial + "." + middleInitial + "." + lastName (m.a.nachinkin)
     *  - если всё ещё занят: добавляем числовой суффикс k=1..N к последнему варианту (m.a.nachinkin1, m.a.nachinkin2, ...)
     */
    public String generateUniqueUsername(String firstName, String middleName, String lastName) {
        String first = transliterate(firstName).toLowerCase().trim();
        String middle = middleName != null ? transliterate(middleName).toLowerCase().trim() : "";
        String last = transliterate(lastName).toLowerCase().trim();

        if (first.isEmpty() || last.isEmpty()) {
            throw new IllegalArgumentException("First name and last name must not be empty");
        }

        String baseUsername = first.charAt(0) + "." + last;

        // Если отчество отсутствует, сразу переходим к числовым суффиксам
        String candidate = baseUsername;
        if (userRepository.findByUsername(candidate) == null) {
            return candidate;
        }

        if (!middle.isEmpty()) {
            String middleBased = first.charAt(0) + "." + middle.charAt(0) + "." + last;
            if (userRepository.findByUsername(middleBased) == null) {
                return middleBased;
            }
            candidate = middleBased;
        }

        int counter = 1;
        while (true) {
            String numbered = candidate + counter;
            if (userRepository.findByUsername(numbered) == null) {
                return numbered;
            }
            counter++;
        }
    }

    /**
     * Простейшая транслитерация кириллицы в латиницу.
     * Можно расширять по мере необходимости.
     */
    private String transliterate(String source) {
        if (source == null) {
            return "";
        }
        Map<Character, String> map = CYRILLIC_MAP;
        StringBuilder sb = new StringBuilder();
        for (char ch : source.toCharArray()) {
            char lower = Character.toLowerCase(ch);
            String repl = map.get(lower);
            if (repl != null) {
                sb.append(repl);
            } else if (Character.isLetterOrDigit(ch)) {
                sb.append(Character.toLowerCase(ch));
            } // пробелы и прочие символы просто пропускаем
        }
        return sb.toString();
    }

    private static final Map<Character, String> CYRILLIC_MAP = new HashMap<>();

    static {
        // Русский алфавит
        CYRILLIC_MAP.put('а', "a");
        CYRILLIC_MAP.put('б', "b");
        CYRILLIC_MAP.put('в', "v");
        CYRILLIC_MAP.put('г', "g");
        CYRILLIC_MAP.put('д', "d");
        CYRILLIC_MAP.put('е', "e");
        CYRILLIC_MAP.put('ё', "e");
        CYRILLIC_MAP.put('ж', "zh");
        CYRILLIC_MAP.put('з', "z");
        CYRILLIC_MAP.put('и', "i");
        CYRILLIC_MAP.put('й', "y");
        CYRILLIC_MAP.put('к', "k");
        CYRILLIC_MAP.put('л', "l");
        CYRILLIC_MAP.put('м', "m");
        CYRILLIC_MAP.put('н', "n");
        CYRILLIC_MAP.put('о', "o");
        CYRILLIC_MAP.put('п', "p");
        CYRILLIC_MAP.put('р', "r");
        CYRILLIC_MAP.put('с', "s");
        CYRILLIC_MAP.put('т', "t");
        CYRILLIC_MAP.put('у', "u");
        CYRILLIC_MAP.put('ф', "f");
        CYRILLIC_MAP.put('х', "kh");
        CYRILLIC_MAP.put('ц', "ts");
        CYRILLIC_MAP.put('ч', "ch");
        CYRILLIC_MAP.put('ш', "sh");
        CYRILLIC_MAP.put('щ', "shch");
        CYRILLIC_MAP.put('ъ', "");
        CYRILLIC_MAP.put('ы', "y");
        CYRILLIC_MAP.put('ь', "");
        CYRILLIC_MAP.put('э', "e");
        CYRILLIC_MAP.put('ю', "yu");
        CYRILLIC_MAP.put('я', "ya");
    }
}


