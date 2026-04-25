package cn.earthsky.rayattributes.energy;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class EnergyBar {

    private static final int BAR_LENGTH = 20;

    public static void display(Player player, double energy, int max) {
        int filled = (int) (energy / max * BAR_LENGTH);
        StringBuilder bar = new StringBuilder();
        bar.append(ChatColor.AQUA).append("能量 ");
        for (int i = 0; i < BAR_LENGTH; i++) {
            if (i < filled) {
                bar.append(ChatColor.AQUA).append("|");
            } else {
                bar.append(ChatColor.DARK_GRAY).append("|");
            }
        }
        bar.append(ChatColor.WHITE).append(" ").append((int) energy).append("/").append(max);

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(bar.toString()));
    }
}
