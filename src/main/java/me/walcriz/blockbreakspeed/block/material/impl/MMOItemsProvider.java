package me.walcriz.blockbreakspeed.block.material.impl;

import me.walcriz.blockbreakspeed.block.material.BlockMaterial;
import me.walcriz.blockbreakspeed.block.material.IMaterialProvider;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.block.Block;

public class MMOItemsProvider extends IMaterialProvider<Type> {
    @Override
    public String getName(BlockMaterial<Type> material) {
        return material.getMaterial().getId();
    }

    @Override
    public BlockMaterial<Type> materialFromBlock(Block block) {
        var optionalCustomBlock = MMOItems.plugin.getCustomBlocks().getFromBlock(block.getBlockData());
        if (optionalCustomBlock.isEmpty())
            return null;

        var customBlock = optionalCustomBlock.get();
        var item = customBlock.getItem();
        return addOrGetMaterial(Type.get(item));
    }

    @Override
    public BlockMaterial<Type> materialFromString(String string) {
        return addOrGetMaterial(Type.get(string));
    }
}
