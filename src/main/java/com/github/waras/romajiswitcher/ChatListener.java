package com.github.waras.romajiswitcher;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Listens for chat events and converts romaji to Japanese
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

        // Convert romaji to Japanese
        String converted = RomajiConverter.convert(text);

        if (!converted.equals(text)) {
            // Replace only the message content, not the entire component
            event.message(Component.text(converted));
        }
    }

    /**
     * Extract plain text from a component
     */
    private String getTextFromComponent(Component component) {
        if (component instanceof TextComponent textComponent) {
            return textComponent.content();
        }
        // For complex components, convert to plain text
        return component.toString();
    }
}
