package org.marrineer.corruptedSoul.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.marrineer.corruptedSoul.CorruptedSoul;

public class message {
    public static final FileConfiguration MESSAGE = CorruptedSoul.getInstance().getMessages();
    public static final String PREFIX = MESSAGE.getString("prefix", "");

    public static String get(String place) {
        assert MESSAGE.getDefaults() != null;
        return MESSAGE.getString(place, MESSAGE.getDefaults().getString(place));
    }

    public static void sendToSender(String text, CommandSender sender) {
        if (sender instanceof Player player) {
            CorruptedSoul.getInstance().audience(player).sendMessage(
                    MiniMessage.miniMessage().deserialize(
                            PlaceholderAPI.setPlaceholders(
                                    player,
                                    String.format("%s %s", PREFIX, text)
                            )
                    )
            );
        } else {
            CorruptedSoul.getInstance().audience(sender).sendMessage(
                    MiniMessage.miniMessage().deserialize(
                            String.format("%s %s", PREFIX, text)
                    )
            );
        }
    }

    public static void sendToPlayer(String text, Player player) {
        CorruptedSoul.getInstance().audience(player).sendMessage(
                MiniMessage.miniMessage().deserialize(
                        PlaceholderAPI.setPlaceholders(
                                player,
                                String.format("%s %s", PREFIX, text)
                        )
                )
        );
    }
}
