package org.marrineer.corruptedSoul.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.marrineer.corruptedSoul.CorruptedSoul;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class messageManager {
    private final CorruptedSoul plugin;
    private File file;
    private FileConfiguration messages;

    public messageManager(CorruptedSoul plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        reload();
    }

    public void reload() {
        this.messages = YamlConfiguration.loadConfiguration(file);
        FileConfiguration defaultMessage = YamlConfiguration.loadConfiguration(
                new InputStreamReader(plugin.getResource("messages.yml"), StandardCharsets.UTF_8)
        );
        messages.setDefaults(defaultMessage);
        messages.options().copyDefaults(true);
    }

    public FileConfiguration getMessages() {
        return messages;
    }
}
