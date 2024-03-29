package me.walcriz.blockbreakspeed;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    /**
     * Initialize config with default values. (Used when mocking and testing)
     */
    public Config() {
    }

    /**
     * Initialize config to load values from config
     * @param main Main plugin instance
     */
    public Config(Main main) {
        reloadConfig(main);
    }

    /**
     * Reload the current config
     * @param main {@link org.bukkit.plugin.java.JavaPlugin}
     */
    public void reloadConfig(Main main) {
        FileConfiguration config = main.getConfig();

//        this.disableAnimations = config.getBoolean("animations.disable");
//        this.animationDelay = config.getInt("animations.delay");
        this.debugLogging = config.getBoolean("debug-logging");
//        this.animationTaskRepeatTime = config.getInt("animations.task-repeat-time");
    }

    public boolean disableAnimations = true;
    public int animationDelay = 10;
    public int animationTaskRepeatTime = 1;

    public boolean debugLogging = false;
}
