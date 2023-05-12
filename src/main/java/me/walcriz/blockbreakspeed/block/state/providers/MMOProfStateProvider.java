package me.walcriz.blockbreakspeed.block.state.providers;

import me.walcriz.blockbreakspeed.block.state.IStateProvider;
import me.walcriz.blockbreakspeed.block.state.StateSettingsMap;
import me.walcriz.blockbreakspeed.block.state.modifiers.MMOProfStateModifier;

public class MMOProfStateProvider implements IStateProvider<MMOProfStateModifier> {
    @Override
    public MMOProfStateModifier buildModifier(StateSettingsMap settings) {
        int value = settings.getInteger("value", 1);
        String name = settings.getString("name", "");
        int level = settings.getInteger("level", 0);
        return null;
    }
}
