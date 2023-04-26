package me.walcriz.blockbreakspeed.block.state.providers;

import me.walcriz.blockbreakspeed.block.state.IStateProvider;
import me.walcriz.blockbreakspeed.block.state.StateSettingsMap;
import me.walcriz.blockbreakspeed.block.state.modifiers.SneakingStateModifier;

public class SneakingStateProvider implements IStateProvider<SneakingStateModifier> {
    @Override
    public SneakingStateModifier getModifier(StateSettingsMap settings) {
        int value = settings.getInteger("value", 1);
        return new SneakingStateModifier(value);
    }
}
