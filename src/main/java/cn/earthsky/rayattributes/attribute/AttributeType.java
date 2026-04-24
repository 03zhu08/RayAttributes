package cn.earthsky.rayattributes.attribute;

public enum AttributeType {
    ATTACK("攻击力", false),
    ATTACK_PERCENT("攻击力%", true),
    HEALTH("生命", false),
    HEALTH_PERCENT("生命%", true),
    DEFENSE("防御", false),
    DEFENSE_PERCENT("防御%", true),
    CRIT_RATE("暴击率", true),
    CRIT_DAMAGE("暴击伤害", true),
    SKILL_DAMAGE("技能增伤", true),
    ENERGY_REGEN("能量恢复效率", true),
    COOLDOWN_REDUCTION("冷却缩减", true),
    DAMAGE_REDUCTION("伤害减免", true);

    private final String displayName;
    private final boolean percentage;

    AttributeType(String displayName, boolean percentage) {
        this.displayName = displayName;
        this.percentage = percentage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isPercentage() {
        return percentage;
    }

    public String formatValue(double value) {
        if (percentage) {
            return String.format("%.1f%%", value * 100);
        }
        return String.valueOf((int) value);
    }
}
