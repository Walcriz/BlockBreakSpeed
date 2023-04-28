package me.walcriz.blockbreakspeed.block.material;

import me.walcriz.blockbreakspeed.Main;
import me.walcriz.blockbreakspeed.block.material.impl.VanillaMaterialProvider;
import me.walcriz.blockbreakspeed.utils.Pair;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.*;

public class MaterialManager {

    private static MaterialManager instance;
    public static MaterialManager getInstance() {
        if (instance == null)
            instance = new MaterialManager();

        return instance;
    }

    private MaterialManager() {
        registerProviders();
    }

    private List<Pair<Integer, IMaterialProvider<?>>> providers = new ArrayList<>();

    public void registerProviders() {
        registerProvider(0, new VanillaMaterialProvider());
        providers.sort(Comparator.comparingInt(Pair::getKey));
    }

    public void registerProvider(int priority, IMaterialProvider<?> provider) {
        providers.add(new Pair<>(priority, provider));
    }

    public BlockMaterial<?> getMaterial(String name) {
        for (var providerData : providers) {
            IMaterialProvider<?> provider = providerData.getValue();
            BlockMaterial<?> material = provider.materialFromString(name);
            if (material != null)
                return material;
        }

        return null;
    }

    public BlockMaterial<?> getMaterial(Block block) {
        for (var providerData : providers) {
            IMaterialProvider<?> provider = providerData.getValue();
            BlockMaterial<?> material = provider.materialFromBlock(block);
            if (material != null)
                return material;
        }

        return null;
    }
}
