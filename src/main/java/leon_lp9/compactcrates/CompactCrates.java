package leon_lp9.compactcrates;

import leon_lp9.compactcrates.commands.MainCommand;
import leon_lp9.compactcrates.database.MySql;
import leon_lp9.compactcrates.events.CratePlaceBreakEvent;
import leon_lp9.compactcrates.events.PlayerJoinEvent;
import leon_lp9.compactcrates.manager.ParticleManager;
import leon_lp9.compactcrates.manager.SpawnCratesManager;
import leon_lp9.compactcrates.placeholders.UserPlaceHolders;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
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

    private static MySql mySql;
    public static boolean useMysql = false;

    public static boolean useFloodgate = false;

    public void onEnable(){
        instance = this;

        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);

        //safe defaults of language file
        if (!new File(getDataFolder(), "language.yml").exists()) {
            CompactCrates.getInstance().saveResource("language.yml", false);
        }
        if (!new File(getDataFolder(), "chests.yml").exists()) {
            CompactCrates.getInstance().saveResource("chests.yml", false);
        }

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
        getServer().getPluginManager().registerEvents(new ParticleManager(), this);

        int pluginId = 17254; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);
        getLogger().info("Metrics enabled");

        //check if votifier is installed
        if (Bukkit.getPluginManager().isPluginEnabled("Votifier")) {
            getServer().getPluginManager().registerEvents(new VoteEvent(), this);
            getLogger().info("Votifier found. VoteEvent registered.");
        }else {
            getLogger().info("Votifier is not installed. VoteEvent will not be registered.");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new UserPlaceHolders().register();
            getLogger().info("PlaceholderAPI found. Placeholders registered.");
        }

        //floodgate
        if (Bukkit.getPluginManager().isPluginEnabled("floodgate")) {
            getLogger().info("Floodgate found. Floodgate support enabled.");
            useFloodgate = true;
        }else {
            getLogger().info("Floodgate is not installed. Floodgate support disabled.");
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
                        player.sendMessage(getPrefix() + "There is a ??enew update??7 available.");
                        player.sendMessage(getPrefix() + "Your are using version ??e" + this.getDescription().getVersion());
                        player.sendMessage(getPrefix() + "The latest version is ??e" + version);
                        player.sendMessage(getPrefix() + "Download it here: https://www.spigotmc.org/resources/compactcrates.107018/");
                    }
                });
            }
        });

        //check if mysql is enabled
        if (getConfig().getBoolean("mysql")) {

            mySql = new MySql(getConfig().getString("host"), getConfig().getInt("port"), getConfig().getString("username"), getConfig().getString("password"), getConfig().getString("database"), getConfig().getString("table"));

            useMysql = true;
            getLogger().info("Mysql is enabled. Connecting to database...");
            mySql.connect();
            getLogger().info("Connected to database.");
            mySql.createTable(getConfig().getString("table"), "uuid VARCHAR(36), crate VARCHAR(16), amount INT(11)");

        }

        if (useMysql){
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    //reconnect to mysql
                    mySql.disconnect();
                    System.out.println("[MySql] Reconnect for MySql");
                    mySql.connect();
                }
            }, 20 * 60 * 5, 20 * 60 * 5);
        }
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
        return CompactCrates.getInstance().getLanguageConfig().getString("prefix").replace("&", "??");
    }

    public static MySql getMySql() {
        return mySql;
    }
}
