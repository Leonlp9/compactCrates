package leon_lp9.compactcrates;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class CompactCrates extends JavaPlugin {


    File newConfig;
    FileConfiguration newConfigz;

    public void onEnable(){
        createNewConfig();

        getChestConfig().set("test", "test");
        getConfig().set("test", "test");
    }


    public void onDisable(){
        saveConfig();
        saveNewConfig();
    }

    public void createNewConfig(){
        newConfig = new File(getDataFolder(), "chests.yml");
        newConfigz = YamlConfiguration.loadConfiguration(newConfig);
        saveNewConfig();
    }

    public void saveNewConfig(){
        try{
            newConfigz.save(newConfig);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public FileConfiguration getChestConfig(){
        return newConfigz;
    }


}
