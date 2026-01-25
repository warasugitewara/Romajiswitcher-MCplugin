package com.github.waras.romajiswitcher;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Listens for chat events and converts romaji to Japanese with color support
 * Only modifies the message content, preserving player name and other plugins' modifications
 */
public class ChatListener implements Listener {
    private final UserPreferences preferences;

    public ChatListener(UserPreferences preferences) {
        this.preferences = preferences;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        if (!preferences.isEnabled(player.getUniqueId())) {
            return;
        }

        // Extract text from the message component
        Component message = event.message();
        String text = getTextFromComponent(message);

        if (text == null || text.isEmpty() || !RomajiConverter.containsRomaji(text)) {
            return;
        }

        // Convert romaji to Japanese with colors
        Component converted = convertWithColors(text, player.getUniqueId());

        if (converted != null && !converted.equals(message)) {
            event.message(converted);
        }
    }

    /**
     * Convert text with color support
     */
    private Component convertWithColors(String text, java.util.UUID playerId) {
        // Get user's color preferences
        String[] colors = preferences.getColors(playerId);
        NamedTextColor japaneseColor = ColorManager.getColor(colors[0]);
        NamedTextColor romajiColor = ColorManager.getColor(colors[1]);

        StringBuilder result = new StringBuilder();
        Component componentResult = Component.empty();
        StringBuilder currentWord = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            if (Character.isLetter(ch) || ch == '-' || ch == '\'') {
                currentWord.append(ch);
            } else {
                // Process accumulated word
                if (currentWord.length() > 0) {
                    String word = currentWord.toString();
                    if (RomajiConverter.containsRomaji(word)) {
                        RomajiConverter.ConversionResult convResult = RomajiConverter.convertWordWithResult(word);
                        
                        // Japanese part with color
                        Component japaneseComponent = Component.text(convResult.japanese)
                            .color(japaneseColor);
                        
                        // Romaji part with color (inside parentheses)
                        Component romajiComponent = Component.text("(" + convResult.original + ")")
                            .color(romajiColor);
                        
                        componentResult = componentResult.append(japaneseComponent).append(romajiComponent);
                    } else {
                        componentResult = componentResult.append(Component.text(word));
                    }
                    currentWord = new StringBuilder();
                }
                componentResult = componentResult.append(Component.text(String.valueOf(ch)));
            }
        }

        // Process final word
        if (currentWord.length() > 0) {
            String word = currentWord.toString();
            if (RomajiConverter.containsRomaji(word)) {
                RomajiConverter.ConversionResult convResult = RomajiConverter.convertWordWithResult(word);
                
                Component japaneseComponent = Component.text(convResult.japanese)
                    .color(japaneseColor);
                Component romajiComponent = Component.text("(" + convResult.original + ")")
                    .color(romajiColor);
                
                componentResult = componentResult.append(japaneseComponent).append(romajiComponent);
            } else {
                componentResult = componentResult.append(Component.text(word));
            }
        }

        return componentResult;
    }

    /**
     * Extract plain text from a component
     */
    private String getTextFromComponent(Component component) {
        if (component instanceof TextComponent textComponent) {
            return textComponent.content();
        }
        return component.toString();
    }
}
