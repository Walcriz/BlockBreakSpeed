package me.walcriz.blockbreakspeed.block;

import me.walcriz.blockbreakspeed.block.state.StateModifierMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public record Hardness(int base, int min, int max) {

    /**
     * Calculate the speed multiplier (hardness in percent) in percent. Used in the {@link BlockConfig#getEffectValues(StateModifierMap, Player, ItemStack, Block)} calculation
     * @param modifierMap The current active states for the block
     * @param player The player trying to mine the block
     * @param block The block that is being mined
     * @return Our target hardness in percent
     * @see <a href="https://minecraft.fandom.com/wiki/Breaking#Calculation">How minecraft calculates hardness</a>
     */
    public double calculateSpeedDiff(StateModifierMap modifierMap, Player player, @Nullable ItemStack heldItem, Block block) {
        boolean isValidTool = heldItem != null && block.isValidTool(heldItem);
        boolean isPreferredTool = heldItem != null && block.isPreferredTool(heldItem);
        boolean requiresValidTool = block.getBlockData().requiresCorrectToolForDrops();

        var hardness = block.getType().getHardness();

        if (hardness <= 0) return 1;

        var speedMultiplier = 1.0;
        if (isPreferredTool) {
            var toolType = heldItem.getType();
            speedMultiplier = getToolMultiplier(toolType);
            var efficiency = heldItem.getEnchantmentLevel(Enchantment.DIG_SPEED);
            if (efficiency > 0) {
                speedMultiplier += efficiency * efficiency + 1;
            }
        }

        // Haste and fatigue

        //

        // damage = speedmilti / hardness / (reqvalidTool ? 30 : 100)
        // damage * ((reqvalidTool ? 30 : 100) * hardness = speedmulti

        // hardnessPercent = speedMultiafter / speedMultiBefore

        int ticks = calculateHardnessTicks(modifierMap, player);

        // Get block max damage
        double damage = 1d / ticks;

        var speedMultiplierAfter = damage * ((requiresValidTool && isValidTool) ? 30 : 100) * hardness;
        var multiplierDiff = speedMultiplierAfter / speedMultiplier; // Now we know how much haste and fatigue should make up
        System.out.println(multiplierDiff);

        return multiplierDiff;
    }

    public int calculateHardnessTicks(StateModifierMap modifierMap, Player player) {
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
