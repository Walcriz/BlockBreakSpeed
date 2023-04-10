package me.walcriz.blockbreakspeed.block.trigger;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

public enum TriggerType {
    Start("start"),
    Break("break"),
    Abort("abort"),
    Stop("stop"),
    ;

    private String configName;

    TriggerType(String configName) {
        this.configName = configName;
    }

    /**
     * Compile a config string
     * @param string The string to compile
     * @return String in the compiled format
     */
    public static @Nullable Trigger compileString(String string) { // Kinda hate to have duplicate of this
        String[] split = string.split(Pattern.quote("{"), 2); // Split at first '{' ex: break{command=asd} -> 1. break   2. command=asd}
        String typestring = split[0];
        String dataString = split[1].substring(0, split[1].length() - 1); // Remove the: command=asd}<- part so it becomes command=asd

        for (TriggerType type : TriggerType.values()) {
            if (!type.configName.equals(typestring))
                continue;

            String[] settings = dataString.split(";");
            Map<String, String> settingsMap = new HashMap<>();
            Arrays.stream(settings).forEach((setting) -> {
                String[] parts = setting.split("=");
                settingsMap.put(parts[0], parts[1]/*.substring(1, parts[1].length() - 2)*/);
            });

            return new Trigger(type, type.convertSettings(settingsMap));
        }

        return null;
    }

    public ITriggerProvider[] convertSettings(Map<String, String> settingsMap) {
        List<ITriggerProvider> arguments = new ArrayList<>();
        settingsMap.forEach((key, value) -> {
            arguments.add(TriggerProviderType.toProvider(key, value));
        });
        return arguments.toArray(new ITriggerProvider[0]);
    }

}
