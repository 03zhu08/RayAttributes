package cn.earthsky.rayattributes.equipment;

import cn.earthsky.rayattributes.RayAttributes;
import cn.earthsky.rayattributes.attribute.AttributeType;
import cn.earthsky.rayattributes.config.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

    /**
     * 创建一个基础装备，仅含主词条，强化等级固定为 1。
     * 副词条不会自动生成 —— 只能通过 /raya enhance 手动添加。
     */
    public ItemStack createEquipment(EquipmentSlot slot, EquipmentTier tier, int level, String setId) {
        ConfigManager cfg = plugin.getConfigManager();
        List<AttributeType> mainPool = cfg.getSlotMainStats(slot);
        if (mainPool.isEmpty()) return null;

        Random rand = new Random();
        AttributeType mainType = mainPool.get(rand.nextInt(mainPool.size()));

        // 首次创建固定为 1 级，强化需手动
        double maxBase = cfg.getMainStatMax().getOrDefault(mainType, 0.0);
        double mainValue = (maxBase / cfg.getMaxEnhanceLevel()) * 1 * tier.getMultiplier();

        EquipmentData data = new EquipmentData(slot, tier, 1, mainType, mainValue);

        if (setId != null && slot != EquipmentSlot.WEAPON) {
            data.setSetId(setId);
        }

        Material material = getMaterialForSlot(slot);
        ItemStack item = new ItemStack(material);
        new LoreWriter(plugin.getConfigManager()).apply(item, data);

        return item;
    }

    /**
     * 若物品 Lore 中包含 &lt;RayAttributes-Automatic&gt; 标记，
     * 则自动生成主词条 + 随机副词条（均为 1 级），并覆写 Lore。
     * 否则不做任何处理，原物返回。
     * <p>
     * 此方法供外部插件（如 FantasyWeapon）调用的公开 API。
     */
    public ItemStack autoGenerate(ItemStack item) {
        if (!containsAutoTag(item)) return item;

        EquipmentSlot slot = detectSlot(item);
        EquipmentTier tier = parseTierFromTag(item);

        ConfigManager cfg = plugin.getConfigManager();
        List<AttributeType> mainPool = cfg.getSlotMainStats(slot);
        if (mainPool.isEmpty()) return item;

        Random rand = new Random();
        AttributeType mainType = mainPool.get(rand.nextInt(mainPool.size()));

        double maxBase = cfg.getMainStatMax().getOrDefault(mainType, 0.0);
        double mainValue = (maxBase / cfg.getMaxEnhanceLevel()) * 1 * tier.getMultiplier();

        EquipmentData data = new EquipmentData(slot, tier, 1, mainType, mainValue);

        // 自动生成 2 个基础副词条
        List<AttributeType> subPool = cfg.getSlotSubStats(slot);
        List<AttributeType> availableSubs = new ArrayList<>(subPool);
        availableSubs.remove(mainType);
        Collections.shuffle(availableSubs, rand);

        int subCount = Math.min(2, availableSubs.size());
        for (int i = 0; i < subCount; i++) {
            AttributeType subType = availableSubs.get(i);
            double subMax = cfg.getSubStatMax().getOrDefault(subType, 0.0) * tier.getMultiplier();
            double subValue = subMax * cfg.getSubStatInitialRatio();
            data.addSubStat(subType, subValue);
        }

        new LoreWriter(plugin.getConfigManager()).apply(item, data);
        return item;
    }

    private boolean containsAutoTag(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return false;
        for (String line : meta.getLore()) {
            if (ChatColor.stripColor(line).contains("<RayAttributes-Automatic>")) {
                return true;
            }
        }
        return false;
    }

    private EquipmentTier parseTierFromTag(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return EquipmentTier.BLUE;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return EquipmentTier.BLUE;
        for (String line : meta.getLore()) {
            String clean = ChatColor.stripColor(line);
            int idx = clean.indexOf("<RayAttributes-Automatic");
            if (idx == -1) continue;
            String rest = clean.substring(idx + "<RayAttributes-Automatic".length());
            // Support <RayAttributes-Automatic:TIER> or <RayAttributes-Automatic>
            if (rest.startsWith(":")) {
                int end = rest.indexOf(">");
                if (end > 1) {
                    String tierStr = rest.substring(1, end).toUpperCase().trim();
                    try {
                        return EquipmentTier.valueOf(tierStr);
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
            break;
        }
        return EquipmentTier.BLUE;
    }

    private EquipmentSlot detectSlot(ItemStack item) {
        switch (item.getType()) {
            case DIAMOND_HELMET:
            case IRON_HELMET:
            case CHAINMAIL_HELMET:
            case GOLD_HELMET:
            case LEATHER_HELMET:
                return EquipmentSlot.HEAD;
            case DIAMOND_CHESTPLATE:
            case IRON_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
            case GOLD_CHESTPLATE:
            case LEATHER_CHESTPLATE:
                return EquipmentSlot.CHEST;
            case DIAMOND_LEGGINGS:
            case IRON_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
            case GOLD_LEGGINGS:
            case LEATHER_LEGGINGS:
                return EquipmentSlot.LEGS;
            case DIAMOND_BOOTS:
            case IRON_BOOTS:
            case CHAINMAIL_BOOTS:
            case GOLD_BOOTS:
            case LEATHER_BOOTS:
                return EquipmentSlot.FEET;
            default:
                return EquipmentSlot.WEAPON;
        }
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
