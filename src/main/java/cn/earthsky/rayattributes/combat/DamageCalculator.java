package cn.earthsky.rayattributes.combat;

import cn.earthsky.rayattributes.attribute.AttributeType;
import cn.earthsky.rayattributes.attribute.PlayerAttributes;

import java.util.Random;

public class DamageCalculator {

    private static final Random RANDOM = new Random();

    public static DamageResult calculate(PlayerAttributes attacker, double skillMultiplier,
                                         double skillDamageBonus, PlayerAttributes defender) {
        double finalAttack = attacker.getFinal(AttributeType.ATTACK);
        double skillDamage = finalAttack * skillMultiplier * (1 + skillDamageBonus);

        boolean isCrit = RANDOM.nextDouble() < attacker.getFinal(AttributeType.CRIT_RATE);
        double critMultiplier = isCrit ? attacker.getFinal(AttributeType.CRIT_DAMAGE) : 1.0;

        double defenseReduction = 0;
        if (defender != null) {
            double defense = defender.getFinal(AttributeType.DEFENSE);
            defenseReduction = defense / (defense + 100);
        }

        double damageReduction = defender != null ? defender.getFinal(AttributeType.DAMAGE_REDUCTION) : 0;
        double totalReduction = Math.min(defenseReduction + damageReduction, 0.9);

        double finalDamage = skillDamage * critMultiplier * (1 - totalReduction);
        return new DamageResult(Math.max(finalDamage, 1), isCrit);
    }

    public static class DamageResult {
        private final double damage;
        private final boolean critical;

        public DamageResult(double damage, boolean critical) {
            this.damage = damage;
            this.critical = critical;
        }

        public double getDamage() { return damage; }
        public boolean isCritical() { return critical; }
    }
}
