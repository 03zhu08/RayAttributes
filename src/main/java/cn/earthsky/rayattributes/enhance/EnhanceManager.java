package cn.earthsky.rayattributes.enhance;

import cn.earthsky.rayattributes.RayAttributes;
import cn.earthsky.rayattributes.attribute.AttributeType;
import cn.earthsky.rayattributes.config.ConfigManager;
import cn.earthsky.rayattributes.equipment.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EnhanceManager {

    private final RayAttributes plugin;

    public EnhanceManager(RayAttributes plugin) {
        this.plugin = plugin;
    }

    public boolean enhance(Player player, ItemStack item) {
        EquipmentData data = LoreParser.parse(item);
        if (data == null) return false;

        ConfigManager cfg = plugin.getConfigManager();
        if (data.getLevel() >= cfg.getMaxEnhanceLevel()) {
            player.sendMessage(ChatColor.RED + "装备已达最大强化等级！");
            return false;
        }

        int newLevel = data.getLevel() + 1;
        data.setLevel(newLevel);

        double maxBase = cfg.getMainStatMax().getOrDefault(data.getMainStatType(), 0.0);
        double mainValue = (maxBase / cfg.getMaxEnhanceLevel()) * newLevel * data.getTier().getMultiplier();
        data.setMainStatValue(mainValue);

        for (Map.Entry<Integer, Integer> entry : cfg.getSubStatUnlock().entrySet()) {
            int requiredCount = entry.getKey();
            int requiredLevel = entry.getValue();
            if (newLevel == requiredLevel && data.getSubStats().size() < requiredCount) {
                unlockNewSubStat(data, cfg);
            }
        }

        if (cfg.getSubStatEnhanceLevels().contains(newLevel) && !data.getSubStats().isEmpty()) {
            enhanceRandomSubStat(data, cfg);
        }

        new LoreWriter(plugin.getConfigManager()).apply(item, data);
        plugin.getAttributeManager().recalculate(player);

        player.sendMessage(ChatColor.GREEN + "强化成功！装备等级: " + ChatColor.YELLOW + "Lv." + newLevel);
        return true;
    }

    private void unlockNewSubStat(EquipmentData data, ConfigManager cfg) {
        List<AttributeType> pool = cfg.getSlotSubStats(data.getSlot());
        List<AttributeType> available = new ArrayList<>(pool);
        available.remove(data.getMainStatType());
        available.removeAll(data.getSubStats().keySet());

        if (available.isEmpty()) return;

        Random rand = new Random();
        AttributeType newType = available.get(rand.nextInt(available.size()));
        double maxVal = cfg.getSubStatMax().getOrDefault(newType, 0.0) * data.getTier().getMultiplier();
        data.addSubStat(newType, maxVal * cfg.getSubStatInitialRatio());
    }

    private void enhanceRandomSubStat(EquipmentData data, ConfigManager cfg) {
        List<AttributeType> subKeys = new ArrayList<>(data.getSubStats().keySet());
        if (subKeys.isEmpty()) return;

        Random rand = new Random();
        AttributeType target = subKeys.get(rand.nextInt(subKeys.size()));
        double maxVal = cfg.getSubStatMax().getOrDefault(target, 0.0) * data.getTier().getMultiplier();
        double current = data.getSubStats().get(target);
        data.getSubStats().put(target, current + maxVal * cfg.getSubStatEnhanceRatio());
    }
}
