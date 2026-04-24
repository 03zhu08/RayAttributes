package cn.earthsky.rayattributes.equipment;

import cn.earthsky.rayattributes.attribute.AttributeType;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class LoreParser {

    private static final String PREFIX = ChatColor.BLACK.toString() + ChatColor.MAGIC + "RA|";

    public static EquipmentData parse(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return null;

        List<String> lore = meta.getLore();
        for (String line : lore) {
            if (line.contains("RA|")) {
                return decode(line);
            }
        }
        return null;
    }

    public static boolean isRayEquipment(ItemStack item) {
        return parse(item) != null;
    }

    private static EquipmentData decode(String encoded) {
        try {
            String raw = ChatColor.stripColor(encoded);
            String[] parts = raw.split("\\|");
            if (parts.length < 6) return null;

            int idx = 0;
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("RA")) { idx = i + 1; break; }
            }

            EquipmentSlot slot = EquipmentSlot.valueOf(parts[idx]);
            EquipmentTier tier = EquipmentTier.valueOf(parts[idx + 1]);
            int level = Integer.parseInt(parts[idx + 2]);

            String[] mainParts = parts[idx + 3].split(":");
            AttributeType mainType = AttributeType.valueOf(mainParts[0]);
            double mainValue = Double.parseDouble(mainParts[1]);

            EquipmentData data = new EquipmentData(slot, tier, level, mainType, mainValue);

            String subStr = parts[idx + 4];
            if (!subStr.isEmpty()) {
                String[] subs = subStr.split(",");
                for (String sub : subs) {
                    if (sub.isEmpty()) continue;
                    String[] kv = sub.split(":");
                    data.addSubStat(AttributeType.valueOf(kv[0]), Double.parseDouble(kv[1]));
                }
            }

            String setId = parts[idx + 5];
            if (!"NONE".equals(setId)) {
                data.setSetId(setId);
            }

            return data;
        } catch (Exception e) {
            return null;
        }
    }
}
