package me.walcriz.blockbreakspeed.block.state;

import me.walcriz.blockbreakspeed.block.IType;
import me.walcriz.blockbreakspeed.block.state.impl.HeldItemBreakModifier;
import me.walcriz.blockbreakspeed.block.state.impl.SneakingBreakModifier;
import me.walcriz.blockbreakspeed.utils.Pair;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public enum BreakStateType implements IType<IBreakModifier> {
    HeldItem(HeldItemBreakModifier.class, "helditem", (settings) -> {
        Material type = Material.getMaterial(settings.getOrDefault("type", "STONE"));
        int value = Integer.parseInt(settings.getOrDefault("value", "1"));
        return new Object[]{ type, value };
    }, new Class[]{ Material.class, Integer.class }),

    Sneaking(SneakingBreakModifier.class, "sneaking", (settings) -> {
        int value = Integer.parseInt(settings.getOrDefault("value", "1"));
        return new Object[]{ value };
    }, new Class[]{ Integer.class }),
    ;

    private Class<? extends IBreakModifier> clazz;
    private String configName;
    private Function<Map<String, String>, Object[]> settingsConverter;
    private Class[] classArgs;

    /**
     * @param clazz The class of the {@link IBreakModifier}
     * @param configName In config name
     * @param settingsConverter Converter to convert string config values to objects
     * @param classArgs Argument types for *clazz*
     */
    BreakStateType(Class<? extends IBreakModifier> clazz, String configName, Function<Map<String, String>, Object[]> settingsConverter, Class[] classArgs) {
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
            Map<String, String> settingsMap = new HashMap<>();
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

    @Override
    public Object[] convertSettings(Map<String, String> settingsMap) {
        return settingsConverter.apply(settingsMap);
    }

}
