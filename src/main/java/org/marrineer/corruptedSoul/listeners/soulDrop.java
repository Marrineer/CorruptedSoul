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
import org.bukkit.inventory.ItemStack;
import org.marrineer.corruptedSoul.CorruptedSoul;
import org.marrineer.corruptedSoul.managers.databaseManager;
import org.marrineer.corruptedSoul.items.ItemManager;
import org.marrineer.corruptedSoul.utils.message;

import java.util.*;

public class soulDrop implements Listener {
    private final CorruptedSoul plugin;
    private final databaseManager dbManager;
    private String type;
    private double chance;
    private ItemManager soul;
    private ItemStack soulType = null;

    public soulDrop(CorruptedSoul plugin, databaseManager dbManager) {
        this.plugin = plugin;
        this.dbManager = dbManager;
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent e) {
        soul = new ItemManager(plugin, dbManager);
        chance = plugin.getConfig().getDouble("chance", 10);
        Player killer = e.getEntity().getKiller();
        if (killer == null) return;

        UUID KillerUUID = killer.getUniqueId();
        if(!(e.getEntity() instanceof Warden)) return;
        switch(isChance()) {
            case "1":
                soulType = soul.corruptedSoul(killer);
                break;
            case "2":
                soulType = soul.cursedSoul(killer);
                break;
            default:
                soulType = null;
                break;
        }
        if(soulType != null) {
            killer.getInventory().addItem(soulType);
            dbManager.incrementCount(killer.getUniqueId());
        }
    }

    private String isChance() {
        Map<String, Double> chances = new HashMap<>();
        chances.put("1", plugin.getConfig().getDouble("soulDrop.chance.corruptedSoul", 5.0));
        chances.put("2", plugin.getConfig().getDouble("soulDrop.chance.cursedSoul", 2.5));

        double total = chances.values().stream().mapToDouble(Double::doubleValue).sum();
        if(total <= 0) return null;

        double rand = Math.random() * total;
        double cumulative = 0.0;
        for (Map.Entry<String, Double> entry : chances.entrySet()) {
            cumulative += entry.getValue();
            if (rand <= cumulative) {
                return entry.getKey();
            }
        }
        return null;
    }
}
