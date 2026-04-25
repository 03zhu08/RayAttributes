package cn.earthsky.rayattributes.equipment;

import org.bukkit.ChatColor;

public enum EquipmentTier {
    BLUE("蓝色", 1.0, ChatColor.BLUE),
    PURPLE("紫色", 1.5, ChatColor.DARK_PURPLE),
    GOLD("金色", 2.2, ChatColor.GOLD);

    private final String displayName;
    private final double multiplier;
    private final ChatColor color;

    EquipmentTier(String displayName, double multiplier, ChatColor color) {
        this.displayName = displayName;
        this.multiplier = multiplier;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public ChatColor getColor() {
        return color;
    }
}
