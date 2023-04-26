package me.walcriz.blockbreakspeed.block.trigger;

import me.walcriz.blockbreakspeed.Main;
import me.walcriz.blockbreakspeed.block.IType;
import me.walcriz.blockbreakspeed.block.trigger.actions.CommandTriggerAction;

import java.lang.reflect.InvocationTargetException;

public enum TriggerProviderType implements IType<ITriggerAction> {
    CommandProvider(CommandTriggerAction.class, "command"),
    ;

    private Class<? extends ITriggerAction> type;
    private String configName;

    TriggerProviderType(Class<? extends ITriggerAction> type, String configName) {
        this.type = type;
        this.configName = configName;
    }

    public static ITriggerAction toProvider(String key, String value) {
        for (TriggerProviderType type : TriggerProviderType.values()) {
            if (!key.equals(type.configName))
                continue;

            try {
                return type.createInstance(value);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                Main.getPluginLogger().severe("Could not parse trigger settings for key: '" + key + "' and value: '" + value + "'");
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public Class<? extends ITriggerAction> getTypeClass() {
        return type;
    }

    @Override
    public Class[] getConstructorArgs() {
        return new Class[]{ String.class };
    }
}
