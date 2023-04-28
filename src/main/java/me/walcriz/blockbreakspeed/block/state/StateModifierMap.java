package me.walcriz.blockbreakspeed.block.state;

import me.walcriz.blockbreakspeed.block.Hardness;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class StateModifierMap extends HashSet<IStateModifier> {

    /**
     * Get current active modifiers for a {@link Player}
     * @param player The player to get modifiers for
     * @return The hardness value to subtract {@link Hardness#base()} with
     */
    public int getCurrentModifiers(Player player) {
        int value = 0;
        for (IStateModifier modifier : this) {
            value += modifier.getModifierValueForPlayer(player);
        }
        return value;
    }

    /**
     * Add modifier to map
     * @param modifier The modifier to add
     */
    public void addModifier(IStateModifier modifier) {
        this.add(modifier);
    }
}
