package leon_lp9.compactcrates;

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
    }


    public void onDisable(){
        // Plugin shutdown logic
    }

    public void createNewConfig(){
        newConfig = new File(getDataFolder(), "chests.yml");
        newConfigz = YamlConfiguration.loadConfiguration(newConfig);

        languageConfig = new File(getDataFolder(), "language.yml");
        languageConfigz = YamlConfiguration.loadConfiguration(languageConfig);

        //getChestConfig().set("test", "test");
        //getConfig().set("test", "test");
        getLanguageConfig().set("prefix", "&e&lCompactCrates &8» &7");

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


}
