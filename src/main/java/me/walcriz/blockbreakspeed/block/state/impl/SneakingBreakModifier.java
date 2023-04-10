package me.walcriz.blockbreakspeed.block.state.impl;

import me.walcriz.blockbreakspeed.block.state.IBreakModifier;
import org.bukkit.entity.Player;

public record SneakingBreakModifier(int value) implements IBreakModifier {
    @Override
    public int getModifierForPlayer(Player player) {
        if (player.isSneaking())
            return value();

        return 0;
    }
}
