package me.walcriz.blockbreakspeed.block.state.providers;

import me.walcriz.blockbreakspeed.block.state.IStateProvider;
import me.walcriz.blockbreakspeed.block.state.StateSettingsMap;
import me.walcriz.blockbreakspeed.block.state.modifiers.NBTStrStateModifier;

public class NBTStrProvider implements IStateProvider<NBTStrStateModifier> {
    @Override
    public NBTStrStateModifier buildModifier(StateSettingsMap settings) {
        int value = settings.getInteger("value", 1);
        String key = settings.getString("key", "");
        String nbtValue = settings.getString("nbtval", "");
        return new NBTStrStateModifier(value, key, nbtValue);
    }
}
