package cn.earthsky.rayattributes.combat;

import cn.earthsky.rayattributes.RayAttributes;
import cn.earthsky.rayattributes.attribute.AttributeType;
import cn.earthsky.rayattributes.attribute.PlayerAttributes;
import cn.earthsky.rayattributes.set.SetBonus;
import cn.earthsky.rayattributes.set.SetEffect;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class CombatListener implements Listener {

    private final RayAttributes plugin;

    public CombatListener(RayAttributes plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player attacker = (Player) event.getDamager();
        PlayerAttributes attackerAttrs = plugin.getAttributeManager().getAttributes(attacker);
        if (attackerAttrs == null) return;

        PlayerAttributes defenderAttrs = null;
        if (event.getEntity() instanceof Player) {
            defenderAttrs = plugin.getAttributeManager().getAttributes((Player) event.getEntity());
        }

        double skillBonus = attackerAttrs.getFinal(AttributeType.SKILL_DAMAGE);
        SetEffect effect = plugin.getSetManager().getActiveEffect(attacker);
        double buffBonus = effect != null ? effect.getActiveBuff() : 0;

        DamageCalculator.DamageResult result = DamageCalculator.calculate(
            attackerAttrs, 1.0, skillBonus + buffBonus, defenderAttrs
        );

        event.setDamage(result.getDamage());

        if (result.isCritical()) {
            attacker.sendMessage(ChatColor.RED + "暴击! " + ChatColor.YELLOW + String.format("%.0f", result.getDamage()));
            handleCritSetEffect(attacker);
        }

        plugin.getEnergyManager().addEnergy(attacker,
            plugin.getConfigManager().getNormalAttackEnergyRecovery());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamageTaken(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        handleGuardianShield(player, event);
    }

    private void handleCritSetEffect(Player player) {
        if (!plugin.getSetManager().hasFourPiece(player, "BERSERKER")) return;
        SetBonus bonus = plugin.getSetManager().getSetBonus("BERSERKER");
        if (bonus == null) return;

        double energyRestore = bonus.getFourPieceEffects().getOrDefault("energy-restore", 0.0);
        plugin.getEnergyManager().addEnergy(player, (int) energyRestore);

        double buffValue = bonus.getFourPieceEffects().getOrDefault("attack-percent-buff.value", 0.0);
        double duration = bonus.getFourPieceEffects().getOrDefault("attack-percent-buff.duration", 100.0);

        SetEffect effect = plugin.getSetManager().getActiveEffect(player);
        if (effect == null) {
            effect = new SetEffect();
            plugin.getSetManager().setActiveEffect(player, effect);
        }
        effect.setBuff(buffValue, (long) (double) duration);
    }

    private void handleGuardianShield(Player player, EntityDamageEvent event) {
        if (!plugin.getSetManager().hasFourPiece(player, "GUARDIAN")) return;
        SetBonus bonus = plugin.getSetManager().getSetBonus("GUARDIAN");
        if (bonus == null) return;

        SetEffect effect = plugin.getSetManager().getActiveEffect(player);
        if (effect == null) {
            effect = new SetEffect();
            plugin.getSetManager().setActiveEffect(player, effect);
        }

        if (effect.getShieldAmount() > 0) {
            double absorbed = Math.min(effect.getShieldAmount(), event.getDamage());
            effect.setShieldAmount(effect.getShieldAmount() - absorbed);
            event.setDamage(event.getDamage() - absorbed);
            return;
        }

        long cooldown = bonus.getFourPieceEffects().getOrDefault("shield-cooldown", 200.0).longValue();
        if (!effect.canTrigger(cooldown)) return;

        PlayerAttributes attrs = plugin.getAttributeManager().getAttributes(player);
        if (attrs == null) return;

        double ratio = bonus.getFourPieceEffects().getOrDefault("shield-ratio", 0.1);
        double shieldAmount = attrs.getFinal(AttributeType.HEALTH) * ratio;
        effect.setShieldAmount(shieldAmount);
        effect.markTriggered();

        player.sendMessage(ChatColor.AQUA + "守护者护盾已激活！" +
            ChatColor.WHITE + String.format(" (%.0f)", shieldAmount));
    }
}
