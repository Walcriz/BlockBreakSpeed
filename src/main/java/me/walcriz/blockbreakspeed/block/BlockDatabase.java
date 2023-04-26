package me.walcriz.blockbreakspeed.block;

import me.walcriz.blockbreakspeed.block.material.BlockMaterial;
import me.walcriz.blockbreakspeed.block.state.StateModifierMap;
import me.walcriz.blockbreakspeed.block.trigger.TriggerMap;
import me.walcriz.blockbreakspeed.block.trigger.TriggerType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public final class BlockDatabase {
    private static BlockDatabase instance;
    public static BlockDatabase getInstance() {
        if (instance == null)
            instance = new BlockDatabase();
        return instance;
    }

    private BlockDatabase() {}

    private BlockConfigMap blockConfigMap = new BlockConfigMap(); // TODO: Populate this map

    public StateModifierMap getModifierMap(Block block) {
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

    public void addConfig(BlockMaterial<?> blockType, BlockConfig config) {
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