package me.walcriz.blockbreakspeed.block.trigger;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public record Trigger(TriggerType type, ITriggerAction[] triggerProviders) {
    public void executeTriggers(Player player, Block block) {
        for (ITriggerAction provider :
                triggerProviders) {
            provider.doAction(player, block);
        }
    }
}
