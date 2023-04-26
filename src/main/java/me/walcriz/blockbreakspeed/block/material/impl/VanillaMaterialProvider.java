package me.walcriz.blockbreakspeed.block.material.impl;

import me.walcriz.blockbreakspeed.block.material.BlockMaterial;
import me.walcriz.blockbreakspeed.block.material.IMaterialProvider;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class VanillaMaterialProvider extends IMaterialProvider<Material> {

    @Override
    public String getName(BlockMaterial<Material> material) {
        return material.getMaterial().name();
    }

    @Override
    public String getName(Block block) {
        return block.getType().name();
    }

    @Override
    public BlockMaterial<Material> materialFromBlock(Block block) {
        return addOrGetMaterial(block.getType());
    }

    @Override
    public BlockMaterial<Material> materialFromString(String string) {
        Material material = Material.getMaterial(string);
        if (material == null)
            return null;

        return addOrGetMaterial(material);
    }
}
