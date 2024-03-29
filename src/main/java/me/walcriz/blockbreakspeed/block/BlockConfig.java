package me.walcriz.blockbreakspeed.block;

import me.walcriz.blockbreakspeed.EffectValues;
import me.walcriz.blockbreakspeed.Main;
import me.walcriz.blockbreakspeed.block.material.BlockMaterial;
import me.walcriz.blockbreakspeed.block.state.StateModifierMap;
import me.walcriz.blockbreakspeed.block.trigger.TriggerMap;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class BlockConfig {
    private boolean cancelBreakEvent = false;

    private BlockMaterial<?> material;
    public BlockMaterial<?> getMaterial() { return material; }
    private Hardness hardness;
    public Hardness getHardness() { return hardness; }
    private BlockInfo blockInfo;
    public BlockInfo getBlockInfo() { return blockInfo; }
    private boolean suppressDrops = false;
    public boolean doSuppressDrops() { return suppressDrops; }

    public BlockConfig(Hardness hardness, BlockMaterial<?> material, boolean suppressDrops, String[] modifierStrings, String[] triggerStrings) {
        this.material = material;
        this.hardness = hardness;
        this.suppressDrops = suppressDrops;

        if (hardness.base() == 0)
            cancelBreakEvent = true;

        createBlockInfo(modifierStrings, triggerStrings);
    }

    private void createBlockInfo(String[] modifierStrings, String[] triggerStrings) {
        blockInfo = new BlockInfo(new StateModifierMap(), new TriggerMap());
        blockInfo.populateInfo(modifierStrings, triggerStrings);
    }

    public static final double hasteIncrease = 0.2d; // 0.2 * x
    public static final int hasteMaxLevel = 127;
    public static final int fatigueMaxLevel = 2;

//    private final Map<IMaterial<?>, EffectValues> effectValuesCache = new HashMap<>();

    /**
     * Get {@link EffectValues} to apply to {@link Player}
     * @param modifierMap The modifier map for this specific block
     * @param player The player trying to break a block
     * @param heldItem The current held item for the player
     * @param block The block being mined
     * @return The calculated or cached {@link EffectValues} to apply
     */
    public EffectValues getEffectValues(StateModifierMap modifierMap, Player player, @Nullable ItemStack heldItem, Block block) {
        if (cancelBreakEvent)
            return new EffectValues(0, 0);

        double speedMultiplierDiff = hardness.calculateSpeedDiff(modifierMap, player, heldItem, block);

        // hardnessTarget = (0.2x + 1) * (f(y))
        // ht / f(y) = 0.2x + 1
        // ht / f(y) - 1 = 0.2x
        // (5 * ht) / f(y) = x
        // where ht = hardnessTarget

        Function<Integer, Double> solveForX = (y) -> ((1 / hasteIncrease) * speedMultiplierDiff) / getFatigueMultiplier(y) - 1 / hasteIncrease;

        int xValue = -1;
        int yValue = -1;
        double xValueFull = -1;
        for (int y = 0; y < fatigueMaxLevel; y++) {
            double x = solveForX.apply(y);

            if (x < 0)
                continue;

            if (x > hasteMaxLevel) {
                break;
            }

            if (x % 1 == 0) { // Prefer whole numbers
                yValue = y;
                xValue = (int) Math.round(x);
                break; // We know two very good values. Stop iterating
            } else if (xValue < 0) { // If we have no value use that
                yValue = y;
                xValue = (int) Math.round(x);
            } else if (Math.abs(x - Math.round(x)) < Math.abs(xValueFull - Math.round(xValueFull))) { // If we found a more exact number. Use that
                yValue = y;
                xValue = (int) Math.round(x);
                xValueFull = x;
            }
        }

        if (xValue < 0) {
            if (speedMultiplierDiff > 0) {
                xValue = hasteMaxLevel;
                yValue = 0;
            } else {
                xValue = 0;
                yValue = fatigueMaxLevel;
            }
        }

        EffectValues effectValues = new EffectValues(xValue, yValue);

        if (Main.doDebugLog())
            Main.getPluginLogger().info(effectValues.toString());

        return effectValues;
    }

    private double getFatigueMultiplier(int level) {
        return switch (level) {
            case 0 -> 1.0;
            case 1 -> 0.3;
            case 2 -> 0.09;
            case 3 -> 0.0027;
            default -> 0.00081;
        };
    }
}
