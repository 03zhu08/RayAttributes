package cn.earthsky.rayattributes.command;

import cn.earthsky.rayattributes.RayAttributes;
import cn.earthsky.rayattributes.attribute.AttributeType;
import cn.earthsky.rayattributes.attribute.PlayerAttributes;
import cn.earthsky.rayattributes.equipment.EquipmentSlot;
import cn.earthsky.rayattributes.equipment.EquipmentTier;
import cn.earthsky.rayattributes.storage.PlayerDataDAO;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AdminCommand implements CommandExecutor {

    private final RayAttributes plugin;

    public AdminCommand(RayAttributes plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "give": return handleGive(sender, args);
            case "info": return handleInfo(sender, args);
            case "enhance": return handleEnhance(sender);
            case "setlevel": return handleSetLevel(sender, args);
            case "reload": return handleReload(sender);
            default:
                sendHelp(sender);
                return true;
        }
    }

    // PLACEHOLDER_CMD_1

    private boolean handleGive(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "用法: /raya give <玩家> <槽位> <品质> [套装ID]");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "玩家不在线");
            return true;
        }

        EquipmentSlot slot;
        EquipmentTier tier;
        try {
            slot = EquipmentSlot.valueOf(args[2].toUpperCase());
            tier = EquipmentTier.valueOf(args[3].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "无效的槽位或品质");
            return true;
        }

        // 创建时固定 1 级，强化需手动
        String setId = args.length > 4 ? args[4].toUpperCase() : null;

        ItemStack item = plugin.getEquipmentManager().createEquipment(slot, tier, 1, setId);
        if (item != null) {
            target.getInventory().addItem(item);
            sender.sendMessage(ChatColor.GREEN + "已给予 " + target.getName() + " 一件 " +
                tier.getDisplayName() + " " + slot.getDisplayName());
        }
        return true;
    }

    private boolean handleInfo(CommandSender sender, String[] args) {
        Player target;
        if (args.length > 1) {
            target = Bukkit.getPlayer(args[1]);
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "请指定玩家");
            return true;
        }

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "玩家不在线");
            return true;
        }

        PlayerAttributes attrs = plugin.getAttributeManager().getAttributes(target);
        PlayerDataDAO.PlayerRecord record = plugin.getAttributeManager().getRecord(target);
        if (attrs == null || record == null) {
            sender.sendMessage(ChatColor.RED + "数据未加载");
            return true;
        }

        sender.sendMessage(ChatColor.GOLD + "=== " + target.getName() + " 属性面板 ===");
        sender.sendMessage(ChatColor.GRAY + "等级: " + ChatColor.WHITE + record.getLevel());
        for (AttributeType type : AttributeType.values()) {
            double val = attrs.getFinal(type);
            if (val != 0) {
                sender.sendMessage(ChatColor.GRAY + type.getDisplayName() + ": " +
                    ChatColor.WHITE + type.formatValue(val));
            }
        }
        sender.sendMessage(ChatColor.GRAY + "能量: " + ChatColor.AQUA +
            String.format("%.0f", plugin.getEnergyManager().getEnergy(target)) +
            "/" + plugin.getConfigManager().getMaxEnergy());
        return true;
    }

    private boolean handleEnhance(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "仅玩家可用");
            return true;
        }
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        plugin.getEnhanceManager().enhance(player, item);
        return true;
    }

    private boolean handleSetLevel(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "用法: /raya setlevel <玩家> <等级>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "玩家不在线");
            return true;
        }
        int level = Integer.parseInt(args[2]);
        plugin.getAttributeManager().setLevel(target, level);
        sender.sendMessage(ChatColor.GREEN + target.getName() + " 等级已设为 " + level);
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        plugin.getConfigManager().loadAll();
        sender.sendMessage(ChatColor.GREEN + "配置已重载");
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== RayAttributes ===");
        sender.sendMessage(ChatColor.YELLOW + "/raya give <玩家> <槽位> <品质> [套装ID]");
        sender.sendMessage(ChatColor.YELLOW + "/raya info [玩家]");
        sender.sendMessage(ChatColor.YELLOW + "/raya enhance" + ChatColor.GRAY + " - 强化手持装备");
        sender.sendMessage(ChatColor.YELLOW + "/raya setlevel <玩家> <等级>");
        sender.sendMessage(ChatColor.YELLOW + "/raya reload");
    }
}
