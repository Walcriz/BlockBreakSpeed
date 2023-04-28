package me.walcriz.blockbreakspeed.block.state.modifiers;

import me.walcriz.blockbreakspeed.block.state.IStateModifier;
import org.bukkit.entity.Player;

public record SneakingStateModifier(int value) implements IStateModifier {
    @Override
    public int getModifierValueForPlayer(Player player) {
        if (player.isSneaking())
            return value();

        return 0;
    }
}
