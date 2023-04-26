package me.walcriz.blockbreakspeed.block;

import me.walcriz.blockbreakspeed.Main;
import me.walcriz.blockbreakspeed.block.material.BlockMaterial;
import me.walcriz.blockbreakspeed.block.material.MaterialManager;
import org.bukkit.block.Block;

import java.util.HashMap;

public class BlockConfigMap extends HashMap<BlockMaterial<?>, BlockConfig> {
    public boolean containsKey(Block block) {
        var manager = MaterialManager.getInstance();
        var material = manager.getMaterial(block);

        return this.containsKey(material);
    }
    public BlockConfig get(Block block) {
        var manager = MaterialManager.getInstance();
        var material = manager.getMaterial(block);
        return this.get(material);
    }
}
