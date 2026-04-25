import { Router, Request, Response } from 'express';
import { EquipmentEngine } from '../engine/EquipmentEngine';
import { PlayerEngine } from '../engine/PlayerEngine';
import { CombatEngine } from '../engine/CombatEngine';
import { EquipmentData } from '../engine/types';
import {
  SLOTS, TIERS, TIER_MULTIPLIER, SLOT_MAIN_POOLS, SLOT_SUB_POOLS,
  MAIN_STAT_MAX, SUB_STAT_MAX, MAX_ENHANCE_LEVEL,
  BASE_HEALTH, HEALTH_PER_LEVEL, BASE_ATTACK, ATTACK_PER_LEVEL,
  BASE_DEFENSE, DEFENSE_PER_LEVEL, BASE_CRIT_RATE, MAX_CRIT_RATE,
  BASE_CRIT_DAMAGE, MAX_CRIT_DAMAGE, MAX_ENERGY, ENERGY_RECOVERY,
} from '../config';
import { ATTR_META } from '../engine/AttributeType';

export const apiRouter = Router();

// ── 配置 ──
apiRouter.get('/config', (_req: Request, res: Response) => {
  res.json({
    slots: SLOTS,
    tiers: TIERS,
    tierMultiplier: TIER_MULTIPLIER,
    mainStatMax: Object.fromEntries(
      Object.entries(MAIN_STAT_MAX).map(([k, v]) => [k, {
        value: v,
        displayName: ATTR_META[k as keyof typeof ATTR_META]?.displayName || k,
      }])
    ),
    slotMainPools: Object.fromEntries(
      Object.entries(SLOT_MAIN_POOLS).map(([k, v]) => [k, v.map(t => ({
        type: t,
        displayName: ATTR_META[t].displayName,
      }))])
    ),
    slotSubPools: Object.fromEntries(
      Object.entries(SLOT_SUB_POOLS).map(([k, v]) => [k, v.map(t => ({
        type: t,
        displayName: ATTR_META[t].displayName,
      }))])
    ),
    enhancement: {
      maxLevel: MAX_ENHANCE_LEVEL,
      subStatUnlock: { 3: 3, 4: 5 },
      subStatEnhanceLevels: [6, 8, 10],
    },
    playerGrowth: {
      baseHealth: BASE_HEALTH,
      healthPerLevel: HEALTH_PER_LEVEL,
      baseAttack: BASE_ATTACK,
      attackPerLevel: ATTACK_PER_LEVEL,
      baseDefense: BASE_DEFENSE,
      defensePerLevel: DEFENSE_PER_LEVEL,
    },
    crit: {
      baseRate: BASE_CRIT_RATE,
      maxRate: MAX_CRIT_RATE,
      baseDamage: BASE_CRIT_DAMAGE,
      maxDamage: MAX_CRIT_DAMAGE,
    },
    energy: {
      maxEnergy: MAX_ENERGY,
      recovery: ENERGY_RECOVERY,
    },
    attributeMeta: ATTR_META,
  });
});

// ── 装备创建 ──
apiRouter.post('/equipment/create', (req: Request, res: Response) => {
  const { slot, tier, setId } = req.body;
  const data = EquipmentEngine.create(slot, tier, setId || null);
  res.json({ equipment: data });
});

apiRouter.post('/equipment/auto-generate', (req: Request, res: Response) => {
  const { slot, tier } = req.body;
  const data = EquipmentEngine.autoGenerate(slot, tier || 'BLUE');
  res.json({ equipment: data });
});

// ── 强化 ──
apiRouter.post('/equipment/enhance', (req: Request, res: Response) => {
  const { equipment } = req.body as { equipment: EquipmentData };
  const result = EquipmentEngine.enhance(equipment);
  res.json(result);
});

apiRouter.post('/equipment/enhance-to', (req: Request, res: Response) => {
  const { equipment, targetLevel } = req.body as { equipment: EquipmentData; targetLevel: number };
  const result = EquipmentEngine.enhanceTo(equipment, targetLevel);
  res.json(result);
});

// ── 玩家属性计算 ──
apiRouter.post('/player/calculate', (req: Request, res: Response) => {
  const { playerLevel, equipment } = req.body as { playerLevel: number; equipment: EquipmentData[] };
  const attrs = PlayerEngine.calculate(playerLevel, equipment || []);
  res.json({ attributes: attrs });
});

// ── 战斗计算 ──
apiRouter.post('/combat/calculate', (req: Request, res: Response) => {
  const { attacker, skillMultiplier, defender } = req.body as {
    attacker: any; skillMultiplier: number; defender?: any | null;
  };
  const result = CombatEngine.calculate(attacker, skillMultiplier, defender || null);
  res.json({ result });
});

apiRouter.post('/combat/simulate', (req: Request, res: Response) => {
  const { attacker, skillMultiplier, defender, iterations } = req.body as {
    attacker: any; skillMultiplier: number; defender?: any | null; iterations?: number;
  };
  const count = Math.min(iterations || 5, 50);
  const results: any[] = [];
  for (let i = 0; i < count; i++) {
    results.push(CombatEngine.calculate(attacker, skillMultiplier, defender || null));
  }
  res.json({ results });
});

// ── 套装 ──
apiRouter.get('/set-bonuses', (_req: Request, res: Response) => {
  res.json({
    setBonuses: {
      '示例套装': {
        name: '示例套装',
        twoPiece: [
          { type: 'ATTACK_PERCENT', value: 0.08, displayName: '攻击力%' },
          { type: 'CRIT_RATE', value: 0.05, displayName: '暴击率' },
        ],
      },
    },
  });
});
