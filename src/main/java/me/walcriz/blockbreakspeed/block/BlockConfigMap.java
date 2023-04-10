package me.walcriz.blockbreakspeed.block;

import org.bukkit.Material;

import java.util.EnumMap;

public class BlockConfigMap extends EnumMap<Material, BlockConfig> {
    public BlockConfigMap() {
        super(Material.class);
    }
}
