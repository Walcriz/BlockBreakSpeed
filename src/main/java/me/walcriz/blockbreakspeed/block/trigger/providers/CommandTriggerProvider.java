package me.walcriz.blockbreakspeed.block.trigger.providers;

import me.walcriz.blockbreakspeed.Main;
import me.walcriz.blockbreakspeed.block.trigger.ITriggerProvider;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandTriggerProvider extends ITriggerProvider {
    public CommandTriggerProvider(String value) {
        super(value);
    }

    @Override
    public void doAction(Player player, Block block) {
        String command = getValue();

        command = command.replaceAll("%player%", player.getName());
        command = command.replaceAll("%uuid%", player.getUniqueId().toString());
        command = command.replaceAll("%locx%", String.valueOf(block.getLocation().getBlockX()));
        command = command.replaceAll("%locy%", String.valueOf(block.getLocation().getBlockY()));
        command = command.replaceAll("%locz%", String.valueOf(block.getLocation().getBlockZ()));

        if (command.startsWith("!")) { // Execute as console
            command = command.substring(1);
            Server server = Bukkit.getServer();
            ConsoleCommandSender sender = server.getConsoleSender();
            Bukkit.getServer().dispatchCommand(sender, command);
            return;
        }

        player.performCommand(command);
    }
}
