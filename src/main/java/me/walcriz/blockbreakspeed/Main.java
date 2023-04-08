package me.walcriz.blockbreakspeed;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.walcriz.blockbreakspeed.commands.ReloadCommand;
import me.walcriz.blockbreakspeed.config.BlockConfig;
import me.walcriz.blockbreakspeed.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;

public final class Main extends JavaPlugin {

    private static Main instance;
    public static Main getInstance() { return instance; }

    private static PlayerListener playerListener;
    public static PlayerListener getPlayerListener() { return playerListener; }

    private static ProtocolManager protocolManager;
    public static ProtocolManager getProtocolManager() { return protocolManager; }

    public static Config config;
    public static EnumMap<Material, BlockConfig> blockConfigs = new EnumMap<>(Material.class);

    public File blockFolder;

    @Override
    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        this.saveDefaultConfig();
        config = new Config(this);

        // Setup directories
        blockFolder = new File(getDataFolder(), "blocks");
        if (!blockFolder.exists())
            this.saveResource("blocks", false);

        // Register events
        playerListener = new PlayerListener();
        Bukkit.getPluginManager().registerEvents(playerListener, this);

        // Register commands
        this.getCommand("bbsreload").setExecutor(new ReloadCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        config = null;
    }

    private void loadBlockConfigs() {
        File[] configs = blockFolder.listFiles();
        if (configs == null)
            return;

        for (File blockConfig : configs) {
            if (blockConfig.getName().equals("example.yml"))
                continue;

            YamlConfiguration configuration = new YamlConfiguration();
            try {
                configuration.load(blockConfig);
                String materialName = configuration.getString("block");
                int hardness = configuration.getInt("hardness-target");
                if (materialName == null) {
                    logger.warning("Incomplete configuration at file: " + blockConfig.getAbsolutePath());
                    continue;
                }

                Material material = Material.getMaterial(materialName);
                if (material == null) {
                    logger.warning("No material of type: '" + materialName + "' exist");
                    continue;
                }

                blockConfigs.put(material, new BlockConfig(hardness, material));
            } catch (IOException e) {
                logger.severe("IOException was created whilst loading config from file: " + blockConfig.getAbsolutePath());
                e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                logger.severe("Invalid configuration at file: " + blockConfig.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }

    public void reloadBlockConfigs() {
        blockConfigs = new EnumMap<>(Material.class);
        loadBlockConfigs();

        playerListener.cancelTask();
        playerListener.removeAllEffects();
    }
}
