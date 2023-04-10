package me.walcriz.blockbreakspeed.block.trigger;

import java.util.EnumMap;

public class TriggerMap extends EnumMap<TriggerType, Trigger> {
    public TriggerMap() {
        super(TriggerType.class);
    }
}
