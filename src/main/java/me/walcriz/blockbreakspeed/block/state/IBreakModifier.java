package me.walcriz.blockbreakspeed.block.state;

import org.bukkit.entity.Player;

public interface IBreakModifier {
    int getModifierForPlayer(Player player);
}
