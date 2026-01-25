package com.github.waras.romajiswitcher;

import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Minecraft color code manager
 */
public class ColorManager {
    private static final java.util.Map<String, NamedTextColor> COLOR_MAP = new java.util.HashMap<>();

    static {
        COLOR_MAP.put("black", NamedTextColor.BLACK);
        COLOR_MAP.put("dark_blue", NamedTextColor.DARK_BLUE);
        COLOR_MAP.put("dark_green", NamedTextColor.DARK_GREEN);
        COLOR_MAP.put("dark_aqua", NamedTextColor.DARK_AQUA);
        COLOR_MAP.put("dark_red", NamedTextColor.DARK_RED);
        COLOR_MAP.put("dark_purple", NamedTextColor.DARK_PURPLE);
        COLOR_MAP.put("gold", NamedTextColor.GOLD);
        COLOR_MAP.put("gray", NamedTextColor.GRAY);
        COLOR_MAP.put("dark_gray", NamedTextColor.DARK_GRAY);
        COLOR_MAP.put("blue", NamedTextColor.BLUE);
        COLOR_MAP.put("green", NamedTextColor.GREEN);
        COLOR_MAP.put("aqua", NamedTextColor.AQUA);
        COLOR_MAP.put("red", NamedTextColor.RED);
        COLOR_MAP.put("light_purple", NamedTextColor.LIGHT_PURPLE);
        COLOR_MAP.put("yellow", NamedTextColor.YELLOW);
        COLOR_MAP.put("white", NamedTextColor.WHITE);
    }

    public static NamedTextColor getColor(String colorName) {
        if (colorName == null) {
            return NamedTextColor.WHITE;
        }
        return COLOR_MAP.getOrDefault(colorName.toLowerCase(), NamedTextColor.WHITE);
    }

    public static boolean isValidColor(String colorName) {
        return colorName != null && COLOR_MAP.containsKey(colorName.toLowerCase());
    }

    public static java.util.Set<String> getAvailableColors() {
        return COLOR_MAP.keySet();
    }
}
