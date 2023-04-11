package me.walcriz.blockbreakspeed;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import me.walcriz.blockbreakspeed.block.BlockManager;
import me.walcriz.blockbreakspeed.block.Hardness;
import me.walcriz.blockbreakspeed.commands.ReloadCommand;
import me.walcriz.blockbreakspeed.block.BlockConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin {

    private static Main instance;
    public static Main getInstance() { return instance; }

    private static PlayerListener playerListener;
    public static PlayerListener getPlayerListener() { return playerListener; }

    private static ProtocolManager protocolManager;
    public static ProtocolManager getProtocolManager() { return protocolManager; }

    public static Config config;

    public File blockFolder;

    @Override
    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        if (!isMock) {
            // Load config
            this.saveDefaultConfig();
            config = new Config(this);

            // Setup directories
            blockFolder = new File(getDataFolder(), "blocks");
            if (!blockFolder.exists())
                this.saveResource("blocks/example.yml", false);

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
    }

    private void loadBlockConfigs() {
        BlockManager manager = BlockManager.getInstance();

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
                Hardness hardness = new Hardness(configuration.getInt("hardness.base"), configuration.getInt("hardness.min"), configuration.getInt("hardness.max"));
                String[] modifierStrings = configuration.getStringList("states").toArray(new String[0]);
                String[] triggerStrings = configuration.getStringList("triggers").toArray(new String[0]);

                if (materialName == null) {
                    logger.warning("Incomplete configuration at file: " + blockConfig.getAbsolutePath());
                    continue;
                }

                Material material = Material.getMaterial(materialName);
                if (material == null) {
                    logger.warning("No material of type: '" + materialName + "' exist");
                    continue;
                }

                manager.addConfig(material, new BlockConfig(hardness, material, modifierStrings, triggerStrings));
            } catch (IOException e) {
                logger.severe("IOException was created whilst loading config from file: " + blockConfig.getAbsolutePath());
                e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                logger.severe("Invalid configuration at file: " + blockConfig.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }

    public List<BlockConfig> mockBlockConfigs(String[] modifiers, String[] triggers, Hardness hardness, Material... materials) {
//        String[] modifiers = new String[] {
//                "helditem{type=diamond_pickaxe;value=5}",
//                "sneaking{value=2}",
//                "helditem{type=stone;value=3}",
//                "helditem{type=netherite_hoe;value=1}",
//                "helditem{type=netherite_pickaxe;value=-2}",
//        };

        BlockManager manager = BlockManager.getInstance();

        List<BlockConfig> configs = new ArrayList<>();
        for (Material material : materials) {
            BlockConfig blockConfig =  new BlockConfig(hardness, material, modifiers, triggers);
            configs.add(blockConfig);
            manager.addConfig(material, blockConfig);
        }
        return configs;
    }

    public void mockConfig(Config mockConfig) {
        config = mockConfig;
    }

    public void reloadBlockConfigs() {
        BlockManager manager = BlockManager.getInstance();
        manager.clearBlockConfigs();
        if (!isMock)
            loadBlockConfigs();

        playerListener.cancelTask();
        playerListener.removeAllEffects();
    }

    public Main() {}

    private boolean isMock = false;
    public Main(boolean isMock) {
        this.isMock = isMock;
    }
}
