package me.walcriz.blockbreakspeed.block.state.modifiers;

import me.walcriz.blockbreakspeed.block.state.IStateModifier;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public record EffectStateModifier(int value, PotionEffectType type, int level) implements IStateModifier {

    @Override
    public int getModifierForPlayer(Player player) {
        PotionEffect effect = player.getPotionEffect(type());
        if (effect == null)
            return 0;

        if (level() != -1 && effect.getAmplifier() != level())
            return 0;

        return value();
    }
}
