package me.walcriz.blockbreakspeed.block;

import me.walcriz.blockbreakspeed.Main;
import me.walcriz.blockbreakspeed.block.state.BreakModifierMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public record Hardness(int base, int min, int max) {

    /**
     * Calculate the speed multiplier (hardness in procent) in procent. Used in the {@link BlockConfig#getEffectValues(BreakModifierMap, Player, ItemStack, Block)} calculation
     * @param modifierMap The current active states for the block
     * @param player The player trying to mine the block
     * @param block The block that is being mined
     * @return Our target hardness in procent
     * @see <a href="https://minecraft.fandom.com/wiki/Breaking#Calculation">How minecraft calculates hardness</a>
     */
    public double calculateHardnessProcent(BreakModifierMap modifierMap, Player player, @Nullable ItemStack heldItem, Block block) {

        int ticks = calculateHardnessTicks(modifierMap, player);

        // Get block max damage
        double damage = 1d / ticks;
        damage *= block.getBreakSpeed(player);

        BlockData blockData = block.getBlockData();
        boolean isPreferredTool = heldItem != null && blockData.isPreferredTool(heldItem);

        damage *= blockData.requiresCorrectToolForDrops() && isPreferredTool ? 30 : 100; // Do I really have to make my own list of every block? That'd be a pain
        // ^ Maybe not, this seems to be the good "hacky" way to do it
        // Nah found a better way.

        double speedMultiplier = damage / block.getBreakSpeed(player);

        // This is the one we want
        double hardnessProcent = speedMultiplier / (isPreferredTool ? getToolMultiplier(heldItem.getType()) : 1);
        if (Main.doDebugLog())
            Main.logger.info("Got 'hardnessProcent: " + hardnessProcent + "' for player: " + player.getName());

        return hardnessProcent;
    }

    public int calculateHardnessTicks(BreakModifierMap modifierMap, Player player) {
        int tickModifier = modifierMap.getCurrentModifiers(player);

        if (tickModifier > max())
            tickModifier = max();
        else if (tickModifier < min())
            tickModifier = min();

        return base() - tickModifier;
    }

    private int getToolMultiplier(Material material) {
        return switch (material) {
            case WOODEN_PICKAXE, WOODEN_AXE, WOODEN_HOE, WOODEN_SHOVEL, WOODEN_SWORD -> 2;
            case STONE_PICKAXE, STONE_AXE, STONE_HOE, STONE_SHOVEL, STONE_SWORD -> 4;
            case IRON_PICKAXE, IRON_AXE, IRON_HOE, IRON_SHOVEL, IRON_SWORD -> 6;
            case GOLDEN_PICKAXE, GOLDEN_AXE, GOLDEN_HOE, GOLDEN_SHOVEL, GOLDEN_SWORD -> 12;
            case DIAMOND_PICKAXE, DIAMOND_AXE, DIAMOND_HOE, DIAMOND_SHOVEL, DIAMOND_SWORD -> 8;
            case NETHERITE_PICKAXE, NETHERITE_AXE, NETHERITE_HOE, NETHERITE_SHOVEL, NETHERITE_SWORD -> 9;
            default -> 1;
        };
    }
}
