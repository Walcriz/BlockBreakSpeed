package me.walcriz.blockbreakspeed.block;

import me.walcriz.blockbreakspeed.block.state.BreakModifierMap;
import me.walcriz.blockbreakspeed.block.state.BreakStateType;
import me.walcriz.blockbreakspeed.block.state.IBreakModifier;
import me.walcriz.blockbreakspeed.block.trigger.Trigger;
import me.walcriz.blockbreakspeed.block.trigger.TriggerMap;
import me.walcriz.blockbreakspeed.block.trigger.TriggerType;
import me.walcriz.blockbreakspeed.utils.Pair;

public record BlockInfo(BreakModifierMap modifierMap, TriggerMap triggerMap) {
    public void populateInfo(String[] modifierStrings, String[] triggerStrings) {
        populateModifiers(modifierStrings);
        populateTriggers(triggerStrings);
    }

    private void populateModifiers(String[] modifierStrings) {
        for (String modifier : modifierStrings) {
            Pair<BreakStateType, IBreakModifier> compiledModifier = BreakStateType.compileString(modifier);
            if (compiledModifier == null)
                continue;

            modifierMap().addModifier(compiledModifier);
        }
    }

    private void populateTriggers(String[] triggerStrings) {
        for (String trigger : triggerStrings) {
            Trigger compiledTrigger = TriggerType.compileString(trigger);
            if (compiledTrigger == null)
                continue;

            triggerMap().put(compiledTrigger.type(), compiledTrigger);
        }
    }
}
