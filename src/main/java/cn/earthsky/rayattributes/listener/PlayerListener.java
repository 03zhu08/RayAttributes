package cn.earthsky.rayattributes.listener;

import cn.earthsky.rayattributes.RayAttributes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final RayAttributes plugin;

    public PlayerListener(RayAttributes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.getAttributeManager().loadPlayer(event.getPlayer());
        plugin.getEnergyManager().loadPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getEnergyManager().unloadPlayer(event.getPlayer());
        plugin.getAttributeManager().unloadPlayer(event.getPlayer());
        plugin.getSetManager().removePlayer(event.getPlayer().getUniqueId());
    }
}
