package me.walcriz.blockbreakspeed.block.state.providers;

import me.walcriz.blockbreakspeed.block.state.IStateProvider;
import me.walcriz.blockbreakspeed.block.state.StateSettingsMap;
import me.walcriz.blockbreakspeed.block.state.modifiers.EffectStateModifier;
import org.bukkit.potion.PotionEffectType;

public class EffectStateProvider implements IStateProvider<EffectStateModifier> {
    @Override
    public EffectStateModifier getModifier(StateSettingsMap settings) {
        int value = settings.getInteger("value", 1);
        PotionEffectType type = settings.getPotionEffect("type", PotionEffectType.FAST_DIGGING);
        int level = settings.getInteger("level", -1);

        return new EffectStateModifier(value, type, level);
    }
}
