package me.walcriz.blockbreakspeed.block.material;

import me.walcriz.blockbreakspeed.Main;
import me.walcriz.blockbreakspeed.block.IType;
import me.walcriz.blockbreakspeed.block.material.impl.MaterialBlockProvider;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum MaterialType implements IType<IMaterial<?>> {
    MinecraftMaterial(MaterialBlockProvider.class, Material::getMaterial, block -> getMaterial(block.getType().name()), 0), // Kinda hate this
    ;

    final Class<? extends IMaterial<?>> providerClass;
    final Function<String, ?> isCorrect;
    final Function<Block, IMaterial<?>> blockToMaterial;
    final int priority;

    <T> MaterialType(Class<? extends IMaterial<T>> providerClass, Function<String, ?> isCorrectString, Function<Block, IMaterial<?>> blockToMaterial, int priority) {
        this.providerClass = providerClass;
        this.isCorrect = isCorrectString;
        this.blockToMaterial = blockToMaterial;
        this.priority = priority;
    }

    public boolean isCorrect(String string) {
        var correct = this.isCorrect.apply(string);
        if (correct instanceof Boolean bool)
            return bool;

        return correct != null;
    }

    private static Map<String, IMaterial<?>> materials = new HashMap<>();
    public static IMaterial<?> getMaterial(String string) {
        var material = materials.get(string);
        if (material != null)
            return material;

        for (MaterialType type : values()){
            if (!type.isCorrect(string))
                continue;

            try {
                var newMaterial = type.createInstance(string);
                materials.put(string, newMaterial);
                return newMaterial;
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                return null;
            }
        }

        return null;
    }

    public static IMaterial<?> getMaterial(Block block) {
        int lastPriority = 0;
        IMaterial<?> material = null;

        for (MaterialType type : values()){
            IMaterial<?> typeMaterial = type.blockToMaterial.apply(block);
            if (typeMaterial == null) {
                if (Main.doDebugLog())
                    Main.logger.info("Material was null");
                continue;
            }

            if (Main.doDebugLog())
                Main.logger.info("Got: " + typeMaterial.getName());

            if (type.priority < lastPriority)
                continue;

            lastPriority = type.priority;
            material = typeMaterial;
        }

        return material;
    }

    @Override
    public Class<? extends IMaterial<?>> getTypeClass() {
        return providerClass;
    }

    @Override
    public Class[] getConstructorArgs() {
        return new Class[]{ String.class };
    }

    // Not needed
    @Override
    public Object[] convertSettings(Map<String, String> settingsMap) {
        return new Object[0];
    }
}
