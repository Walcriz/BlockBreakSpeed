package me.walcriz.blockbreakspeed.block.state;

import me.walcriz.blockbreakspeed.block.Hardness;
import me.walcriz.blockbreakspeed.utils.StringHelpers;
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
            var modifierValue = modifier.getModifierValueForPlayer(player);
            StringHelpers.debugPlayerMsg(player, "Modifier value=" + modifierValue + " for modifier: " + modifier.getClass().getName());
            value += modifierValue;
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
