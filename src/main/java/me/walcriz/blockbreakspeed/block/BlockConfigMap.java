package me.walcriz.blockbreakspeed.block;

import me.walcriz.blockbreakspeed.block.material.IMaterial;
import me.walcriz.blockbreakspeed.block.material.MaterialType;
import org.bukkit.block.Block;

import java.util.HashMap;

public class BlockConfigMap extends HashMap<IMaterial<?>, BlockConfig> {
    public boolean containsKey(Block block) {
        return this.containsKey(MaterialType.getMaterial(block));
    }
    public BlockConfig get(Block block) {
        return this.get(MaterialType.getMaterial(block));
    }
}
