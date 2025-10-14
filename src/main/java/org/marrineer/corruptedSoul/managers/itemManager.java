package org.marrineer.corruptedSoul.managers;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class itemManager {
    private final CorruptedSoul plugin;
    private final databaseManager dbManager;

    public itemManager(CorruptedSoul plugin, databaseManager dbManager) {
        this.plugin = plugin;
        this.dbManager = dbManager;
    }
    public ItemStack corruptedSoul(Player player) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();

        Component displayName = MiniMessage.miniMessage().deserialize(
                plugin.getConfig().getString("corruptedSoul.displayName", "<b><gradient:#FFFFFF:#FF00E3>Soul</gradient></b>")
        );
        meta.displayName(displayName);
        List<String> lore = plugin.getConfig().getStringList("corruptedSoul.lore");
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
        NamespacedKey NBT = new NamespacedKey(plugin, "corrupted");
        meta.getPersistentDataContainer().set(NBT, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }
}
