package me.walcriz.blockbreakspeed.block.trigger;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public abstract class ITriggerProvider {

    private final String value;
    protected String getValue() {
        return value;
    }

    public ITriggerProvider(String value) {
        this.value = value;
    }

    public abstract void doAction(Player player, Block block);
}
