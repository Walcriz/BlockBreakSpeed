package me.walcriz.blockbreakspeed.block.material;

import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

public abstract class IMaterial<T> {
    private final T type;
    protected T getType() { return type; }

    public IMaterial(String blockType) {
        this.type = materialFromString(blockType);
    }

    // Abstract
    public abstract boolean equals(Block block);
    public abstract String getName();
    protected abstract @Nullable T materialFromString(String blockType);
}
