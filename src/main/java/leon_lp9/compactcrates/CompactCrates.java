package leon_lp9.compactcrates;

import leon_lp9.compactcrates.commands.MainCommand;
import leon_lp9.compactcrates.events.CratePlaceBreakEvent;
import leon_lp9.compactcrates.events.PlayerJoinEvent;
import leon_lp9.compactcrates.manager.ParticleManager;
import leon_lp9.compactcrates.manager.SpawnCratesManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class CompactCrates extends JavaPlugin {

    private static CompactCrates instance;

    File newConfig;
    FileConfiguration newConfigz;

    File languageConfig;
    FileConfiguration languageConfigz;

    File userConfig;
    FileConfiguration userConfigz;

    public void onEnable(){
        instance = this;

        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);

        //safe defaults of language file
        CompactCrates.getInstance().saveResource("language.yml", false);
        CompactCrates.getInstance().saveResource("chests.yml", false);

        createNewConfig();

        // Register Commands
        getCommand("compactcrates").setExecutor(new MainCommand());
        getCommand("compactcrates").setTabCompleter(new MainCommand());


        CompactCrates.getInstance().getLanguageConfig().options().copyDefaults(true);
        CompactCrates.getInstance().getChestConfig().options().copyDefaults(true);

        // Register Events
        getServer().getPluginManager().registerEvents(new CratePlaceBreakEvent(), this);
        getServer().getPluginManager().registerEvents(new InventoryManager(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinEvent(), this);

        //check if votifier is installed
        if (Bukkit.getPluginManager().getPlugin("Votifier") != null) {
            getServer().getPluginManager().registerEvents(new VoteEvent(), this);
            getLogger().info("Votifier found. VoteEvent registered.");
        }else {
            getLogger().info("Votifier is not installed. VoteEvent will not be registered.");
        }

        // Spawn Crates
        //Wait for the server to load the world
        Bukkit.getScheduler().runTaskLater(this, () -> {
            SpawnCratesManager.spawnCrates();
            ParticleManager.start();
        }, 20);

        new UpdateChecker(this, 107018).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getLogger().info("There is not a new update available.");
            } else {
                getLogger().info("There is a new update available.");
                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (player.hasPermission("compactcrates.notify.update")) {
                        player.sendMessage(getPrefix() + "There is a §enew update§7 available.");
                        player.sendMessage(getPrefix() + "Your are using version §e" + this.getDescription().getVersion());
                        player.sendMessage(getPrefix() + "The latest version is §e" + version);
                        player.sendMessage(getPrefix() + "Download it here: https://www.spigotmc.org/resources/compactcrates.107018/");
                    }
                });
            }
        });
    }


    public void onDisable(){
        // Plugin shutdown logic

        SpawnCratesManager.removeCrates();

        ParticleManager.stop();
    }

    public void createNewConfig(){
        newConfig = new File(getDataFolder(), "chests.yml");
        newConfigz = YamlConfiguration.loadConfiguration(newConfig);

        languageConfig = new File(getDataFolder(), "language.yml");
        languageConfigz = YamlConfiguration.loadConfiguration(languageConfig);

        userConfig = new File(getDataFolder(), "users.yml");
        userConfigz = YamlConfiguration.loadConfiguration(userConfig);

        saveChestsConfig();
        saveLanguageConfig();
        saveUserConfig();
        saveConfig();
    }

    public void saveChestsConfig(){
        try{
            newConfigz.save(newConfig);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void saveLanguageConfig(){
        try {
            languageConfigz.save(languageConfig);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveUserConfig(){
        try {
            userConfigz.save(userConfig);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public FileConfiguration getChestConfig(){
        return newConfigz;
    }

    public FileConfiguration getLanguageConfig(){
        return languageConfigz;
    }

    public FileConfiguration getUserConfig(){
        return userConfigz;
    }

    public static CompactCrates getInstance() {
        return instance;
    }

    public static String getPrefix(){
        return CompactCrates.getInstance().getLanguageConfig().getString("prefix").replace("&", "§");
    }


}
