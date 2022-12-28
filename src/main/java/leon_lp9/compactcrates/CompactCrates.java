package leon_lp9.compactcrates;

import leon_lp9.compactcrates.commands.MainCommand;
import leon_lp9.compactcrates.events.CratePlaceBreakEvent;
import leon_lp9.compactcrates.manager.ParticleManager;
import leon_lp9.compactcrates.manager.SpawnCratesManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;

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
        createNewConfig();

        // Register Commands
        getCommand("compactcrates").setExecutor(new MainCommand());
        getCommand("compactcrates").setTabCompleter(new MainCommand());

        // Register Events
        getServer().getPluginManager().registerEvents(new CratePlaceBreakEvent(), this);
        getServer().getPluginManager().registerEvents(new InventoryManager(), this);

        // Spawn Crates
        //Wait for the server to load the world
        Bukkit.getScheduler().runTaskLater(this, () -> {
            SpawnCratesManager.spawnCrates();
            ParticleManager.start();
        }, 20);
    }


    public void onDisable(){
        // Plugin shutdown logic

        SpawnCratesManager.removeCrates();
    }

    public void createNewConfig(){
        newConfig = new File(getDataFolder(), "chests.yml");
        newConfigz = YamlConfiguration.loadConfiguration(newConfig);

        languageConfig = new File(getDataFolder(), "language.yml");
        languageConfigz = YamlConfiguration.loadConfiguration(languageConfig);

        userConfig = new File(getDataFolder(), "users.yml");
        userConfigz = YamlConfiguration.loadConfiguration(userConfig);

        //getChestConfig().set("test", "test");
        //getConfig().set("test", "test");
        if (!getLanguageConfig().contains("prefix")) {
            getLanguageConfig().set("prefix", "&e&lCompactCrates &8» &7");
        }
        if (!getLanguageConfig().contains("click")) {
            getLanguageConfig().set("click", "&e&l&oClick");
        }
        if (!getLanguageConfig().contains("firstInventoryName")) {
            getLanguageConfig().set("firstInventoryName", "§6§lCompactCrates");
        }
        if (!getLanguageConfig().contains("firstInventoryLore")) {
            ArrayList<String> list = new ArrayList<String>() {{
                add("&7&m------------------------");
                add("&7Click to open the Crate");
                add("&7You have §e%key% &7Crates");
                add("&7&m------------------------");
            }};
            getLanguageConfig().set("firstInventoryLore", list);
        }
        if (!getConfig().contains("inventorySize")) {
            getConfig().set("inventorySize", 45);
        }
        if (!getConfig().contains("Particle")) {
            getConfig().set("Particle", "CRIT");
        }
        if (!getChestConfig().contains("cratesTypes")) {
            getChestConfig().set("cratesTypes.Default.Type", "ENDER_CHEST");
            getChestConfig().set("cratesTypes.Default.Name", "&5&lDefault Crate");
            getChestConfig().set("cratesTypes.Default.ID", "Default1");
            getChestConfig().set("cratesTypes.Default.Slot", "20");

            getChestConfig().set("cratesTypes.Default2.Type", "REDSTONE_BLOCK");
            getChestConfig().set("cratesTypes.Default2.Name", "&c&lDefault Crate");
            getChestConfig().set("cratesTypes.Default2.ID", "Default2");
            getChestConfig().set("cratesTypes.Default2.Slot", "24");
        }
        if (!getLanguageConfig().contains("secondInventoryName")){
            getLanguageConfig().set("secondInventoryName", "&6&lCompactCrates &8» &7%crate%");
        }


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
