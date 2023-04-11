package me.walcriz.blockbreakspeed;

public class EffectValues {
    public int hasteValue;
    public int fatigueValue;
    public boolean usePacketAnimations;

    public EffectValues(int hasteValue, int fatigueValue) {
        this.fatigueValue = fatigueValue;
        this.hasteValue = hasteValue;
        this.usePacketAnimations = hasteValue > 3 && !Main.config.disableAnimations; // Minecraft doesn't show animations for breaking over haste level 3
    }

    @Override
    public String toString() {
        return "hastevalue: " + hasteValue + ", fatigueValue: " + fatigueValue + ", usePacketAnimations: "+ usePacketAnimations;
    }
}
