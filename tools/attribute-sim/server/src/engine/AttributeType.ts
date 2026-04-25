/** 属性类型 — 与插件 AttributeType.java 对齐 */
export enum AttributeType {
  ATTACK = 'ATTACK',
  ATTACK_PERCENT = 'ATTACK_PERCENT',
  HEALTH = 'HEALTH',
  HEALTH_PERCENT = 'HEALTH_PERCENT',
  DEFENSE = 'DEFENSE',
  DEFENSE_PERCENT = 'DEFENSE_PERCENT',
  CRIT_RATE = 'CRIT_RATE',
  CRIT_DAMAGE = 'CRIT_DAMAGE',
  SKILL_DAMAGE = 'SKILL_DAMAGE',
  ENERGY_REGEN = 'ENERGY_REGEN',
  COOLDOWN_REDUCTION = 'COOLDOWN_REDUCTION',
  DAMAGE_REDUCTION = 'DAMAGE_REDUCTION',
}

export const ATTR_META: Record<AttributeType, { displayName: string; isPercent: boolean }> = {
  [AttributeType.ATTACK]: { displayName: '攻击力', isPercent: false },
  [AttributeType.ATTACK_PERCENT]: { displayName: '攻击力%', isPercent: true },
  [AttributeType.HEALTH]: { displayName: '生命', isPercent: false },
  [AttributeType.HEALTH_PERCENT]: { displayName: '生命%', isPercent: true },
  [AttributeType.DEFENSE]: { displayName: '防御', isPercent: false },
  [AttributeType.DEFENSE_PERCENT]: { displayName: '防御%', isPercent: true },
  [AttributeType.CRIT_RATE]: { displayName: '暴击率', isPercent: true },
  [AttributeType.CRIT_DAMAGE]: { displayName: '暴击伤害', isPercent: true },
  [AttributeType.SKILL_DAMAGE]: { displayName: '技能增伤', isPercent: true },
  [AttributeType.ENERGY_REGEN]: { displayName: '能量恢复效率', isPercent: true },
  [AttributeType.COOLDOWN_REDUCTION]: { displayName: '冷却缩减', isPercent: true },
  [AttributeType.DAMAGE_REDUCTION]: { displayName: '伤害减免', isPercent: true },
};

export function fmtAttr(type: AttributeType, value: number): string {
  const meta = ATTR_META[type];
  if (meta.isPercent) return (value * 100).toFixed(1) + '%';
  return value.toFixed(0);
}

export function fmtAttrShort(type: AttributeType, value: number): string {
  const meta = ATTR_META[type];
  if (meta.isPercent) return (value * 100).toFixed(1) + '%';
  return value.toFixed(1);
}
