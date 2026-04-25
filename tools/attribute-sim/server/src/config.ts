import { AttributeType } from './engine/AttributeType';

// ── 插槽主词条池 ──
export const SLOT_MAIN_POOLS: Record<string, AttributeType[]> = {
  WEAPON: [AttributeType.ATTACK],
  HEAD: [AttributeType.HEALTH],
  CHEST: [AttributeType.ATTACK_PERCENT, AttributeType.HEALTH_PERCENT,
    AttributeType.DEFENSE_PERCENT, AttributeType.SKILL_DAMAGE],
  LEGS: [AttributeType.HEALTH_PERCENT, AttributeType.DEFENSE_PERCENT],
  FEET: [AttributeType.ENERGY_REGEN, AttributeType.COOLDOWN_REDUCTION],
};

// ── 插槽副词条池 ──
export const SLOT_SUB_POOLS: Record<string, AttributeType[]> = {
  WEAPON: [AttributeType.ATTACK, AttributeType.ATTACK_PERCENT,
    AttributeType.CRIT_RATE, AttributeType.CRIT_DAMAGE,
    AttributeType.SKILL_DAMAGE, AttributeType.ENERGY_REGEN],
  HEAD: [AttributeType.HEALTH, AttributeType.HEALTH_PERCENT,
    AttributeType.DEFENSE_PERCENT, AttributeType.CRIT_RATE,
    AttributeType.CRIT_DAMAGE],
  CHEST: [AttributeType.HEALTH, AttributeType.DEFENSE,
    AttributeType.CRIT_RATE, AttributeType.CRIT_DAMAGE,
    AttributeType.SKILL_DAMAGE],
  LEGS: [AttributeType.HEALTH, AttributeType.DEFENSE,
    AttributeType.DAMAGE_REDUCTION, AttributeType.ENERGY_REGEN],
  FEET: [AttributeType.CRIT_RATE, AttributeType.CRIT_DAMAGE,
    AttributeType.ENERGY_REGEN],
};

// ── 品质倍率 ──
export const TIER_MULTIPLIER: Record<string, number> = {
  BLUE: 1.0,
  PURPLE: 1.5,
  GOLD: 2.2,
};
export const TIERS = ['BLUE', 'PURPLE', 'GOLD'] as const;
export type Tier = (typeof TIERS)[number];

// ── 主词条最大值 ──
export const MAIN_STAT_MAX: Partial<Record<AttributeType, number>> = {
  [AttributeType.ATTACK]: 120,
  [AttributeType.ATTACK_PERCENT]: 0.12,
  [AttributeType.HEALTH]: 300,
  [AttributeType.HEALTH_PERCENT]: 0.18,
  [AttributeType.DEFENSE_PERCENT]: 0.15,
  [AttributeType.SKILL_DAMAGE]: 0.15,
  [AttributeType.ENERGY_REGEN]: 0.12,
  [AttributeType.COOLDOWN_REDUCTION]: 0.10,
};

// ── 副词条最大值 ──
export const SUB_STAT_MAX: Partial<Record<AttributeType, number>> = {
  [AttributeType.ATTACK]: 8,
  [AttributeType.ATTACK_PERCENT]: 0.03,
  [AttributeType.HEALTH]: 40,
  [AttributeType.HEALTH_PERCENT]: 0.04,
  [AttributeType.DEFENSE]: 10,
  [AttributeType.DEFENSE_PERCENT]: 0.035,
  [AttributeType.CRIT_RATE]: 0.03,
  [AttributeType.CRIT_DAMAGE]: 0.06,
  [AttributeType.SKILL_DAMAGE]: 0.05,
  [AttributeType.ENERGY_REGEN]: 0.04,
  [AttributeType.DAMAGE_REDUCTION]: 0.03,
};

// ── 强化参数 ──
export const MAX_ENHANCE_LEVEL = 10;
export const SUB_STAT_UNLOCK: Record<number, number> = { 3: 3, 4: 5 };  // count -> level
export const SUB_STAT_ENHANCE_LEVELS = [6, 8, 10];
export const SUB_STAT_INITIAL_RATIO = 0.25;
export const SUB_STAT_ENHANCE_RATIO = 0.25;

// ── 玩家基础成长 ──
export const BASE_HEALTH = 100;
export const HEALTH_PER_LEVEL = 20;
export const BASE_ATTACK = 10;
export const ATTACK_PER_LEVEL = 5;
export const BASE_DEFENSE = 5;
export const DEFENSE_PER_LEVEL = 3;

// ── 暴击 ──
export const BASE_CRIT_RATE = 0.05;
export const MAX_CRIT_RATE = 0.75;
export const BASE_CRIT_DAMAGE = 1.5;
export const MAX_CRIT_DAMAGE = 2.5;

// ── 能量 ──
export const MAX_ENERGY = 100;
export const ENERGY_RECOVERY = 10;

// ── 插槽列表 ──
export const SLOTS = Object.keys(SLOT_MAIN_POOLS);
