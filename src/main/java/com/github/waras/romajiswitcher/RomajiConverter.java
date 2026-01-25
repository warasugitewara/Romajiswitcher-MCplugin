package com.github.waras.romajiswitcher;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Romaji to Japanese (Hiragana/Kanji) converter
 * Complete support for:
 * - Hepburn (ヘボン式) and Kunrei (訓令式) romanization
 * - Sokuon (促音) - small tsu (っ)
 * - Long vowels (長音) - aa, ii, uu, ee, oo, ei, ou
 * - Small kana (小書き仮名) - lXX or xXX for all sounds
 * - Kanji conversion for common words (LunaChat inspired)
 * - Space handling: aiu eo -> あいう　えお (aiu eo)
 * Examples: eenala -> ええなぁ, arigatou -> 有難う, a i u e o -> あ い う え お
 */
public class RomajiConverter {

    private static final Map<String, String> ROMAJI_MAP = new LinkedHashMap<>();
    private static final Map<String, String> KANJI_MAP = new LinkedHashMap<>();
    private static final Map<String, String> SMALL_KANA_MAP = new HashMap<>();

    static {
        // Initialize small kana map
        SMALL_KANA_MAP.put("あ", "ぁ");
        SMALL_KANA_MAP.put("い", "ぃ");
        SMALL_KANA_MAP.put("う", "ぅ");
        SMALL_KANA_MAP.put("え", "ぇ");
        SMALL_KANA_MAP.put("お", "ぉ");
        SMALL_KANA_MAP.put("や", "ゃ");
        SMALL_KANA_MAP.put("ゆ", "ゅ");
        SMALL_KANA_MAP.put("よ", "ょ");
        SMALL_KANA_MAP.put("わ", "ゎ");
        SMALL_KANA_MAP.put("つ", "っ");

        // Initialize kanji dictionary (common words)
        initializeKanjiMap();

        // 3-character combinations (palatalized sounds)
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
        
        ROMAJI_MAP.put("ja", "じゃ");
        ROMAJI_MAP.put("ju", "じゅ");
        ROMAJI_MAP.put("jo", "じょ");
        ROMAJI_MAP.put("zya", "じゃ");
        ROMAJI_MAP.put("zyu", "じゅ");
        ROMAJI_MAP.put("zyo", "じょ");

        // 2-character combinations (Hepburn style)
        ROMAJI_MAP.put("shi", "し");
        ROMAJI_MAP.put("chi", "ち");
        ROMAJI_MAP.put("tsu", "つ");
        ROMAJI_MAP.put("fu", "ふ");
        
        // 2-character combinations (Kunrei style)
        ROMAJI_MAP.put("si", "し");
        ROMAJI_MAP.put("ti", "ち");
        ROMAJI_MAP.put("tu", "つ");
        ROMAJI_MAP.put("zi", "じ");
        ROMAJI_MAP.put("hu", "ふ");
        
        // Long vowels (double vowels)
        ROMAJI_MAP.put("aa", "ああ");
        ROMAJI_MAP.put("ii", "いい");
        ROMAJI_MAP.put("uu", "うう");
        ROMAJI_MAP.put("ee", "ええ");
        ROMAJI_MAP.put("oo", "おお");
        ROMAJI_MAP.put("ou", "おう");
        ROMAJI_MAP.put("ei", "えい");
        
        // 2-character combinations
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
        ROMAJI_MAP.put("su", "す");
        ROMAJI_MAP.put("se", "せ");
        ROMAJI_MAP.put("so", "そ");
        ROMAJI_MAP.put("za", "ざ");
        ROMAJI_MAP.put("zu", "ず");
        ROMAJI_MAP.put("ze", "ぜ");
        ROMAJI_MAP.put("zo", "ぞ");
        
        ROMAJI_MAP.put("ta", "た");
        ROMAJI_MAP.put("te", "て");
        ROMAJI_MAP.put("to", "と");
        ROMAJI_MAP.put("da", "だ");
        ROMAJI_MAP.put("de", "で");
        ROMAJI_MAP.put("do", "ど");
        ROMAJI_MAP.put("di", "ぢ");
        ROMAJI_MAP.put("du", "づ");
        
        ROMAJI_MAP.put("na", "な");
        ROMAJI_MAP.put("ni", "に");
        ROMAJI_MAP.put("nu", "ぬ");
        ROMAJI_MAP.put("ne", "ね");
        ROMAJI_MAP.put("no", "の");
        
        ROMAJI_MAP.put("ha", "は");
        ROMAJI_MAP.put("hi", "ひ");
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
        ROMAJI_MAP.put("wo", "を");
        ROMAJI_MAP.put("we", "ゑ");
        
        // Foreign sounds
        ROMAJI_MAP.put("fa", "ふぁ");
        ROMAJI_MAP.put("fi", "ふぃ");
        ROMAJI_MAP.put("fe", "ふぇ");
        ROMAJI_MAP.put("fo", "ふぉ");
        ROMAJI_MAP.put("va", "ゔぁ");
        ROMAJI_MAP.put("vi", "ゔぃ");
        ROMAJI_MAP.put("vu", "ゔ");
        ROMAJI_MAP.put("ve", "ゔぇ");
        ROMAJI_MAP.put("vo", "ゔぉ");
        
        // 1-character vowels
        ROMAJI_MAP.put("a", "あ");
        ROMAJI_MAP.put("i", "い");
        ROMAJI_MAP.put("u", "う");
        ROMAJI_MAP.put("e", "え");
        ROMAJI_MAP.put("o", "お");
        
        ROMAJI_MAP.put("nn", "ん");
        ROMAJI_MAP.put("n'", "ん");

        // Generate small kana variants
        generateSmallKanaVariants();
    }

    private static void initializeKanjiMap() {
        // Common words with kanji (LunaChat inspired)
        KANJI_MAP.put("arigatou", "有難う");
        KANJI_MAP.put("arigatai", "有難い");
        KANJI_MAP.put("arigatougozaimasu", "有難うございます");
        KANJI_MAP.put("konnichiha", "こんにちは");
        KANJI_MAP.put("konnichiwa", "こんにちは");
        KANJI_MAP.put("ohayou", "お早う");
        KANJI_MAP.put("ohayougozaimasu", "お早うございます");
        KANJI_MAP.put("oyasumi", "お休み");
        KANJI_MAP.put("oyasumiasai", "お休みなさい");
        KANJI_MAP.put("sumimasen", "済みません");
        KANJI_MAP.put("sumimasenga", "済みませんが");
        KANJI_MAP.put("yokatta", "良かった");
        KANJI_MAP.put("yabai", "危ない");
        KANJI_MAP.put("sugoi", "凄い");
        KANJI_MAP.put("kawaii", "可愛い");
        KANJI_MAP.put("atatakai", "温かい");
        KANJI_MAP.put("samui", "寒い");
        KANJI_MAP.put("atui", "熱い");
        KANJI_MAP.put("atsui", "厚い");
        KANJI_MAP.put("oishii", "美味しい");
        KANJI_MAP.put("naritai", "成りたい");
        KANJI_MAP.put("yaritagatteru", "やりたがってる");
        KANJI_MAP.put("suki", "好き");
        KANJI_MAP.put("daisuki", "大好き");
        KANJI_MAP.put("daikirai", "大嫌い");
        KANJI_MAP.put("machigai", "間違い");
        KANJI_MAP.put("machigaeta", "間違えた");
        KANJI_MAP.put("kudasai", "下さい");
        KANJI_MAP.put("onegaishimasu", "お願いします");
    }

    private static void generateSmallKanaVariants() {
        Map<String, String> baseMap = new HashMap<>(ROMAJI_MAP);
        
        for (Map.Entry<String, String> entry : baseMap.entrySet()) {
            String romaji = entry.getKey();
            String hiragana = entry.getValue();
            
            if (romaji.length() <= 1) {
                continue;
            }
            
            String smallHiragana = makeSmall(hiragana);
            ROMAJI_MAP.put("l" + romaji, smallHiragana);
            ROMAJI_MAP.put("x" + romaji, smallHiragana);
        }
        
        addSmallSingleChars();
    }

    private static void addSmallSingleChars() {
        ROMAJI_MAP.put("la", "ぁ");
        ROMAJI_MAP.put("li", "ぃ");
        ROMAJI_MAP.put("lu", "ぅ");
        ROMAJI_MAP.put("le", "ぇ");
        ROMAJI_MAP.put("lo", "ぉ");
        ROMAJI_MAP.put("lya", "ゃ");
        ROMAJI_MAP.put("lyu", "ゅ");
        ROMAJI_MAP.put("lyo", "ょ");
        ROMAJI_MAP.put("lwa", "ゎ");
        ROMAJI_MAP.put("ltu", "っ");
        
        ROMAJI_MAP.put("xa", "ぁ");
        ROMAJI_MAP.put("xi", "ぃ");
        ROMAJI_MAP.put("xu", "ぅ");
        ROMAJI_MAP.put("xe", "ぇ");
        ROMAJI_MAP.put("xo", "ぉ");
        ROMAJI_MAP.put("xya", "ゃ");
        ROMAJI_MAP.put("xyu", "ゅ");
        ROMAJI_MAP.put("xyo", "ょ");
        ROMAJI_MAP.put("xwa", "ゎ");
        ROMAJI_MAP.put("xtu", "っ");
    }

    private static String makeSmall(String hiragana) {
        if (hiragana == null || hiragana.isEmpty()) {
            return hiragana;
        }
        
        StringBuilder result = new StringBuilder();
        for (char c : hiragana.toCharArray()) {
            String small = SMALL_KANA_MAP.get(String.valueOf(c));
            if (small != null) {
                result.append(small);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Convert a single romaji word to Japanese with kanji support
     * Result format: 日本語(日本語) e.g., 有難う(有難う), あいうえお(あいうえお)
     */
    public static String convertWord(String romajiWord) {
        if (romajiWord == null || romajiWord.isEmpty()) {
            return romajiWord;
        }

        String lower = romajiWord.toLowerCase();
        
        // Check kanji map first (for common words)
        if (KANJI_MAP.containsKey(lower)) {
            String kanji = KANJI_MAP.get(lower);
            return kanji + "(" + kanji + ")";
        }

        StringBuilder japanese = new StringBuilder();
        int i = 0;

        while (i < lower.length()) {
            boolean matched = false;

            // Handle sokuon
            if (i + 1 < lower.length() && isConsonant(lower.charAt(i)) && 
                lower.charAt(i) == lower.charAt(i + 1) &&
                lower.charAt(i) != 'n' && lower.charAt(i) != 'l' && lower.charAt(i) != 'x') {
                
                String nextPart = lower.substring(i + 1);
                if (canMatch(nextPart)) {
                    japanese.append("っ");
                    i++;
                    matched = true;
                }
            }

            // Try 4-character matches
            if (!matched && i + 4 <= lower.length()) {
                String fourChar = lower.substring(i, i + 4);
                if (ROMAJI_MAP.containsKey(fourChar)) {
                    japanese.append(ROMAJI_MAP.get(fourChar));
                    i += 4;
                    matched = true;
                }
            }

            // Try 3-character matches
            if (!matched && i + 3 <= lower.length()) {
                String threeChar = lower.substring(i, i + 3);
                if (ROMAJI_MAP.containsKey(threeChar)) {
                    japanese.append(ROMAJI_MAP.get(threeChar));
                    i += 3;
                    matched = true;
                }
            }

            // Try 2-character matches
            if (!matched && i + 2 <= lower.length()) {
                String twoChar = lower.substring(i, i + 2);
                if (ROMAJI_MAP.containsKey(twoChar)) {
                    japanese.append(ROMAJI_MAP.get(twoChar));
                    i += 2;
                    matched = true;
                }
            }

            // Try 1-character match
            if (!matched && i + 1 <= lower.length()) {
                String oneChar = lower.substring(i, i + 1);
                if (ROMAJI_MAP.containsKey(oneChar)) {
                    japanese.append(ROMAJI_MAP.get(oneChar));
                    i += 1;
                    matched = true;
                }
            }

            if (!matched) {
                japanese.append(lower.charAt(i));
                i++;
            }
        }

        return japanese.toString() + "(" + japanese.toString() + ")";
    }

    private static boolean canMatch(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        if (text.length() >= 4 && ROMAJI_MAP.containsKey(text.substring(0, 4))) {
            return true;
        }
        if (text.length() >= 3 && ROMAJI_MAP.containsKey(text.substring(0, 3))) {
            return true;
        }
        if (text.length() >= 2 && ROMAJI_MAP.containsKey(text.substring(0, 2))) {
            return true;
        }
        if (text.length() >= 1 && ROMAJI_MAP.containsKey(text.substring(0, 1))) {
            return true;
        }

        return false;
    }

    private static boolean isConsonant(char c) {
        return "bcdfghjklmnpqrstvwxyz".indexOf(c) >= 0;
    }

    /**
     * Convert romaji text to Japanese, handling spaces properly
     * Example: "a i u e o" -> "あ い う え お (あ い う え お)"
     */
    public static String convert(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder();
        StringBuilder currentWord = new StringBuilder();
        StringBuilder originalWord = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            // Check if character is part of a romaji word
            if (Character.isLetter(ch) || ch == '-' || ch == '\'') {
                currentWord.append(ch);
                originalWord.append(ch);
            } else {
                // Process accumulated word
                if (currentWord.length() > 0) {
                    String word = currentWord.toString();
                    if (containsRomaji(word)) {
                        result.append(convertWord(word));
                    } else {
                        result.append(word);
                    }
                    currentWord = new StringBuilder();
                    originalWord = new StringBuilder();
                }
                // Keep the separator (including spaces)
                result.append(ch);
            }
        }

        // Process final word
        if (currentWord.length() > 0) {
            String word = currentWord.toString();
            if (containsRomaji(word)) {
                result.append(convertWord(word));
            } else {
                result.append(word);
            }
        }

        return result.toString();
    }

    /**
     * Check if text contains romaji
     */
    public static boolean containsRomaji(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        String lower = text.toLowerCase();
        for (String key : ROMAJI_MAP.keySet()) {
            if (lower.contains(key)) {
                return true;
            }
        }
        
        // Also check kanji map
        for (String key : KANJI_MAP.keySet()) {
            if (lower.equals(key)) {
                return true;
            }
        }
        
        return false;
    }
}
