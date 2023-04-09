package me.walcriz.blockbreakspeed.config;

import me.walcriz.blockbreakspeed.Main;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    public Config(Main main) {
        FileConfiguration config = main.getConfig();
        this.disableAnimations = config.getBoolean("animations.disable");
        this.animationDelay = config.getInt("animations.delay");
        this.miningCheckTicks = config.getInt("mining-check-delay");
    }

    public boolean disableAnimations = false;
    public int animationDelay = 10;

    public int miningCheckTicks = 2;
}
