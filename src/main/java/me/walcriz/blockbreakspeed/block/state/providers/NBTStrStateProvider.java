package me.walcriz.blockbreakspeed.block.state.providers;

import me.walcriz.blockbreakspeed.block.state.IStateProvider;
import me.walcriz.blockbreakspeed.block.state.StateSettingsMap;
import me.walcriz.blockbreakspeed.block.state.modifiers.NBTStrStateModifier;
import me.walcriz.blockbreakspeed.utils.StringHelpers;

public class NBTStrStateProvider implements IStateProvider<NBTStrStateModifier> {
    @Override
    public NBTStrStateModifier buildModifier(StateSettingsMap settings) {
        int value = settings.getInteger("value", 1);
        String key = settings.getString("key", "");
        String nbtValue = settings.getString("nbtval", "");
        StringHelpers.debugConsoleMsg("Created nbt string modifier with values: { value=" + value + ", key=" + key + ", nbtvalue=" + nbtValue + " }");
        return new NBTStrStateModifier(value, key, nbtValue);
    }
}
