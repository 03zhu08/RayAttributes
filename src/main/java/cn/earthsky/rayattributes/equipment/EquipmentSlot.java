package cn.earthsky.rayattributes.equipment;

public enum EquipmentSlot {
    WEAPON("武器", 0),
    HEAD("头部", 5),
    CHEST("躯干", 6),
    LEGS("护腿", 7),
    FEET("脚部", 8);

    private final String displayName;
    private final int armorSlot;

    EquipmentSlot(String displayName, int armorSlot) {
        this.displayName = displayName;
        this.armorSlot = armorSlot;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getArmorSlot() {
        return armorSlot;
    }
}
