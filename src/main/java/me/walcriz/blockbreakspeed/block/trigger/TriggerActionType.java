package me.walcriz.blockbreakspeed.block.trigger;

import me.walcriz.blockbreakspeed.Main;
import me.walcriz.blockbreakspeed.block.trigger.actions.CommandTriggerAction;

import java.lang.reflect.InvocationTargetException;

public enum TriggerActionType implements ITriggerActionType<ITriggerAction> {
    CommandProvider(CommandTriggerAction.class, "command"),
    ;

    private Class<? extends ITriggerAction> type;
    private String configName;

    TriggerActionType(Class<? extends ITriggerAction> type, String configName) {
        this.type = type;
        this.configName = configName;
    }

    public static ITriggerAction toAction(String key, String value) {
        for (TriggerActionType type : TriggerActionType.values()) {
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
