package cn.earthsky.rayattributes.equipment;

import cn.earthsky.rayattributes.attribute.AttributeType;
import cn.earthsky.rayattributes.config.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoreWriter {

    private final ConfigManager cfg;

    public LoreWriter(ConfigManager cfg) {
        this.cfg = cfg;
    }

    public void apply(ItemStack item, EquipmentData data) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();

        // 品质名 槽位名 Lv.等级
        lore.add(cfg.getTierColor(data.getTier().name()) + data.getTier().getDisplayName() + " "
                + ChatColor.RESET + cfg.getStatLabelColor() + data.getSlot().getDisplayName()
                + cfg.getLevelPrefix() + "Lv." + cfg.getLevelColor() + data.getLevel());

        // 套装行
        if (data.getSetId() != null) {
            lore.add(cfg.getSetLabelColor() + "套装: " + cfg.getSetValueColor() + data.getSetId());
        }

        // 分隔线
        lore.add(cfg.getSeparator());

        // 主词条
        lore.add(cfg.getHeaderColor("primary") + "【主词条】");
        lore.add(formatStat(data.getMainStatType(), data.getMainStatValue()));

        // 副词条
        if (!data.getSubStats().isEmpty()) {
            lore.add(cfg.getSeparator());
            lore.add(cfg.getHeaderColor("secondary") + "【副词条】");
            for (Map.Entry<AttributeType, Double> entry : data.getSubStats().entrySet()) {
                lore.add(formatStat(entry.getKey(), entry.getValue()));
            }
        }

        // 底部分隔 + 类型行
        lore.add(cfg.getSeparator());
        lore.add(cfg.getFooterColor() + cfg.getWeaponTypeLabel());

        // 隐藏编码数据 (用于解析)
        lore.add(encodeData(data));

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private String formatStat(AttributeType type, double value) {
        return cfg.getStatLabelColor() + type.getDisplayName() + ": "
                + cfg.getStatValueColor(type) + "+" + type.formatValue(value);
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
