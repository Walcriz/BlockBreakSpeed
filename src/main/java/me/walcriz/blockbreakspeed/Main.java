package me.walcriz.blockbreakspeed;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.walcriz.blockbreakspeed.block.BlockDatabase;
import me.walcriz.blockbreakspeed.block.Hardness;
import me.walcriz.blockbreakspeed.block.material.IMaterial;
import me.walcriz.blockbreakspeed.block.material.MaterialType;
import me.walcriz.blockbreakspeed.commands.ReloadCommand;
import me.walcriz.blockbreakspeed.block.BlockConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public final class Main extends JavaPlugin {

    private static Main instance;
    public static Main getInstance() { return instance; }

    private static PlayerListener playerListener;
    public static PlayerListener getPlayerListener() { return playerListener; }

    private static ProtocolManager protocolManager;
    public static ProtocolManager getProtocolManager() { return protocolManager; }

    public static Config config;

    public static Logger logger;

    public File blockFolder;

    public static boolean doDebugLog() { return config.debugLogging; }

    @Override
    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        logger = this.getLogger();

        if (!isMock) {
            logger.info("Loading configs...");

            // Load config
            this.saveDefaultConfig();
            config = new Config(this);

            // Setup directories
            blockFolder = new File(getDataFolder(), "blocks");
            if (!blockFolder.exists()) {
                logger.info(blockFolder.getAbsolutePath() + " was not found. Creating it...");
                this.saveResource("blocks/example.yml", false);
            }

            // Load block configs
            loadBlockConfigs();
        }


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
        playerListener.cancelTask();
    }

    private void loadBlockConfigs() {
        BlockDatabase manager = BlockDatabase.getInstance();

        File[] configs = blockFolder.listFiles();
        if (configs == null)
            return;

        for (File blockConfig : configs) {
            if (blockConfig.getName().equals("example.yml"))
                continue;

            if (doDebugLog())
                logger.info("Loading block config from file: '" + blockConfig.getName() + "'...");

            YamlConfiguration configuration = new YamlConfiguration();
            try {
                configuration.load(blockConfig);
                String materialName = configuration.getString("block");
                Hardness hardness = new Hardness(configuration.getInt("hardness.base"), configuration.getInt("hardness.min"), configuration.getInt("hardness.max"));
                String[] modifierStrings = configuration.getStringList("states").toArray(new String[0]);
                String[] triggerStrings = configuration.getStringList("triggers").toArray(new String[0]);

                boolean suppressDrops = false;
                if (configuration.contains("disable-drops"))
                    suppressDrops = configuration.getBoolean("disable-drops");

                if (materialName == null) {
                    logger.warning("Incomplete configuration at file: " + blockConfig.getAbsolutePath());
                    continue;
                }

                IMaterial<?> material = MaterialType.getMaterial(materialName);
                if (material == null) {
                    logger.warning("No material of type: '" + materialName + "' exist");
                    continue;
                }

                manager.addConfig(material, new BlockConfig(hardness, material, suppressDrops, modifierStrings, triggerStrings));
            } catch (IOException e) {
                logger.severe("IOException was created whilst loading config from file: " + blockConfig.getAbsolutePath());
                e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                logger.severe("Invalid configuration at file: " + blockConfig.getAbsolutePath());
                e.printStackTrace();
            }

            if (doDebugLog())
                logger.info("Successfully loaded block config for file: '" + blockConfig.getName() + "'");
        }
    }

    public void mockConfig(Config mockConfig) {
        config = mockConfig;
    }

    public void reloadBlockConfigs() {
        BlockDatabase manager = BlockDatabase.getInstance();
        manager.clearBlockConfigs();
        if (!isMock)
            loadBlockConfigs();

        config.reloadConfig(this);

        playerListener.cancelTask();
        playerListener.removeAllEffects();
    }

    public Main() {}

    private boolean isMock = false;
    public Main(boolean isMock) {
        this.isMock = isMock;
    }
}
