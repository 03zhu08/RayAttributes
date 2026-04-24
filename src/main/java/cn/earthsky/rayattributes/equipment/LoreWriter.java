package cn.earthsky.rayattributes.equipment;

import cn.earthsky.rayattributes.attribute.AttributeType;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoreWriter {

    private static final String SEPARATOR = ChatColor.DARK_GRAY + "━━━━━━━━━━━━━━━━━━";

    public static void apply(ItemStack item, EquipmentData data) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        lore.add(data.getTier().getColor() + data.getTier().getDisplayName() + " " +
                 data.getSlot().getDisplayName() +
                 ChatColor.GRAY + " Lv." + data.getLevel());

        if (data.getSetId() != null) {
            lore.add(ChatColor.GREEN + "套装: " + data.getSetId());
        }

        lore.add(SEPARATOR);
        lore.add(ChatColor.YELLOW + "【主词条】");
        lore.add(formatStat(data.getMainStatType(), data.getMainStatValue()));

        if (!data.getSubStats().isEmpty()) {
            lore.add(SEPARATOR);
            lore.add(ChatColor.GRAY + "【副词条】");
            for (Map.Entry<AttributeType, Double> entry : data.getSubStats().entrySet()) {
                lore.add(formatStat(entry.getKey(), entry.getValue()));
            }
        }

        lore.add(SEPARATOR);

        // NBT-like data encoded in hidden lore for parsing
        lore.add(encodeData(data));

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private static String formatStat(AttributeType type, double value) {
        String prefix = ChatColor.GRAY + type.getDisplayName() + ": " + ChatColor.WHITE + "+";
        return prefix + type.formatValue(value);
    }

    static String encodeData(EquipmentData data) {
        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.BLACK).append(ChatColor.MAGIC);
        sb.append("RA|");
        sb.append(data.getSlot().name()).append("|");
        sb.append(data.getTier().name()).append("|");
        sb.append(data.getLevel()).append("|");
        sb.append(data.getMainStatType().name()).append(":").append(data.getMainStatValue()).append("|");
        for (Map.Entry<AttributeType, Double> e : data.getSubStats().entrySet()) {
            sb.append(e.getKey().name()).append(":").append(e.getValue()).append(",");
        }
        sb.append("|");
        sb.append(data.getSetId() != null ? data.getSetId() : "NONE");
        return sb.toString();
    }
}
