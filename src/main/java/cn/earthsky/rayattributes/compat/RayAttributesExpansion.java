package cn.earthsky.rayattributes.compat;

import cn.earthsky.rayattributes.RayAttributes;
import cn.earthsky.rayattributes.attribute.AttributeType;
import cn.earthsky.rayattributes.attribute.PlayerAttributes;
import cn.earthsky.rayattributes.storage.PlayerDataDAO;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

/**
 * PlaceholderAPI 拓展 — 暴露 RayAttributes 全部属性变量
 *
 * 变量格式:
 *   %raya_level%               — 玩家等级
 *   %raya_energy%              — 当前能量
 *   %raya_max_energy%          — 最大能量
 *   %raya_<属性名>%             — 属性最终值 (e.g. %raya_attack%)
 *   %raya_<属性名>_flat%       — 属性基础值
 *   %raya_<属性名>_percent%    — 属性百分比值
 *   %raya_<属性名>_final%      — 属性最终值 (显式)
 *
 * 属性名用 AttributeType 枚举名转小写: attack, health, defense,
 *   crit_rate, crit_damage, skill_damage, energy_regen,
 *   cooldown_reduction, damage_reduction, attack_percent,
 *   health_percent, defense_percent
 */
public class RayAttributesExpansion extends PlaceholderExpansion {

    private final RayAttributes plugin;

    public RayAttributesExpansion(RayAttributes plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "raya";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().isEmpty()
                ? "EarthSky" : plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null || params == null || params.isEmpty()) {
            return "";
        }

        String lower = params.toLowerCase();

        // ── 特殊占位符 ──
        if (lower.equals("level")) {
            PlayerDataDAO.PlayerRecord record = plugin.getAttributeManager().getRecord(player);
            return record != null ? String.valueOf(record.getLevel()) : "1";
        }
        if (lower.equals("energy")) {
            return String.valueOf((int) plugin.getEnergyManager().getEnergy(player));
        }
        if (lower.equals("max_energy")) {
            return String.valueOf(plugin.getConfigManager().getMaxEnergy());
        }

        // ── 属性占位符 ──
        PlayerAttributes attrs = plugin.getAttributeManager().getAttributes(player);
        if (attrs == null) return "";

        // 尝试匹配 <type>[_flat|_percent|_final]
        for (AttributeType type : AttributeType.values()) {
            String enumName = type.name().toLowerCase();

            if (lower.equals(enumName) || lower.equals(enumName + "_final")) {
                return formatValue(type, attrs.getFinal(type));
            }
            if (lower.equals(enumName + "_flat")) {
                return formatValue(type, attrs.getFlat(type));
            }
            if (lower.equals(enumName + "_percent")) {
                return formatValue(type, attrs.getPercent(type));
            }
        }

        return "";
    }

    private String formatValue(AttributeType type, double value) {
        if (type.isPercentage()) {
            return String.format("%.1f", value * 100);
        }
        return String.valueOf((int) value);
    }
}
