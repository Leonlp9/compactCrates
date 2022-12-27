package leon_lp9.compactcrates;

import leon_lp9.compactcrates.commands.MainCommand;
import leon_lp9.compactcrates.events.CratePlaceBreakEvent;
import leon_lp9.compactcrates.manager.SpawnCratesManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class CompactCrates extends JavaPlugin {

    private static CompactCrates instance;

    File newConfig;
    FileConfiguration newConfigz;

    File languageConfig;
    FileConfiguration languageConfigz;

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
        Bukkit.getScheduler().runTaskLater(this, SpawnCratesManager::spawnCrates, 20);
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
        if (!getConfig().contains("inventorySize")) {
            getConfig().set("inventorySize", 45);
        }
        if (!getChestConfig().contains("cratesTypes")) {
            getChestConfig().set("cratesTypes.Default.Type", "ENDER_CHEST");
            getChestConfig().set("cratesTypes.Default.Name", "Default");
            getChestConfig().set("cratesTypes.Default.Slot", "20");

            getChestConfig().set("cratesTypes.Default2.Type", "REDSTONE_BLOCK");
            getChestConfig().set("cratesTypes.Default2.Name", "Default2");
            getChestConfig().set("cratesTypes.Default2.Slot", "24");
        }


        saveChestsConfig();
        saveLanguageConfig();
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

    public FileConfiguration getChestConfig(){
        return newConfigz;
    }

    public FileConfiguration getLanguageConfig(){
        return languageConfigz;
    }

    public static CompactCrates getInstance() {
        return instance;
    }

    public static String getPrefix(){
        return CompactCrates.getInstance().getLanguageConfig().getString("prefix").replace("&", "§");
    }


}
