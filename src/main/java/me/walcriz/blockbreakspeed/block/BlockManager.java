package me.walcriz.blockbreakspeed.block;

import me.walcriz.blockbreakspeed.Main;
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

    public BreakModifierMap getModifierMap(Material blockType) {
        return blockConfigMap.get(blockType).getBlockInfo().modifierMap();
    }

    public TriggerMap getTriggerMap(Material blockType) {
        return blockConfigMap.get(blockType).getBlockInfo().triggerMap();
    }

    public BlockInfo getBlockInformation(Material blockType) {
        return blockConfigMap.get(blockType).getBlockInfo();
    }

    public BlockConfigMap getBlockConfigMap() {
        return blockConfigMap;
    }

    public BlockConfig getBlockConfig(Material blockType) {
        return blockConfigMap.get(blockType);
    }

    public boolean contains(Material material) {
        return blockConfigMap.containsKey(material);
    }

    public void executeTriggers(Player player, Block block, TriggerType type) {
        TriggerMap triggers = getTriggerMap(block.getType());
        triggers.get(type).executeTriggers(player, block);
    }

    public void clearBlockConfigs() {
        this.blockConfigMap = new BlockConfigMap();
    }
}