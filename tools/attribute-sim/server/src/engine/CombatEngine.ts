import { AttributeType } from './AttributeType';
import { PlayerAttributesData, CombatResult } from './types';

export class CombatEngine {

  static calculate(
    attacker: PlayerAttributesData,
    skillMultiplier: number,
    defender?: PlayerAttributesData | null,
  ): CombatResult {
    const atk = attacker.final[AttributeType.ATTACK] || 0;
    const skillDmg = atk * skillMultiplier * (1 + (attacker.final[AttributeType.SKILL_DAMAGE] || 0));

    const isCrit = Math.random() < (attacker.final[AttributeType.CRIT_RATE] || 0);
    const critMult = isCrit ? (attacker.final[AttributeType.CRIT_DAMAGE] || 1.5) : 1.0;

    let defenseReduction = 0;
    let dmgReduction = 0;
    if (defender) {
      const def = defender.final[AttributeType.DEFENSE] || 0;
      defenseReduction = def / (def + 100);
      dmgReduction = defender.final[AttributeType.DAMAGE_REDUCTION] || 0;
    }
    const totalReduction = Math.min(defenseReduction + dmgReduction, 0.9);

    const finalDamage = Math.max(skillDmg * critMult * (1 - totalReduction), 1);

    return {
      rawSkill: Math.round(skillDmg * 100) / 100,
      isCrit,
      critMult: Math.round(critMult * 100) / 100,
      defenseReduction: Math.round(defenseReduction * 1000) / 10,
      dmgReduction: Math.round(dmgReduction * 1000) / 10,
      totalReduction: Math.round(totalReduction * 1000) / 10,
      finalDamage: Math.round(finalDamage * 10) / 10,
    };
  }
}
