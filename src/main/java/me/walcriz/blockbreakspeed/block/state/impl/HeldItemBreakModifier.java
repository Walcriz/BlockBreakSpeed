package me.walcriz.blockbreakspeed.block.state.impl;

import me.walcriz.blockbreakspeed.block.state.IBreakModifier;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public record HeldItemBreakModifier(int value, Material type) implements IBreakModifier {
    @Override
    public int getModifierForPlayer(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack heldItem = inventory.getItem(inventory.getHeldItemSlot());

        if (heldItem == null)
            return 0;

        if (!heldItem.getType().equals(type()))
            return 0;

        return value();
    }
}
