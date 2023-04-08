package me.walcriz.blockbreakspeed.config;

import me.walcriz.blockbreakspeed.EffectValues;
import me.walcriz.blockbreakspeed.exceptions.TargetCalculationException;
import me.walcriz.blockbreakspeed.exceptions.TargetNegativeException;
import org.bukkit.Material;

import java.util.function.Function;

public class BlockConfig {
    private double hardnessTarget;
    private boolean cancelBreakEvent = false;
    private Material material;

    public BlockConfig(int hardnessTarget, Material material) throws TargetNegativeException {
        if (hardnessTarget < 0 )
            throw new TargetNegativeException("Target of " + material.name() + " hardness could not be calculated. Negative numbers are not supported! Was: hardnessTarget=" + hardnessTarget);

        this.material = material;
        if (hardnessTarget == 0) {
            cancelBreakEvent = true;
            this.hardnessTarget = 0;
            return;
        }

        this.hardnessTarget = hardnessTarget / 100d;
    }

    public static final double hasteIncrease = 0.2d; // 0.2 * x
    public static final double fatigueMultiplier = 0.3d; // 0.3^x
    public static final int hasteMaxLevel = 255;
    public static final int fatigueMaxLevel = 4;

    private EffectValues effectValues;
    public EffectValues getEffectValues() throws TargetCalculationException {
        if (effectValues != null) // If we already know the solution stop
            return effectValues;
        if (cancelBreakEvent)
            return new EffectValues(0, 0);

        // hardnessTarget = 0.2x * 0.3^y
        // y = log(ht/0.2x) / log(0.3)
        // where ht = hardnessTarget
        Function<Double, Double> solveForY = (x) -> Math.log(hardnessTarget / (hasteIncrease * x)) / Math.log(fatigueMultiplier);

        int xValue = -1;
        int yValue = -1;
        for (int x = 3; x < hasteMaxLevel; x++) {
            double y = solveForY.apply((double) x);

            if (y < 0) // y cant be less than 0 or
                continue;

            if (y > fatigueMaxLevel) // y cant be higher than 4 (A minecraft restriction)
                break; // This means that we already failed to find a whole number value (We will use the rounded value)

            if (y % 1 == 0) { // Prefer whole numbers
                xValue = x;
                yValue = (int) Math.floor(y);
                break; // We know two good values. Stop iterating
            } else if (yValue < 0) {
                xValue = x;
                yValue = (int) Math.round(y);
            }
        }

        if (yValue < 0)
            throw new TargetCalculationException("Failed to find a non negative y value for block: " + material.name() + "! Was: hardnessTarget=" + hardnessTarget + " | x=" + xValue + " | y=" + yValue);

        effectValues = new EffectValues(xValue, yValue);
        return effectValues;
    }
}
