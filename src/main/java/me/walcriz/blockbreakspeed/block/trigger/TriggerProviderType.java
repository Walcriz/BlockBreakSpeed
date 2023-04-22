package me.walcriz.blockbreakspeed.block.trigger;

import me.walcriz.blockbreakspeed.Main;
import me.walcriz.blockbreakspeed.block.IType;
import me.walcriz.blockbreakspeed.block.trigger.providers.CommandTriggerProvider;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public enum TriggerProviderType implements IType<ITriggerProvider> {
    CommandProvider(CommandTriggerProvider.class, "command"),
    ;

    private Class<? extends ITriggerProvider> type;
    private String configName;

    TriggerProviderType(Class<? extends ITriggerProvider> type, String configName) {
        this.type = type;
        this.configName = configName;
    }

    public static ITriggerProvider toProvider(String key, String value) {
        for (TriggerProviderType type : TriggerProviderType.values()) {
            if (!key.equals(type.configName))
                continue;

            try {
                return type.createInstance(value);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                Main.getInstance().logger.severe("Could not parse trigger settings for key: '" + key + "' and value: '" + value + "'");
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public Class<? extends ITriggerProvider> getTypeClass() {
        return type;
    }

    @Override
    public Class[] getConstructorArgs() {
        return new Class[]{ String.class };
    }
}
