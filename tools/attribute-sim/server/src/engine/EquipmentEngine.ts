import { AttributeType } from './AttributeType';
import {
  SLOT_MAIN_POOLS, SLOT_SUB_POOLS, TIER_MULTIPLIER,
  MAIN_STAT_MAX, SUB_STAT_MAX, MAX_ENHANCE_LEVEL,
  SUB_STAT_UNLOCK, SUB_STAT_ENHANCE_LEVELS,
  SUB_STAT_INITIAL_RATIO, SUB_STAT_ENHANCE_RATIO,
} from '../config';
import { EquipmentData } from './types';

export class EquipmentEngine {

  /** 从词条池中随机选取 (排除已选) */
  static randomPick(pool: AttributeType[], exclude: AttributeType[] = []): AttributeType {
    const filtered = pool.filter(t => !exclude.includes(t));
    return filtered[Math.floor(Math.random() * filtered.length)];
  }

  /** 创建装备 (Lv.1, 仅主词条) */
  static create(slot: string, tier: string, setId?: string | null): EquipmentData {
    const pool = SLOT_MAIN_POOLS[slot] || [AttributeType.ATTACK];
    const mainType = pool[Math.floor(Math.random() * pool.length)];
    const mainMax = MAIN_STAT_MAX[mainType] || 0;
    const mainVal = (mainMax / MAX_ENHANCE_LEVEL) * 1 * (TIER_MULTIPLIER[tier] || 1);
    return {
      slot, tier, level: 1,
      mainType, mainValue: mainVal,
      subStats: {}, setId: setId || null,
    };
  }

  /** 完整自动生成 (对应 <RayAttributes-Automatic>) */
  static autoGenerate(slot: string, tier: string = 'BLUE'): EquipmentData {
    const data = EquipmentEngine.create(slot, tier, null);
    const subPool = SLOT_SUB_POOLS[slot] || [];
    const available = subPool.filter(t => t !== data.mainType);

    // Shuffle and pick 2
    for (let i = available.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [available[i], available[j]] = [available[j], available[i]];
    }

    const tierMult = TIER_MULTIPLIER[tier] || 1;
    for (let i = 0; i < Math.min(2, available.length); i++) {
      const st = available[i];
      const smax = (SUB_STAT_MAX[st] || 0) * tierMult;
      data.subStats[st] = smax * SUB_STAT_INITIAL_RATIO;
    }
    return data;
  }

  /** 强化一级 */
  static enhance(data: EquipmentData): { data: EquipmentData; messages: string[] } {
    const output: EquipmentData = JSON.parse(JSON.stringify(data));
    const msgs: string[] = [];

    if (output.level >= MAX_ENHANCE_LEVEL) {
      return { data: output, messages: ['装备已达最大强化等级！'] };
    }

    output.level++;
    msgs.push(`强化成功 → Lv.${output.level}`);

    // 主词条成长
    const mainMax = MAIN_STAT_MAX[output.mainType] || 0;
    output.mainValue = (mainMax / MAX_ENHANCE_LEVEL) * output.level * (TIER_MULTIPLIER[output.tier] || 1);

    // 解锁副词条
    for (const [countStr, reqLevel] of Object.entries(SUB_STAT_UNLOCK)) {
      const count = parseInt(countStr);
      if (output.level === reqLevel && Object.keys(output.subStats).length < count) {
        const subPool = SLOT_SUB_POOLS[output.slot] || [];
        const existing = Object.keys(output.subStats) as AttributeType[];
        const available = subPool.filter(t => t !== output.mainType && !existing.includes(t));
        if (available.length > 0) {
          const newType = available[Math.floor(Math.random() * available.length)];
          const smax = (SUB_STAT_MAX[newType] || 0) * (TIER_MULTIPLIER[output.tier] || 1);
          output.subStats[newType] = smax * SUB_STAT_INITIAL_RATIO;
          msgs.push(`  解锁新副词条!`);
          break;
        }
      }
    }

    // 副词条增强
    if (SUB_STAT_ENHANCE_LEVELS.includes(output.level)) {
      const keys = Object.keys(output.subStats) as AttributeType[];
      if (keys.length > 0) {
        const target = keys[Math.floor(Math.random() * keys.length)];
        const smax = (SUB_STAT_MAX[target] || 0) * (TIER_MULTIPLIER[output.tier] || 1);
        const bonus = smax * SUB_STAT_ENHANCE_RATIO;
        output.subStats[target] = (output.subStats[target] || 0) + bonus;
        msgs.push(`  副词条增强!`);
      }
    }

    return { data: output, messages: msgs };
  }

  /** 强化到目标等级 */
  static enhanceTo(data: EquipmentData, targetLevel: number): { data: EquipmentData; messages: string[] } {
    let current = JSON.parse(JSON.stringify(data)) as EquipmentData;
    const allMsgs: string[] = [];
    const target = Math.min(targetLevel, MAX_ENHANCE_LEVEL);
    while (current.level < target) {
      const result = EquipmentEngine.enhance(current);
      current = result.data;
      allMsgs.push(...result.messages);
    }
    return { data: current, messages: allMsgs };
  }
}
