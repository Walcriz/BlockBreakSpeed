package me.walcriz.blockbreakspeed.block.state;

import org.bukkit.entity.Player;

public interface IStateModifier {
    int getModifierForPlayer(Player player);
}
