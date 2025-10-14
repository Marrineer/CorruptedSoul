package org.marrineer.corruptedSoul;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.marrineer.corruptedSoul.listeners.soulDrop;
import org.marrineer.corruptedSoul.managers.databaseManager;
import org.marrineer.corruptedSoul.managers.messageManager;
import org.marrineer.corruptedSoul.utils.soulExpansion;

import java.sql.SQLException;

public final class CorruptedSoul extends JavaPlugin {
    public static CorruptedSoul instance;
    private BukkitAudiences adventure;
    private messageManager MessageManager;
    private FileConfiguration messages;
    private databaseManager dbManager;
    private soulExpansion PAPI;
    private final double interval = getConfig().getDouble("autoSave.interval", 600);

    public static CorruptedSoul getInstance() { return instance; }
    public BukkitAudiences adventure() { return this.adventure; }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        logg("Enabling");

        PAPI = new soulExpansion(this, dbManager);
        PAPI.register();


        this.adventure = BukkitAudiences.create(this);
        MessageManager = new messageManager(this);
        dbManager = new databaseManager(this);

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                dbManager.connect();
                dbManager.startAutoFlush(interval);
                getLogger().info("Database connected");
            } catch (SQLException e) {
                getLogger().warning("(\"Error while connecting to the database, disabling plugin...\");");
                Bukkit.getScheduler().runTask(this, () -> {
                    Bukkit.getPluginManager().disablePlugin(this);
                });
            }
        });
        Bukkit.getPluginManager().registerEvents(new soulDrop(this, dbManager), this);
    }

    @Override
    public void onDisable() {
        logg("Disabling");
        saveConfig();
        if(dbManager != null) {
            dbManager.disconnect();
            dbManager = null;
            getLogger().info("Database disconnected");
        }
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }

    private void logg(String status) {
        StringBuilder text = new StringBuilder("\n\n");
        text.append("&8[]===========[").append(status).append(" &cCoordLeak&8]===========[]\n");
        text.append("&8|\n");
        text.append("&8| &cInformation:\n");
        text.append("&8|\n");
        text.append("&8|   &9Name: &bCorruptedSoul\n");
        text.append("&8|   &9Author: ").append(getDescription().getAuthors()).append("\n");
        text.append("&8|\n");
        text.append("&8| &9Contact:\n");
        text.append("&8|   &9Email: &bmarrineer@gmail.com\n");
        text.append("&8|   &9Discord: &b@marrineer\n");
        text.append("&8|\n");
        text.append("&8[]=========================================[]\n");

        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', text.toString()));
    }

    public Audience audience(CommandSender sender) { return adventure.sender(sender); }
    public Audience audience(Player player) { return adventure.player(player); }
    public FileConfiguration getMessages() { return MessageManager.getMessages(); }
}
