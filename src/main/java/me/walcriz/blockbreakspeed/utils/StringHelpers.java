package me.walcriz.blockbreakspeed.utils;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import me.walcriz.blockbreakspeed.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelpers {

    public static String parseString(String input){

        return IridiumColorAPI.process(input);
    }

    public static Component parseComponent(String input, Player player){
        return LegacyComponentSerializer.legacy('§').deserialize(parseString(input));
    }



    public static void playerMsg(Player player, String message) {
        player.sendMessage(StringHelpers.parseString(Main.getInstance().getConfig().getString("prefix") + "&f " + message));
    }
    public void playerClean(Player player, String message) {
        player.sendMessage(StringHelpers.parseString(message));
    }

    public static void consoleMsg(String message, Main.consoleTypes type){
        if(type == Main.consoleTypes.INFO){
            Main.getInstance().getLogger().info(StringHelpers.parseString(message));
        } else if (type == Main.consoleTypes.WARN) {
            Main.getInstance().getLogger().warning(StringHelpers.parseString(message));
        } else if (type == Main.consoleTypes.SEVERE) {
            Main.getInstance().getLogger().severe(StringHelpers.parseString(message));
        }
    }

    public static void debugPlayerMsg(Player p, String message) {
        if (Main.getConfiguration().debugLogging) {
            p.sendMessage(StringHelpers.parseString("&c&lDEBUG &4&l>> &c" + message));
        }
    }

    public static void debugConsoleMsg(String message) {
        if (Main.getConfiguration().debugLogging) {
            Main.getInstance().getLogger().warning("DEBUG >> " + message);
        }
    }
}
