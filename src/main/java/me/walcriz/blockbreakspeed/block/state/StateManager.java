package me.walcriz.blockbreakspeed.block.state;

import me.walcriz.blockbreakspeed.Main;
import me.walcriz.blockbreakspeed.block.state.providers.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

public class StateManager {
    private static StateManager instance;
    public static StateManager getInstance() {
        if (instance == null)
            instance = new StateManager();

        return instance;
    }

    private StateManager() {
        registerProviders();
    }

    private Map<String, IStateProvider<?>> providers = new HashMap<>();
    public void registerProviders() {
        registerProvider("helditem", new HeldItemStateProvider());
        registerProvider("effect", new EffectStateProvider());
        registerProvider("sneaking", new SneakingStateProvider());
        registerProvider("nbtstr", new NBTStrStateProvider());

        if (Main.hasMMOCore())
            registerProvider("mmoprof", new MMOProfStateProvider());
    }

    public void registerProvider(String typeCode, IStateProvider<?> provider) {
        providers.put(typeCode, provider);
    }

    /**
     * Compile a config string
     * @param string The string to compile
     * @return String in the compiled format
     */
    public @Nullable IStateModifier compileString(String string) {
        String[] split = string.split(Pattern.quote("{"), 2); // Split at first '{' ex: break{command=asd} -> 1. break   2. command=asd}
        String typestring = split[0];
        String dataString = split[1].substring(0, split[1].length() - 1); // Remove the: command=asd}<- part so it becomes command=asd

        IStateProvider<?> provider = providers.get(typestring);
        if (provider == null) {
            Main.getPluginLogger().severe("State with typecode: '" + typestring + "' does not exist!");
            return null;
        }

        String[] settings = dataString.split(";");
        StateSettingsMap settingsMap = new StateSettingsMap();
        Arrays.stream(settings).forEach((setting) -> {
            String[] parts = setting.split("=");
            settingsMap.put(parts[0], parts[1]);
        });

        return provider.buildModifier(settingsMap);
    }
}
