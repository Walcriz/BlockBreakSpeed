package me.walcriz.blockbreakspeed.block.state.providers;

import me.walcriz.blockbreakspeed.block.state.IStateProvider;
import me.walcriz.blockbreakspeed.block.state.StateSettingsMap;
import me.walcriz.blockbreakspeed.block.state.modifiers.HeldItemStateModifier;
import org.bukkit.Material;

public class HeldItemStateProvider implements IStateProvider<HeldItemStateModifier> {
    @Override
    public HeldItemStateModifier getModifier(StateSettingsMap settings) {
        int value = settings.getInteger("value", 1);
        var type = settings.getVanillaMaterial("type", Material.STONE);
        return new HeldItemStateModifier(value, type);
    }
}
