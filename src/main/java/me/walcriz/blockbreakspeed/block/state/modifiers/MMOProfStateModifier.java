package me.walcriz.blockbreakspeed.block.state.modifiers;

import me.walcriz.blockbreakspeed.Main;
import me.walcriz.blockbreakspeed.block.state.IStateModifier;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.MMOCoreAPI;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.Profession;
import net.Indyuce.mmocore.manager.profession.ProfessionManager;
import org.bukkit.entity.Player;

public record MMOProfStateModifier(int value, String name, int level) implements IStateModifier {
    @Override
    public int getModifierValueForPlayer(Player player) {
        ProfessionManager professionManager = MMOCore.plugin.professionManager;
        Profession profession = professionManager.get(name());
        if (profession == null)
            return 0;

        MMOCoreAPI api = Main.getMMOCoreAPI();
        PlayerData data = api.getPlayerData(player);
        var skills = data.getCollectionSkills();

        if (skills.getExperience(profession) >= level())
            return value();

        return 0;
    }
}
