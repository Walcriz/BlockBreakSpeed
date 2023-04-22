package me.walcriz.blockbreakspeed.block.material.impl;

import me.walcriz.blockbreakspeed.block.material.IMaterial;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

public class MaterialBlockProvider extends IMaterial<Material> {
    public MaterialBlockProvider(String blockType) {
        super(blockType);
    }

    @Override
    public boolean equals(Block block) {
        return block.getType().equals(this.getType());
    }

    @Override
    public String getName() {
        return getType().name();
    }

    @Override
    protected @Nullable Material materialFromString(String blockType) {
        return Material.getMaterial(blockType);
    }


}
