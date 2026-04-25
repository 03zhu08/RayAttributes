package cn.earthsky.rayattributes;

import cn.earthsky.rayattributes.attribute.AttributeManager;
import cn.earthsky.rayattributes.combat.CombatListener;
import cn.earthsky.rayattributes.command.AdminCommand;
import cn.earthsky.rayattributes.compat.RayAttributesExpansion;
import cn.earthsky.rayattributes.config.ConfigManager;
import cn.earthsky.rayattributes.energy.EnergyManager;
import cn.earthsky.rayattributes.enhance.EnhanceManager;
import cn.earthsky.rayattributes.equipment.EquipmentManager;
import cn.earthsky.rayattributes.listener.EquipmentListener;
import cn.earthsky.rayattributes.listener.PlayerListener;
import cn.earthsky.rayattributes.set.SetManager;
import cn.earthsky.rayattributes.storage.DatabaseManager;
import cn.earthsky.rayattributes.storage.PlayerDataDAO;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class RayAttributes extends JavaPlugin {

    private static RayAttributes instance;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private PlayerDataDAO playerDataDAO;
    private EquipmentManager equipmentManager;
    private AttributeManager attributeManager;
    private EnhanceManager enhanceManager;
    private SetManager setManager;
    private EnergyManager energyManager;

    @Override
    public void onEnable() {
        instance = this;

        configManager = new ConfigManager(this);
        configManager.loadAll();

        databaseManager = new DatabaseManager();
        databaseManager.init(this);

        playerDataDAO = new PlayerDataDAO(databaseManager, this);
        equipmentManager = new EquipmentManager(this);
        setManager = new SetManager(this);
        attributeManager = new AttributeManager(this);
        enhanceManager = new EnhanceManager(this);
        energyManager = new EnergyManager(this);

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new EquipmentListener(this), this);
        getServer().getPluginManager().registerEvents(new CombatListener(this), this);

        getCommand("raya").setExecutor(new AdminCommand(this));

        // 注册 PlaceholderAPI 拓展 (如果可用)
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new RayAttributesExpansion(this).register();
            getLogger().info("PlaceholderAPI 拓展已注册");
        }

        getLogger().info("RayAttributes 已启用");
    }

    @Override
    public void onDisable() {
        if (energyManager != null) energyManager.shutdown();
        if (attributeManager != null) attributeManager.saveAll();
        if (databaseManager != null) databaseManager.close();
        getLogger().info("RayAttributes 已关闭");
    }

    public static RayAttributes getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public PlayerDataDAO getPlayerDataDAO() { return playerDataDAO; }
    public EquipmentManager getEquipmentManager() { return equipmentManager; }
    public AttributeManager getAttributeManager() { return attributeManager; }
    public EnhanceManager getEnhanceManager() { return enhanceManager; }
    public SetManager getSetManager() { return setManager; }
    public EnergyManager getEnergyManager() { return energyManager; }
}
