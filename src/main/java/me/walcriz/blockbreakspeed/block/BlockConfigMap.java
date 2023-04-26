package me.walcriz.blockbreakspeed.block;

import me.walcriz.blockbreakspeed.Main;
import me.walcriz.blockbreakspeed.block.material.IMaterial;
import me.walcriz.blockbreakspeed.block.material.MaterialType;
import org.bukkit.block.Block;

import java.util.HashMap;

public class BlockConfigMap extends HashMap<IMaterial<?>, BlockConfig> {
    public boolean containsKey(Block block) {
        var material = MaterialType.getMaterial(block);
        var contains = this.containsKey(material);

        if (Main.doDebugLog()) {
            var logger = Main.getPluginLogger();
            if (!contains) {
                logger.info("Material: " + material.getName() + " does not have any config!");
            } else {
                logger.info("Material: " + material.getName() + " has a config!");
            }

            logger.info("In material list:");
            this.forEach(((iMaterial, config) -> logger.info("\t" + iMaterial.getName())));
        }

        return contains;
    }
    public BlockConfig get(Block block) {
        var material = MaterialType.getMaterial(block);
        return this.get(material);
    }
}
