package me.walcriz.blockbreakspeed.block.trigger;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public record Trigger(TriggerType type, ITriggerProvider[] triggerProviders) { 
    public void executeTriggers(Player player, Block block) {
        for (ITriggerProvider provider :
                triggerProviders) {
            provider.doAction(player, block);
        }
    }
}
