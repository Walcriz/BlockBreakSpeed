package me.walcriz.blockbreakspeed.block.state;

public interface IStateProvider<T extends IStateModifier> {
    T getModifier(StateSettingsMap settings);
}
