package org.marrineer.corruptedSoul.utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.marrineer.corruptedSoul.CorruptedSoul;
import org.marrineer.corruptedSoul.managers.databaseManager;

public class soulExpansion extends PlaceholderExpansion {
    private final CorruptedSoul plugin;
    private final databaseManager dbManager;

    public soulExpansion(CorruptedSoul plugin, databaseManager dbManager) {
        this.plugin = plugin;
        this.dbManager = dbManager;
    }
    @Override
    public @NotNull String getIdentifier() {
        return "corruptedsoul";
    }

    @Override
    public @NotNull String getAuthor() {
        return "marrineer";
    }

    @Override
    public @NotNull String getVersion() {
        return "v1.0.0";
    }

    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return "";
        return switch(identifier.toLowerCase()) {
            case "totalsoul" -> String.valueOf(dbManager.getUsage(player.getUniqueId()));
            default -> null;
        };
    }
}
