import { AttributeType } from './AttributeType';
import {
  BASE_HEALTH, HEALTH_PER_LEVEL, BASE_ATTACK, ATTACK_PER_LEVEL,
  BASE_DEFENSE, DEFENSE_PER_LEVEL,
  BASE_CRIT_RATE, MAX_CRIT_RATE, BASE_CRIT_DAMAGE, MAX_CRIT_DAMAGE,
} from '../config';
import { EquipmentData, PlayerAttributesData } from './types';

/** 预设套装加成 (简化版) */
const SET_BONUSES: Record<string, Partial<Record<AttributeType, number>>> = {
  '示例套装': {
    [AttributeType.ATTACK_PERCENT]: 0.08,
    [AttributeType.CRIT_RATE]: 0.05,
  },
};

export class PlayerEngine {

  static calculate(playerLevel: number, equipment: EquipmentData[]): PlayerAttributesData {
    const flat: Partial<Record<AttributeType, number>> = {};
    const pct: Partial<Record<AttributeType, number>> = {};

    // 基础成长
    const addFlat = (t: AttributeType, v: number) => { flat[t] = (flat[t] || 0) + v; };
    const addPct = (t: AttributeType, v: number) => { pct[t] = (pct[t] || 0) + v; };

    addFlat(AttributeType.HEALTH, BASE_HEALTH + HEALTH_PER_LEVEL * playerLevel);
    addFlat(AttributeType.ATTACK, BASE_ATTACK + ATTACK_PER_LEVEL * playerLevel);
    addFlat(AttributeType.DEFENSE, BASE_DEFENSE + DEFENSE_PER_LEVEL * playerLevel);

    // 装备属性
    for (const eq of equipment) {
      const meta = { isPercent: AttributeType.ATTACK }; // placeholder, we use eq.mainType
      if (eq.mainType === AttributeType.ATTACK_PERCENT || eq.mainType === AttributeType.HEALTH_PERCENT ||
        eq.mainType === AttributeType.DEFENSE_PERCENT || eq.mainType === AttributeType.CRIT_RATE ||
        eq.mainType === AttributeType.CRIT_DAMAGE || eq.mainType === AttributeType.SKILL_DAMAGE ||
        eq.mainType === AttributeType.ENERGY_REGEN || eq.mainType === AttributeType.COOLDOWN_REDUCTION ||
        eq.mainType === AttributeType.DAMAGE_REDUCTION) {
        addPct(eq.mainType, eq.mainValue);
      } else {
        addFlat(eq.mainType, eq.mainValue);
      }
      for (const [tStr, v] of Object.entries(eq.subStats)) {
        const t = tStr as AttributeType;
        if (t === AttributeType.ATTACK_PERCENT || t === AttributeType.HEALTH_PERCENT ||
          t === AttributeType.DEFENSE_PERCENT || t === AttributeType.CRIT_RATE ||
          t === AttributeType.CRIT_DAMAGE || t === AttributeType.SKILL_DAMAGE ||
          t === AttributeType.ENERGY_REGEN || t === AttributeType.COOLDOWN_REDUCTION ||
          t === AttributeType.DAMAGE_REDUCTION) {
          addPct(t, v);
        } else {
          addFlat(t, v);
        }
      }
    }

    // 套装加成
    const setCounts: Record<string, number> = {};
    for (const eq of equipment) {
      if (eq.setId) setCounts[eq.setId] = (setCounts[eq.setId] || 0) + 1;
    }
    for (const [sid, cnt] of Object.entries(setCounts)) {
      if (cnt >= 2 && SET_BONUSES[sid]) {
        for (const [tStr, val] of Object.entries(SET_BONUSES[sid])) {
          addPct(tStr as AttributeType, val);
        }
      }
    }

    // 计算最终值
    const final: Partial<Record<AttributeType, number>> = {};
    final[AttributeType.ATTACK] = (flat[AttributeType.ATTACK] || 0) * (1 + (pct[AttributeType.ATTACK_PERCENT] || 0));
    final[AttributeType.HEALTH] = (flat[AttributeType.HEALTH] || 0) * (1 + (pct[AttributeType.HEALTH_PERCENT] || 0));
    final[AttributeType.DEFENSE] = (flat[AttributeType.DEFENSE] || 0) * (1 + (pct[AttributeType.DEFENSE_PERCENT] || 0));
    final[AttributeType.CRIT_RATE] = Math.min(BASE_CRIT_RATE + (pct[AttributeType.CRIT_RATE] || 0), MAX_CRIT_RATE);
    final[AttributeType.CRIT_DAMAGE] = Math.min(BASE_CRIT_DAMAGE + (pct[AttributeType.CRIT_DAMAGE] || 0), MAX_CRIT_DAMAGE);

    for (const t of [AttributeType.SKILL_DAMAGE, AttributeType.ENERGY_REGEN,
    AttributeType.COOLDOWN_REDUCTION, AttributeType.DAMAGE_REDUCTION]) {
      final[t] = pct[t] || 0;
    }

    return { level: playerLevel, flat, pct, final };
  }
}
