package me.walcriz.blockbreakspeed.commands;

import me.walcriz.blockbreakspeed.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class AdminCommand implements ICommand {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("[BBS] Wrong usage. Usage: blockbreakspeed <reload|info>");
            return true;
        }

        switch (args[0]) {
            case "reload":
                if (!sender.hasPermission("blockbreakspeed.admin.reload")) {
                    sender.sendMessage("[BBS] You don't have permission to reload the blockbreakspeed config");
                    return true;
                }

                Main.getInstance().reloadConfigs();
                this.messageSender(sender, "[BBS] Successfully reloaded configs!");
                return true;
            case "info":
                sender.sendMessage("[BBS] A semi drop in replacement for Breaker 2 made by Walcriz");
                return true;
            default:
                sender.sendMessage("[BBS] Wrong usage. Usage: blockbreakspeed <reload|info>");
                return true;
        }
    }
}
