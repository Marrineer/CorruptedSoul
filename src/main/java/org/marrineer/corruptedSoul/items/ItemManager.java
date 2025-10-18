package org.marrineer.corruptedSoul.items;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.marrineer.corruptedSoul.CorruptedSoul;
import org.marrineer.corruptedSoul.managers.databaseManager;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {
    private final CorruptedSoul plugin;
    private final databaseManager dbManager;

    public ItemManager(CorruptedSoul plugin, databaseManager dbManager) {
        this.plugin = plugin;
        this.dbManager = dbManager;
    }

    public ItemStack corruptedSoul(Player player) {
        ItemStack Corrupted = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = Corrupted.getItemMeta();

        Component displayName = MiniMessage.miniMessage().deserialize(
                plugin.getConfig().getString("soulInfo.corruptedSoul.displayName", "")
        );
        meta.displayName(displayName);
        List<String> lore = plugin.getConfig().getStringList("soulInfo.corruptedSoul.lore");
        List<Component> finalLore = new ArrayList<>();

        for (String line : lore) {
            finalLore.add(
                    MiniMessage.miniMessage().deserialize(
                            PlaceholderAPI.setPlaceholders(
                                    player,
                                    line
                            )
                    )
            );
        }
        meta.lore(finalLore);
        NamespacedKey NBT = new NamespacedKey(plugin, "soulType");
        meta.getPersistentDataContainer().set(NBT, PersistentDataType.STRING, "CORRUPTED");
        Corrupted.setItemMeta(meta);
        return Corrupted;
    }
    public ItemStack cursedSoul(Player player) {
        ItemStack Cursed = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = Cursed.getItemMeta();

        Component displayName = MiniMessage.miniMessage().deserialize(
                plugin.getConfig().getString("soulInfo.cursedSoul.displayName", "")
        );
        meta.displayName(displayName);
        List<String> lore = plugin.getConfig().getStringList("soulInfo.cursedSoul.lore");
        List<Component> finalLore = new ArrayList<>();
        for(String line : lore) {
            finalLore.add(
                    MiniMessage.miniMessage().deserialize(
                            PlaceholderAPI.setPlaceholders(
                                    player,
                                    line
                            )
                    )
            );
        }
        meta.lore(finalLore);
        NamespacedKey NBT = new NamespacedKey(plugin, "soulType");
        meta.getPersistentDataContainer().set(NBT, PersistentDataType.STRING, "CURSED");
        Cursed.setItemMeta(meta);
        return Cursed;
    }
}
