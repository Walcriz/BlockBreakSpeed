package me.walcriz.blockbreakspeed.block;

import me.walcriz.blockbreakspeed.block.state.StateManager;
import me.walcriz.blockbreakspeed.block.state.StateModifierMap;
import me.walcriz.blockbreakspeed.block.state.IStateModifier;
import me.walcriz.blockbreakspeed.block.trigger.Trigger;
import me.walcriz.blockbreakspeed.block.trigger.TriggerMap;
import me.walcriz.blockbreakspeed.block.trigger.TriggerType;

public record BlockInfo(StateModifierMap modifierMap, TriggerMap triggerMap) {
    public void populateInfo(String[] modifierStrings, String[] triggerStrings) {
        populateModifiers(modifierStrings);
        populateTriggers(triggerStrings);
    }

    private void populateModifiers(String[] modifierStrings) {
        var manager = StateManager.getInstance();
        for (String modifier : modifierStrings) {

            IStateModifier compiledModifier = manager.compileString(modifier);
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
