package org.marrineer.corruptedSoul.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.marrineer.corruptedSoul.CorruptedSoul;
import org.marrineer.corruptedSoul.managers.databaseManager;
import org.marrineer.corruptedSoul.managers.itemManager;
import org.marrineer.corruptedSoul.utils.message;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class soulDrop implements Listener {
    private final CorruptedSoul plugin;
    private final databaseManager dbManager;
    private double chance;
    private itemManager soul;

    public soulDrop(CorruptedSoul plugin, databaseManager dbManager) {
        this.plugin = plugin;
        this.dbManager = dbManager;
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent e) {
        soul = new itemManager(plugin, dbManager);
        chance = plugin.getConfig().getDouble("chance", 10);
        Player killer = e.getEntity().getKiller();
        if(killer == null) return;

        LivingEntity warden = e.getEntity();
        UUID KillerUUID = killer.getUniqueId();
        if(e instanceof Warden) {
            if(isChance(chance)) {
                dbManager.incrementUsage(KillerUUID);
                killer.give(soul.corruptedSoul(killer));
            }
        } else return;
        if(plugin.getConfig().getBoolean("message.whole-server")) {
            for(Player players : Bukkit.getOnlinePlayers()) {
                plugin.audience(players).sendMessage(
                        MiniMessage.miniMessage().deserialize(
                                PlaceholderAPI.setPlaceholders(
                                        killer,
                                        Objects.requireNonNull(plugin.getMessages().getString(
                                                "harvested",
                                                plugin.getMessages().getDefaults().getString("harvested")
                                        ))
                                )
                        )
                );
            }
        } else {
            message.sendToPlayer(message.get("harvested"), killer);
        }
    }

    private boolean isChance(double percent) {
        Random random = new Random();
        return random.nextDouble(100) < percent;
    }
}
