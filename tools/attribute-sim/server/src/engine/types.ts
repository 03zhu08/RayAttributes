import { AttributeType } from './AttributeType';

export interface EquipmentData {
  slot: string;
  tier: string;
  level: number;
  mainType: AttributeType;
  mainValue: number;
  subStats: Partial<Record<AttributeType, number>>;
  setId: string | null;
}

export interface PlayerAttributesData {
  level: number;
  flat: Partial<Record<AttributeType, number>>;
  pct: Partial<Record<AttributeType, number>>;
  final: Partial<Record<AttributeType, number>>;
}

export interface CombatResult {
  rawSkill: number;
  isCrit: boolean;
  critMult: number;
  defenseReduction: number;
  dmgReduction: number;
  totalReduction: number;
  finalDamage: number;
}

export interface SetBonusData {
  id: string;
  name: string;
  twoPiece: Array<{ type: AttributeType; value: number }>;
}
