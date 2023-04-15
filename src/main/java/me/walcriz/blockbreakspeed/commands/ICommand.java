package me.walcriz.blockbreakspeed.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface ICommand extends CommandExecutor {
    String PREFIX = "&6[&7BlockBreakSpeed&6]&r ";

    default void messageSender(CommandSender sender, String msg) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + msg));
    }
}
