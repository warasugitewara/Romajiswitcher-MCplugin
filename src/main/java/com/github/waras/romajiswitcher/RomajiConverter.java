package com.github.waras.romajiswitcher;

import java.io.*;
import java.util.*;

/**
 * Comprehensive Japanese romanization to hiragana conversion engine.
 * Supports Hepburn, Kunrei, and hybrid romanization styles.
 * Handles: basic syllables, small kana (l/x prefix), sokuon (促音), chōonpu (長音)
 */
public class RomajiConverter {

    private static final String VOWELS = "aiueo";

    /**
     * Main romanization mapping with longest-match-first priority
     */
    private static final LinkedHashMap<String, String> ROMAJI_MAP = new LinkedHashMap<>();

    /**
     * Kanji mapping for common words (loaded from file or hardcoded)
     */
    private static final Map<String, String> KANJI_MAP = new HashMap<>();

    static {
        // Initialize basic hiragana mappings (3-char first for priority)
        // Small kana with l/x prefix
        ROMAJI_MAP.put("lya", "ゃ");
        ROMAJI_MAP.put("lyu", "ゅ");
        ROMAJI_MAP.put("lyo", "ょ");
        ROMAJI_MAP.put("xya", "ゃ");
        ROMAJI_MAP.put("xyu", "ゅ");
        ROMAJI_MAP.put("xyo", "ょ");
        ROMAJI_MAP.put("lwa", "ゎ");
        ROMAJI_MAP.put("xwa", "ゎ");
        ROMAJI_MAP.put("ltu", "っ");
        ROMAJI_MAP.put("xtu", "っ");
        ROMAJI_MAP.put("la", "ぁ");
        ROMAJI_MAP.put("li", "ぃ");
        ROMAJI_MAP.put("lu", "ぅ");
        ROMAJI_MAP.put("le", "ぇ");
        ROMAJI_MAP.put("lo", "ぉ");
        ROMAJI_MAP.put("xa", "ぁ");
        ROMAJI_MAP.put("xi", "ぃ");
        ROMAJI_MAP.put("xu", "ぅ");
        ROMAJI_MAP.put("xe", "ぇ");
        ROMAJI_MAP.put("xo", "ぉ");
        ROMAJI_MAP.put("lla", "ぁ");
        ROMAJI_MAP.put("lli", "ぃ");
        ROMAJI_MAP.put("llu", "ぅ");
        ROMAJI_MAP.put("lle", "ぇ");
        ROMAJI_MAP.put("llo", "ぉ");

        // Palatalized consonants (3-char)
        ROMAJI_MAP.put("kya", "きゃ");
        ROMAJI_MAP.put("kyu", "きゅ");
        ROMAJI_MAP.put("kyo", "きょ");
        ROMAJI_MAP.put("gya", "ぎゃ");
        ROMAJI_MAP.put("gyu", "ぎゅ");
        ROMAJI_MAP.put("gyo", "ぎょ");
        ROMAJI_MAP.put("sha", "しゃ");
        ROMAJI_MAP.put("shu", "しゅ");
        ROMAJI_MAP.put("sho", "しょ");
        ROMAJI_MAP.put("cha", "ちゃ");
        ROMAJI_MAP.put("chu", "ちゅ");
        ROMAJI_MAP.put("cho", "ちょ");
        ROMAJI_MAP.put("tya", "ちゃ");
        ROMAJI_MAP.put("tyu", "ちゅ");
        ROMAJI_MAP.put("tyo", "ちょ");
        ROMAJI_MAP.put("dya", "ぢゃ");
        ROMAJI_MAP.put("dyu", "ぢゅ");
        ROMAJI_MAP.put("dyo", "ぢょ");
        ROMAJI_MAP.put("jya", "じゃ");
        ROMAJI_MAP.put("jyu", "じゅ");
        ROMAJI_MAP.put("jyo", "じょ");
        ROMAJI_MAP.put("zya", "じゃ");
        ROMAJI_MAP.put("zyu", "じゅ");
        ROMAJI_MAP.put("zyo", "じょ");
        ROMAJI_MAP.put("nya", "にゃ");
        ROMAJI_MAP.put("nyu", "にゅ");
        ROMAJI_MAP.put("nyo", "にょ");
        ROMAJI_MAP.put("hya", "ひゃ");
        ROMAJI_MAP.put("hyu", "ひゅ");
        ROMAJI_MAP.put("hyo", "ひょ");
        ROMAJI_MAP.put("bya", "びゃ");
        ROMAJI_MAP.put("byu", "びゅ");
        ROMAJI_MAP.put("byo", "びょ");
        ROMAJI_MAP.put("pya", "ぴゃ");
        ROMAJI_MAP.put("pyu", "ぴゅ");
        ROMAJI_MAP.put("pyo", "ぴょ");
        ROMAJI_MAP.put("mya", "みゃ");
        ROMAJI_MAP.put("myu", "みゅ");
        ROMAJI_MAP.put("myo", "みょ");
        ROMAJI_MAP.put("rya", "りゃ");
        ROMAJI_MAP.put("ryu", "りゅ");
        ROMAJI_MAP.put("ryo", "りょ");

        // nn pattern and nn + vowel combinations (3-char priority)
        // These ensure that nna/nni/nnu/nne/nno are parsed as ん + na/ni/nu/ne/no
        ROMAJI_MAP.put("nna", "んな");
        ROMAJI_MAP.put("nni", "んに");
        ROMAJI_MAP.put("nnu", "んぬ");
        ROMAJI_MAP.put("nne", "んね");
        ROMAJI_MAP.put("nno", "んの");

        // Special Kunrei variants (3-char)
        ROMAJI_MAP.put("tsa", "つぁ");
        ROMAJI_MAP.put("tse", "つぇ");
        ROMAJI_MAP.put("tsi", "つぃ");
        ROMAJI_MAP.put("tsu", "つ");
        ROMAJI_MAP.put("tso", "つぉ");
        ROMAJI_MAP.put("dzi", "ぢ");
        ROMAJI_MAP.put("dze", "ぢぇ");
        ROMAJI_MAP.put("ju", "じゅ");
        ROMAJI_MAP.put("zi", "じ");
        ROMAJI_MAP.put("ze", "ぜ");

        // Basic consonants (2-char)
        ROMAJI_MAP.put("ka", "か");
        ROMAJI_MAP.put("ki", "き");
        ROMAJI_MAP.put("ku", "く");
        ROMAJI_MAP.put("ke", "け");
        ROMAJI_MAP.put("ko", "こ");
        ROMAJI_MAP.put("ga", "が");
        ROMAJI_MAP.put("gi", "ぎ");
        ROMAJI_MAP.put("gu", "ぐ");
        ROMAJI_MAP.put("ge", "げ");
        ROMAJI_MAP.put("go", "ご");
        ROMAJI_MAP.put("sa", "さ");
        ROMAJI_MAP.put("si", "し");
        ROMAJI_MAP.put("su", "す");
        ROMAJI_MAP.put("se", "せ");
        ROMAJI_MAP.put("so", "そ");
        ROMAJI_MAP.put("za", "ざ");
        ROMAJI_MAP.put("zi", "じ");
        ROMAJI_MAP.put("zu", "ず");
        ROMAJI_MAP.put("ze", "ぜ");
        ROMAJI_MAP.put("zo", "ぞ");
        ROMAJI_MAP.put("ta", "た");
        ROMAJI_MAP.put("ti", "ち");
        ROMAJI_MAP.put("tu", "つ");
        ROMAJI_MAP.put("te", "て");
        ROMAJI_MAP.put("to", "と");
        ROMAJI_MAP.put("da", "だ");
        ROMAJI_MAP.put("di", "ぢ");
        ROMAJI_MAP.put("du", "づ");
        ROMAJI_MAP.put("de", "で");
        ROMAJI_MAP.put("do", "ど");
        ROMAJI_MAP.put("na", "な");
        ROMAJI_MAP.put("ni", "に");
        ROMAJI_MAP.put("nu", "ぬ");
        ROMAJI_MAP.put("ne", "ね");
        ROMAJI_MAP.put("no", "の");
        ROMAJI_MAP.put("ha", "は");
        ROMAJI_MAP.put("hi", "ひ");
        ROMAJI_MAP.put("hu", "ふ");
        ROMAJI_MAP.put("he", "へ");
        ROMAJI_MAP.put("ho", "ほ");
        ROMAJI_MAP.put("ba", "ば");
        ROMAJI_MAP.put("bi", "び");
        ROMAJI_MAP.put("bu", "ぶ");
        ROMAJI_MAP.put("be", "べ");
        ROMAJI_MAP.put("bo", "ぼ");
        ROMAJI_MAP.put("pa", "ぱ");
        ROMAJI_MAP.put("pi", "ぴ");
        ROMAJI_MAP.put("pu", "ぷ");
        ROMAJI_MAP.put("pe", "ぺ");
        ROMAJI_MAP.put("po", "ぽ");
        ROMAJI_MAP.put("ma", "ま");
        ROMAJI_MAP.put("mi", "み");
        ROMAJI_MAP.put("mu", "む");
        ROMAJI_MAP.put("me", "め");
        ROMAJI_MAP.put("mo", "も");
        ROMAJI_MAP.put("ya", "や");
        ROMAJI_MAP.put("yu", "ゆ");
        ROMAJI_MAP.put("yo", "よ");
        ROMAJI_MAP.put("ra", "ら");
        ROMAJI_MAP.put("ri", "り");
        ROMAJI_MAP.put("ru", "る");
        ROMAJI_MAP.put("re", "れ");
        ROMAJI_MAP.put("ro", "ろ");
        ROMAJI_MAP.put("wa", "わ");
        ROMAJI_MAP.put("wi", "ゐ");
        ROMAJI_MAP.put("we", "ゑ");
        ROMAJI_MAP.put("wo", "を");
        ROMAJI_MAP.put("nn", "ん");

        // Vowels (1-char)
        ROMAJI_MAP.put("a", "あ");
        ROMAJI_MAP.put("i", "い");
        ROMAJI_MAP.put("u", "う");
        ROMAJI_MAP.put("e", "え");
        ROMAJI_MAP.put("o", "お");

        // Load kanji dictionary
        loadKanjiDictionary();
    }

    /**
     * Load kanji dictionary from resources
     */
    private static void loadKanjiDictionary() {
        try {
            InputStream inputStream = RomajiConverter.class.getClassLoader()
                    .getResourceAsStream("kanji_dictionary.txt");
            
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        KANJI_MAP.put(parts[0].trim(), parts[1].trim());
                    }
                }
                reader.close();
            } else {
                // Fallback: use hardcoded basic kanji
                loadDefaultKanji();
            }
        } catch (Exception e) {
            System.err.println("Failed to load kanji dictionary: " + e.getMessage());
            loadDefaultKanji();
        }
    }

    /**
     * Fallback default kanji mappings
     */
    private static void loadDefaultKanji() {
        KANJI_MAP.put("arigatou", "有難う");
        KANJI_MAP.put("arigatai", "有難い");
        KANJI_MAP.put("osusume", "お勧め");
        KANJI_MAP.put("sugoi", "凄い");
        KANJI_MAP.put("mazui", "不味い");
        KANJI_MAP.put("oishii", "美味しい");
        KANJI_MAP.put("kawaii", "可愛い");
        KANJI_MAP.put("atarashii", "新しい");
        KANJI_MAP.put("furui", "古い");
        KANJI_MAP.put("okii", "大きい");
        KANJI_MAP.put("tiisai", "小さい");
        KANJI_MAP.put("chiisai", "小さい");
        KANJI_MAP.put("hayai", "速い");
        KANJI_MAP.put("osoi", "遅い");
        KANJI_MAP.put("tsuyoi", "強い");
        KANJI_MAP.put("yowai", "弱い");
        KANJI_MAP.put("takai", "高い");
        KANJI_MAP.put("hikui", "低い");
        KANJI_MAP.put("tokyo", "東京");
        KANJI_MAP.put("osaka", "大阪");
        KANJI_MAP.put("kyoto", "京都");
        KANJI_MAP.put("onegai", "お願い");
        KANJI_MAP.put("oyasuminasai", "お休みなさい");
        KANJI_MAP.put("oyasumi", "お休み");
        KANJI_MAP.put("ohayougozaimasu", "おはようございます");
        KANJI_MAP.put("ohayou", "おはよう");
        KANJI_MAP.put("konnichiwa", "こんにちは");
        KANJI_MAP.put("konbanwa", "こんばんは");
        KANJI_MAP.put("domo", "どうも");
        KANJI_MAP.put("arigatougozaimasu", "ありがとうございます");
        KANJI_MAP.put("gakkou", "学校");
        KANJI_MAP.put("sensei", "先生");
        KANJI_MAP.put("gakusei", "学生");
        KANJI_MAP.put("daigaku", "大学");
    }

    /**
     * Conversion result that preserves original romaji for display
     */
    public static class ConversionResult {
        public final String japanese;
        public final String originalRomaji;

        public ConversionResult(String japanese, String originalRomaji) {
            this.japanese = japanese;
            this.originalRomaji = originalRomaji;
        }

        public String getFormattedText() {
            return japanese + "(" + originalRomaji + ")";
        }
    }

    /**
     * Convert input text word by word
     */
    public static ConversionResult convert(String input) {
        if (input == null || input.isEmpty()) {
            return new ConversionResult("", "");
        }

        StringBuilder japaneseText = new StringBuilder();
        StringBuilder originalText = new StringBuilder();

        String[] words = input.split("(?=\\s)|(?<=\\s)");

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }

            if (word.charAt(0) == ' ') {
                japaneseText.append(' ');
                originalText.append(' ');
            } else {
                ConversionResult wordResult = convertWord(word);
                japaneseText.append(wordResult.japanese);
                originalText.append(wordResult.originalRomaji);
            }
        }

        return new ConversionResult(japaneseText.toString(), originalText.toString());
    }

    /**
     * Convert single word with kanji support
     */
    public static ConversionResult convertWord(String word) {
        if (word == null || word.isEmpty()) {
            return new ConversionResult("", "");
        }

        String lowerWord = word.toLowerCase();

        // Check kanji dictionary first
        if (KANJI_MAP.containsKey(lowerWord)) {
            return new ConversionResult(KANJI_MAP.get(lowerWord), word);
        }

        // Fall back to character-by-character conversion
        return convertWordWithResult(lowerWord, word);
    }

    /**
     * Convert word character-by-character while preserving original case
     */
    private static ConversionResult convertWordWithResult(String lowerWord, String originalWord) {
        StringBuilder japanese = new StringBuilder();
        StringBuilder romaji = new StringBuilder();
        int i = 0;

        while (i < lowerWord.length()) {
            char current = lowerWord.charAt(i);

            // Try 3-character match first
            if (i + 3 <= lowerWord.length()) {
                String threeChar = lowerWord.substring(i, i + 3);
                if (ROMAJI_MAP.containsKey(threeChar)) {
                    japanese.append(ROMAJI_MAP.get(threeChar));
                    romaji.append(originalWord.substring(i, i + 3).toLowerCase());
                    i += 3;
                    continue;
                }
            }

            // Try 2-character match
            if (i + 2 <= lowerWord.length()) {
                String twoChar = lowerWord.substring(i, i + 2);
                if (ROMAJI_MAP.containsKey(twoChar)) {
                    // Special handling for 'n' + vowel/y: only match if NOT followed by another vowel/y
                    // This distinguishes between "na" (な) and "n" + vowel (ん + vowel)
                    if (twoChar.charAt(0) == 'n' && "aiueoy".indexOf(twoChar.charAt(1)) >= 0) {
                        // This is n + vowel (na/ni/nu/ne/no)
                        // Check if next char exists and is NOT a vowel/y (which would make it standalone n)
                        if (i + 2 < lowerWord.length()) {
                            char nextChar = lowerWord.charAt(i + 2);
                            if ("aiueoy".indexOf(nextChar) >= 0) {
                                // Next char is vowel/y, so this n is standalone (ん)
                                // Fall through to handle single 'n'
                            } else {
                                // Next char is consonant or nothing, so na/ni/nu/ne/no is intended
                                japanese.append(ROMAJI_MAP.get(twoChar));
                                romaji.append(originalWord.substring(i, i + 2).toLowerCase());
                                i += 2;
                                continue;
                            }
                        } else {
                            // End of word, so na/ni/nu/ne/no is intended
                            japanese.append(ROMAJI_MAP.get(twoChar));
                            romaji.append(originalWord.substring(i, i + 2).toLowerCase());
                            i += 2;
                            continue;
                        }
                    } else {
                        // Not n + vowel, match normally
                        japanese.append(ROMAJI_MAP.get(twoChar));
                        romaji.append(originalWord.substring(i, i + 2).toLowerCase());
                        i += 2;
                        continue;
                    }
                }
            }

            // Try 1-character match
            if (ROMAJI_MAP.containsKey(String.valueOf(current))) {
                if (current != 'n') {
                    // Regular single character (not 'n')
                    japanese.append(ROMAJI_MAP.get(String.valueOf(current)));
                    romaji.append(originalWord.charAt(i));
                    i++;
                } else {
                    // For 'n' check - never gets here since 'n' is not in ROMAJI_MAP as single char
                    i++;
                }
                continue;
            }

            // Handle 'n' specially (standalone ん or part of na/ni/nu/ne/no already handled above)
            if (current == 'n') {
                japanese.append("ん");
                romaji.append(originalWord.charAt(i));
                i++;
                continue;
            }

            // Handle sokuon (促音) - doubled consonant
            if (i + 1 < lowerWord.length() && current == lowerWord.charAt(i + 1) && current != 'n' && current != 'y') {
                String remaining = lowerWord.substring(i + 1);
                if (canStartWithConsonant(remaining)) {
                    japanese.append("っ");
                    romaji.append(originalWord.charAt(i));
                    i++;
                    continue;
                }
            }

            // Unmatched character - keep as is
            japanese.append(current);
            romaji.append(originalWord.charAt(i));
            i++;
        }

        return new ConversionResult(japanese.toString(), romaji.toString());
    }

    private static boolean isVowelOrY(char c) {
        return "aiueoy".indexOf(c) >= 0;
    }

    /**
     * Check if a romaji sequence can start with the next consonant
     */
    private static boolean canStartWithConsonant(String remaining) {
        if (remaining.isEmpty()) {
            return false;
        }

        String[] patterns = {
            "ka", "ki", "ku", "ke", "ko",
            "ga", "gi", "gu", "ge", "go",
            "sa", "si", "su", "se", "so",
            "ta", "ti", "tu", "te", "to",
            "da", "di", "du", "de", "do",
            "pa", "pi", "pu", "pe", "po",
            "ba", "bi", "bu", "be", "bo",
            "ma", "mi", "mu", "me", "mo",
            "ha", "hi", "hu", "he", "ho",
            "na", "ni", "nu", "ne", "no",
            "ra", "ri", "ru", "re", "ro",
            "ya", "yu", "yo",
            "wa", "wo",
            "tsu", "tsa", "tsi", "tse", "tso",
            "cha", "chu", "cho",
            "sha", "shu", "sho",
            "kya", "kyu", "kyo",
            "gya", "gyu", "gyo",
            "nya", "nyu", "nyo",
            "hya", "hyu", "hyo",
            "mya", "myu", "myo",
            "rya", "ryu", "ryo",
            "pya", "pyu", "pyo",
            "bya", "byu", "byo"
        };

        for (String pattern : patterns) {
            if (remaining.startsWith(pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add a custom kanji entry to the dictionary
     */
    public static void addKanjiEntry(String romaji, String kanji) {
        KANJI_MAP.put(romaji.toLowerCase(), kanji);
    }

    /**
     * Remove a kanji entry from the dictionary
     */
    public static boolean removeKanjiEntry(String romaji) {
        return KANJI_MAP.remove(romaji.toLowerCase()) != null;
    }

    /**
     * Get all kanji entries
     */
    public static Map<String, String> getKanjiEntries() {
        return new HashMap<>(KANJI_MAP);
    }

    /**
     * Get a specific kanji entry
     */
    public static String getKanjiEntry(String romaji) {
        return KANJI_MAP.get(romaji.toLowerCase());
    }
}
