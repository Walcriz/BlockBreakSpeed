package me.walcriz.blockbreakspeed.block.state;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.function.Function;

public class BreakSettingsMap extends HashMap<String, String> {

    public int getInteger(String key, int defaultValue) {
        String value = this.get(key);
        if (value == null)
            return defaultValue;

        return Integer.parseInt(value);
    }

    public String getString(String key, String defaultValue) {
        return this.getOrDefault(key, defaultValue);
    }

    public <T extends Enum<T>> T getEnum(String key, T defaultValue, Class<T> clazz) {
        String value = this.get(key);
        if (value == null)
            return defaultValue;

        return Enum.valueOf(clazz, value);
    }

    public <T extends Enum<T>> T getEnum(String key, T defaultValue, Function<String, T> converter) {
        String value = this.get(key);
        if (value == null)
            return defaultValue;

        return converter.apply(value);
    }

    public Material getMaterial(String key, Material defaultValue) {
        return getEnum(key, defaultValue, Material::getMaterial);
    }

    public PotionEffectType getPotionEffect(String key, PotionEffectType defaultValue) {
        String value = this.get(key);
        if (value == null)
            return defaultValue;

        return PotionEffectType.getByName(value);
    }
}
