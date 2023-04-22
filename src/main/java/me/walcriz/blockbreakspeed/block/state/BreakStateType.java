package me.walcriz.blockbreakspeed.block.state;

import me.walcriz.blockbreakspeed.block.IType;
import me.walcriz.blockbreakspeed.block.state.impl.EffectBreakModifier;
import me.walcriz.blockbreakspeed.block.state.impl.HeldItemBreakModifier;
import me.walcriz.blockbreakspeed.block.state.impl.SneakingBreakModifier;
import me.walcriz.blockbreakspeed.utils.Pair;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public enum BreakStateType implements IType<IBreakModifier> {
    HeldItem(HeldItemBreakModifier.class, "helditem", (settings) -> { // TODO: Move into classes?
        var type = settings.getMaterial("type", "STONE");
        int value = settings.getInteger("value", 1);
        return new Object[]{ value, type };
    }, new Class[]{ Integer.class, Material.class }),

    Sneaking(SneakingBreakModifier.class, "sneaking", (settings) -> {
        int value = settings.getInteger("value", 1);
        return new Object[]{ value };
    }, new Class[]{ Integer.class }),

    Effect(EffectBreakModifier.class, "effect", (settings) -> {
        PotionEffectType type = settings.getPotionEffect("type", PotionEffectType.FAST_DIGGING);
        int level = settings.getInteger("level", -1);
        int value = settings.getInteger("value", 1);
        return new Object[]{ value, type, level };
    }, new Class[]{ Integer.class, PotionEffectType.class, Integer.class })
    ;

    private Class<? extends IBreakModifier> clazz;
    private String configName;
    private Function<BreakSettingsMap, Object[]> settingsConverter;
    private Class[] classArgs;

    /**
     * @param clazz The class of the {@link IBreakModifier}
     * @param configName In config name
     * @param settingsConverter Converter to convert string config values to objects
     * @param classArgs Argument types for *clazz*
     */
    BreakStateType(Class<? extends IBreakModifier> clazz, String configName, Function<BreakSettingsMap, Object[]> settingsConverter, Class[] classArgs) {
        this.clazz = clazz;
        this.configName = configName;
        this.settingsConverter = settingsConverter;
        this.classArgs = classArgs;
    }

    /**
     * Compile a config string
     * @param string The string to compile
     * @return String in the compiled format
     */
    public static @Nullable Pair<BreakStateType, IBreakModifier> compileString(String string) {
        String[] split = string.split(Pattern.quote("{"), 2); // Split at first '{' ex: break{command=asd} -> 1. break   2. command=asd}
        String typestring = split[0];
        String dataString = split[1].substring(0, split[1].length() - 1); // Remove the: command=asd}<- part so it becomes command=asd

        for (BreakStateType type : BreakStateType.values()) {
            if (!type.configName.equals(typestring))
                continue;

            String[] settings = dataString.split(";");
            BreakSettingsMap settingsMap = new BreakSettingsMap();
            Arrays.stream(settings).forEach((setting) -> {
                String[] parts = setting.split("=");
                settingsMap.put(parts[0], parts[1]);
            });

            try {
                IBreakModifier instance = type.createInstance(type.convertSettings(settingsMap));
                return new Pair<>(type, instance);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                return null;
            }
        }

        return null;
    }

    @Override
    public Class<? extends IBreakModifier> getTypeClass() {
        return clazz;
    }

    @Override
    public Class[] getConstructorArgs() {
        return classArgs;
    }

    public Object[] convertSettings(Map<String, String> settingsMap) {
        return settingsConverter.apply((BreakSettingsMap) settingsMap);
    }

}
