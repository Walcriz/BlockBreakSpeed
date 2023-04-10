package me.walcriz.blockbreakspeed.block;

import me.walcriz.blockbreakspeed.EffectValues;
import me.walcriz.blockbreakspeed.block.state.BreakModifierMap;
import me.walcriz.blockbreakspeed.block.trigger.TriggerMap;
import me.walcriz.blockbreakspeed.exceptions.TargetCalculationException;
import me.walcriz.blockbreakspeed.exceptions.TargetNegativeException;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class BlockConfig {
    public boolean cancelBreakEvent = false;

    private Material material;
    private Hardness hardness;
    private BlockInfo blockInfo;
    public BlockInfo getBlockInfo() { return blockInfo; }

    public BlockConfig(Hardness hardness, Material material, String[] modifierStrings, String[] triggerStrings) throws TargetNegativeException {
        this.material = material;
        this.hardness = hardness;

        if (hardness.base() == 0)
            cancelBreakEvent = true;

        createBlockInfo(modifierStrings, triggerStrings);
    }

    private void createBlockInfo(String[] modifierStrings, String[] triggerStrings) {
        blockInfo = new BlockInfo(new BreakModifierMap(), new TriggerMap());
        blockInfo.populateInfo(modifierStrings, triggerStrings);
    }

    public static final double hasteIncrease = 0.2d; // 0.2 * x
    public static final double fatigueMultiplier = 0.3d; // 0.3^x
    public static final int hasteMaxLevel = 255;
    public static final int fatigueMaxLevel = 4;

    private final Map<Material, EffectValues> effectValuesCache = new HashMap<>();

    /**
     * Get {@link EffectValues} to apply to {@link Player}
     * @param modifierMap The modifier map for this specific block
     * @param player The player trying to break a block
     * @param heldItem The current held item for the player
     * @param block The block being mined
     * @return The calculated or cached {@link EffectValues} to apply
     * @throws TargetCalculationException Thrown if the target could not be reached
     */
    public EffectValues getEffectValues(BreakModifierMap modifierMap, Player player, ItemStack heldItem, Block block) throws TargetCalculationException {
        if (effectValuesCache.containsKey(heldItem.getType())) // If we already know the solution stop
            return effectValuesCache.get(heldItem.getType());

        if (cancelBreakEvent)
            return new EffectValues(0, 0);

        double hardnessTarget = hardness.calculateHardnessProcent(modifierMap, player, heldItem, block);

        // hardnessTarget = 0.2x * 0.3^y
        // y = log(ht/0.2x) / log(0.3)
        // where ht = hardnessTarget
        Function<Double, Double> solveForY = (x) -> Math.log(hardnessTarget / (hasteIncrease * x + 1)) / Math.log(fatigueMultiplier);

        int xValue = -1;
        int yValue = -1;
        double yValueFull = -1;
        for (int x = 0; x < hasteMaxLevel; x++) {
            double y = solveForY.apply((double) x);

            if (y < 0) // y cant be less than 0
                continue;

            if (y > fatigueMaxLevel) // y cant be higher than 4 (A minecraft restriction)
                break; // This means that we already failed to find a whole number value (We will use the rounded value)

            if (y % 1 == 0) { // Prefer whole numbers
                xValue = x;
                yValue = (int) Math.floor(y);
                break; // We know two very good values. Stop iterating
            } else if (yValue < 0) { // If we have no value use that
                xValue = x;
                yValue = (int) Math.round(y);
                yValueFull = y;
            } else if (Math.abs(y - Math.round(y)) < Math.abs(yValueFull - Math.round(yValueFull))) { // If we found a more exact number. Use that
                xValue = x;
                yValue = (int) Math.round(y);
                yValueFull = y;
            }
        }

        if (yValue < 0)
            throw new TargetCalculationException("Failed to find a non negative y value for block: " + material.name() + "! Was: hardnessTarget=" + hardnessTarget + " | x=" + xValue + " | y=" + yValue);

        EffectValues effectValues = new EffectValues(xValue, yValue);
        effectValuesCache.put(heldItem.getType(), effectValues);
        return effectValues;
    }
}
