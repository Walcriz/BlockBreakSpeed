package me.walcriz.blockbreakspeed.block.state.modifiers;

import de.tr7zw.changeme.nbtapi.NBT;
import me.walcriz.blockbreakspeed.Main;
import me.walcriz.blockbreakspeed.block.state.IStateModifier;
import me.walcriz.blockbreakspeed.utils.StringHelpers;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public record NBTStrStateModifier(int value, String key, String nbtValue) implements IStateModifier {
    @Override
    public int getModifierValueForPlayer(Player player) {
        StringHelpers.debugPlayerMsg(player, "{ value=" + value + ", key=" + key + ", nbtvalue=" + nbtValue + " }");
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem.getType() == Material.AIR)
            return 0;

        StringHelpers.debugPlayerMsg(player, "Is holding something");

        String data = NBT.get(heldItem, (nbt) -> nbt.getString(key()));

        if (data != null && data.equals(nbtValue()))
            return value();

        return 0;
    }
}
