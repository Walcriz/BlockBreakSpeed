package me.walcriz.blockbreakspeed;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    /**
     * Initialize config with default values. (Used when mocking and testing)
     */
    public Config(int miningCheckTicks) {
        this.miningCheckTicks = miningCheckTicks;
    }

    /**
     * Initialize config to load values from config
     * @param main Main plugin instance
     */
    public Config(Main main) {
        FileConfiguration config = main.getConfig();

        this.disableAnimations = config.getBoolean("animations.disable"); // I'm going to be so confused when I see this next time

        this.animationDelay = config.getInt("animations.delay");
        this.miningCheckTicks = config.getInt("mining-check-delay");
    }

    public boolean disableAnimations = true;
    public int animationDelay = 10;

    public int miningCheckTicks = 2;
}
