package me.walcriz.blockbreakspeed.block.state;

import me.walcriz.blockbreakspeed.block.Hardness;
import me.walcriz.blockbreakspeed.utils.Pair;
import org.bukkit.entity.Player;

import java.util.EnumMap;

public class BreakModifierMap extends EnumMap<BreakStateType, IBreakModifier> {
    public BreakModifierMap() {
        super(BreakStateType.class);
    }

    /**
     * Get current active modifiers for a {@link Player}
     * @param player The player to get modifiers for
     * @return The hardness value to subtract {@link Hardness#base()} with
     */
    public int getCurrentModifiers(Player player) {
        int value = 0;
        for (IBreakModifier modifier : this.values()) {
            value += modifier.getModifierForPlayer(player);
        }
        return value;
    }

    /**
     * Add modifier to map
     * @param modifier The modifier to add
     */
    public void addModifier(Pair<BreakStateType, IBreakModifier> modifier) {
        this.put(modifier.getKey(), modifier.getValue());
    }
}
