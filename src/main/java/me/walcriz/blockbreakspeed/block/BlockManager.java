package me.walcriz.blockbreakspeed.block;

import me.walcriz.blockbreakspeed.Main;
import me.walcriz.blockbreakspeed.block.material.IMaterial;
import me.walcriz.blockbreakspeed.block.material.MaterialType;
import me.walcriz.blockbreakspeed.block.state.BreakModifierMap;
import me.walcriz.blockbreakspeed.block.trigger.TriggerMap;
import me.walcriz.blockbreakspeed.block.trigger.TriggerType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public final class BlockManager {
    private static BlockManager instance;
    public static BlockManager getInstance() {
        if (instance == null)
            instance = new BlockManager();
        return instance;
    }

    private BlockManager() {}

    private BlockConfigMap blockConfigMap = new BlockConfigMap(); // TODO: Populate this map

    public BreakModifierMap getModifierMap(Block block) {
        return blockConfigMap.get(block).getBlockInfo().modifierMap();
    }

    public TriggerMap getTriggerMap(Block block) {
        return blockConfigMap.get(block).getBlockInfo().triggerMap();
    }

    public BlockInfo getBlockInformation(Block block) {
        return blockConfigMap.get(block).getBlockInfo();
    }

    public BlockConfigMap getBlockConfigMap() {
        return blockConfigMap;
    }

    public BlockConfig getBlockConfig(Block block) {
        return blockConfigMap.get(block);
    }

    public boolean contains(Block material) {
        return blockConfigMap.containsKey(material);
    }

    public void addConfig(IMaterial<?> blockType, BlockConfig config) {
        getBlockConfigMap().put(blockType, config);
    }

    public void executeTriggers(Player player, Block block, TriggerType type) {
        TriggerMap triggers = getTriggerMap(block);
        if (triggers.containsKey(type))
            triggers.get(type).executeTriggers(player, block);
    }

    public void clearBlockConfigs() {
        this.blockConfigMap = new BlockConfigMap();
    }
}