package cn.earthsky.rayattributes.listener;

import cn.earthsky.rayattributes.RayAttributes;
import cn.earthsky.rayattributes.equipment.LoreParser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class EquipmentListener implements Listener {

    private final RayAttributes plugin;

    public EquipmentListener(RayAttributes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        boolean isArmorSlot = event.getSlotType() == InventoryType.SlotType.ARMOR;
        boolean isShiftClick = event.isShiftClick() && event.getCurrentItem() != null
            && LoreParser.isRayEquipment(event.getCurrentItem());

        if (isArmorSlot || isShiftClick) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getAttributeManager().recalculate(player);
            }, 1L);
        }
    }
}
