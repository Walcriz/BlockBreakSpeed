package me.walcriz.blockbreakspeed.commands;

import me.walcriz.blockbreakspeed.Main;
import me.walcriz.blockbreakspeed.utils.StringHelpers;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AdminCommand implements ICommand {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player)){
            StringHelpers.consoleMsg("Please run this as a Player with permission.", Main.consoleTypes.INFO);
        }

        String usage = "Wrong usage. Usage: blockbreakspeed <reload|info>";

        Player player = (Player) sender;
        if (args.length == 0) {
            StringHelpers.playerMsg(player,usage);
            return true;
        }

        switch (args[0]) {
            case "reload":
                if (!sender.hasPermission("blockbreakspeed.admin.reload")) {
                    StringHelpers.playerMsg(player, "You don't have permission to reload the blockbreakspeed config");
                    return true;
                }

                Main.getInstance().reloadConfigs();
                StringHelpers.playerMsg(player, "Successfully reloaded configs!");
                return true;
            case "info":
                StringHelpers.playerMsg(player,"A semi drop in replacement for Breaker 2 made by Walcriz");
                return true;
            default:
                StringHelpers.playerMsg(player,usage);
                return true;
        }
    }
}
