package me.walcriz.blockbreakspeed.block.state;

import me.walcriz.blockbreakspeed.block.material.BlockMaterial;
import me.walcriz.blockbreakspeed.block.material.MaterialManager;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.function.Function;

public class StateSettingsMap extends HashMap<String, String> {

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

    public BlockMaterial<?> getMaterial(String key, String defaultValue) {
        String value = this.get(key);
        MaterialManager manager = MaterialManager.getInstance();
        if (value == null)
            return manager.getMaterial(defaultValue);

        return manager.getMaterial(value);
    }

    public Material getVanillaMaterial(String key, Material defaultValue) {
        Material material = Material.getMaterial(key);
        if (material == null)
            return defaultValue;

        return material;
    }

    public PotionEffectType getPotionEffect(String key, PotionEffectType defaultValue) {
        String value = this.get(key);
        if (value == null)
            return defaultValue;

        return PotionEffectType.getByName(value);
    }
}
