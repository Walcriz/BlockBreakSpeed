package me.walcriz.blockbreakspeed.block.material;

import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

public abstract class IMaterialProvider<T> {
    private Map<T, BlockMaterial<T>> materialCache = new HashMap<>();
    protected BlockMaterial<T> addOrGetMaterial(T material) {
        BlockMaterial<T> blockMaterial = materialCache.getOrDefault(material, null);
        if (blockMaterial != null)
            return blockMaterial;

        blockMaterial = new BlockMaterial<>(material);
        materialCache.put(material, blockMaterial);
        return blockMaterial;
    }

    public abstract String getName(BlockMaterial<T> material);

    public abstract BlockMaterial<T> materialFromBlock(Block block);
    public abstract BlockMaterial<T> materialFromString(String string);
}
