package me.walcriz.blockbreakspeed.block.material;

public class BlockMaterial<T> {
    T material;
    public T getMaterial() { return material; }

    public BlockMaterial(T material) {
        this.material = material;
    }
}
