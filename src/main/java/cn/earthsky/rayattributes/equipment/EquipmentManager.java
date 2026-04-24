package cn.earthsky.rayattributes.equipment;

import cn.earthsky.rayattributes.RayAttributes;
import cn.earthsky.rayattributes.attribute.AttributeType;
import cn.earthsky.rayattributes.config.ConfigManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EquipmentManager {

    private final RayAttributes plugin;

    public EquipmentManager(RayAttributes plugin) {
        this.plugin = plugin;
    }

    public List<EquipmentData> getEquippedItems(Player player) {
        List<EquipmentData> result = new ArrayList<>();
        ItemStack[] armor = player.getInventory().getArmorContents();

        addIfPresent(result, player.getInventory().getItemInMainHand());
        for (ItemStack piece : armor) {
            addIfPresent(result, piece);
        }
        return result;
    }

    private void addIfPresent(List<EquipmentData> list, ItemStack item) {
        if (item != null && item.getType() != Material.AIR) {
            EquipmentData data = LoreParser.parse(item);
            if (data != null) {
                list.add(data);
            }
        }
    }

    public ItemStack createEquipment(EquipmentSlot slot, EquipmentTier tier, int level, String setId) {
        ConfigManager cfg = plugin.getConfigManager();
        List<AttributeType> mainPool = cfg.getSlotMainStats(slot);
        List<AttributeType> subPool = cfg.getSlotSubStats(slot);

        if (mainPool.isEmpty()) return null;

        Random rand = new Random();
        AttributeType mainType = mainPool.get(rand.nextInt(mainPool.size()));

        double maxBase = cfg.getMainStatMax().getOrDefault(mainType, 0.0);
        double mainValue = (maxBase / cfg.getMaxEnhanceLevel()) * level * tier.getMultiplier();

        EquipmentData data = new EquipmentData(slot, tier, level, mainType, mainValue);

        int subCount = 2;
        for (Map.Entry<Integer, Integer> entry : cfg.getSubStatUnlock().entrySet()) {
            if (level >= entry.getValue()) {
                subCount = Math.max(subCount, entry.getKey());
            }
        }

        List<AttributeType> availableSubs = new ArrayList<>(subPool);
        availableSubs.remove(mainType);
        Collections.shuffle(availableSubs, rand);

        for (int i = 0; i < Math.min(subCount, availableSubs.size()); i++) {
            AttributeType subType = availableSubs.get(i);
            double subMax = cfg.getSubStatMax().getOrDefault(subType, 0.0) * tier.getMultiplier();
            double subValue = subMax * cfg.getSubStatInitialRatio();

            int enhanceCount = 0;
            for (int enhLv : cfg.getSubStatEnhanceLevels()) {
                if (level >= enhLv) enhanceCount++;
            }
            subValue += subMax * cfg.getSubStatEnhanceRatio() * Math.min(enhanceCount, 1);

            data.addSubStat(subType, subValue);
        }

        if (setId != null && slot != EquipmentSlot.WEAPON) {
            data.setSetId(setId);
        }

        Material material = getMaterialForSlot(slot);
        ItemStack item = new ItemStack(material);
        LoreWriter.apply(item, data);

        return item;
    }

    private Material getMaterialForSlot(EquipmentSlot slot) {
        switch (slot) {
            case WEAPON: return Material.DIAMOND_SWORD;
            case HEAD: return Material.DIAMOND_HELMET;
            case CHEST: return Material.DIAMOND_CHESTPLATE;
            case LEGS: return Material.DIAMOND_LEGGINGS;
            case FEET: return Material.DIAMOND_BOOTS;
            default: return Material.DIAMOND_SWORD;
        }
    }
}
