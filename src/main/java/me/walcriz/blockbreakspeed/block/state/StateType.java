package me.walcriz.blockbreakspeed.block.state;

import me.walcriz.blockbreakspeed.block.IType;
import me.walcriz.blockbreakspeed.block.state.impl.EffectStateModifier;
import me.walcriz.blockbreakspeed.block.state.impl.HeldItemStateModifier;
import me.walcriz.blockbreakspeed.block.state.impl.SneakingStateModifier;
import me.walcriz.blockbreakspeed.utils.Pair;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public enum StateType implements IType<IStateModifier> { // TODO: Refactor
    HeldItem(HeldItemStateModifier.class, "helditem", (settings) -> { // TODO: Move into classes?
        var type = settings.getMaterial("type", "STONE");
        int value = settings.getInteger("value", 1);
        return new Object[]{ value, type };
    }, new Class[]{ Integer.class, Material.class }),

    Sneaking(SneakingStateModifier.class, "sneaking", (settings) -> {
        int value = settings.getInteger("value", 1);
        return new Object[]{ value };
    }, new Class[]{ Integer.class }),

    Effect(EffectStateModifier.class, "effect", (settings) -> {
        PotionEffectType type = settings.getPotionEffect("type", PotionEffectType.FAST_DIGGING);
        int level = settings.getInteger("level", -1);
        int value = settings.getInteger("value", 1);
        return new Object[]{ value, type, level };
    }, new Class[]{ Integer.class, PotionEffectType.class, Integer.class })
    ;

    private Class<? extends IStateModifier> clazz;
    private String configName;
    private Function<StateSettingsMap, Object[]> settingsConverter;
    private Class[] classArgs;

    /**
     * @param clazz The class of the {@link IStateModifier}
     * @param configName In config name
     * @param settingsConverter Converter to convert string config values to objects
     * @param classArgs Argument types for *clazz*
     */
    StateType(Class<? extends IStateModifier> clazz, String configName, Function<StateSettingsMap, Object[]> settingsConverter, Class[] classArgs) {
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
    public static @Nullable Pair<StateType, IStateModifier> compileString(String string) {
        String[] split = string.split(Pattern.quote("{"), 2); // Split at first '{' ex: break{command=asd} -> 1. break   2. command=asd}
        String typestring = split[0];
        String dataString = split[1].substring(0, split[1].length() - 1); // Remove the: command=asd}<- part so it becomes command=asd

        for (StateType type : StateType.values()) {
            if (!type.configName.equals(typestring))
                continue;

            String[] settings = dataString.split(";");
            StateSettingsMap settingsMap = new StateSettingsMap();
            Arrays.stream(settings).forEach((setting) -> {
                String[] parts = setting.split("=");
                settingsMap.put(parts[0], parts[1]);
            });

            try {
                IStateModifier instance = type.createInstance(type.convertSettings(settingsMap));
                return new Pair<>(type, instance);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                return null;
            }
        }

        return null;
    }

    @Override
    public Class<? extends IStateModifier> getTypeClass() {
        return clazz;
    }

    @Override
    public Class[] getConstructorArgs() {
        return classArgs;
    }

    public Object[] convertSettings(Map<String, String> settingsMap) {
        return settingsConverter.apply((StateSettingsMap) settingsMap);
    }

}
