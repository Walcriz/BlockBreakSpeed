package me.walcriz.blockbreakspeed.block.state.impl;

import me.walcriz.blockbreakspeed.block.state.IBreakModifier;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public record EffectBreakModifier(int value, PotionEffectType type, int level) implements IBreakModifier {

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
