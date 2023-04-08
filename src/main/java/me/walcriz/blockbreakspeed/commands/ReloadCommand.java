package me.walcriz.blockbreakspeed.commands;

import me.walcriz.blockbreakspeed.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("blockbreakspeed.config.reload")) {
            sender.sendMessage("You don't have permission to reload the blockbreakspeed config");
            return true;
        }

        Main.getInstance().reloadBlockConfigs();

        return true;
    }
}
