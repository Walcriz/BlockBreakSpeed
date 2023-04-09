package me.walcriz.blockbreakspeed;

public class EffectValues {
    public int hasteValue;
    public int fatigueValue;
    public boolean usePacketAnimations;

    public EffectValues(int fatigueValue, int hasteValue) {
        this.fatigueValue = fatigueValue;
        this.hasteValue = hasteValue;
        this.usePacketAnimations = hasteValue > 3 && !Main.config.disableAnimations; // Minecraft doesn't show animations for breaking over haste level 3
    }
}
