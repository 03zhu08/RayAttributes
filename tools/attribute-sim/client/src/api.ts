const BASE = '/api';

async function post<T>(path: string, body?: unknown): Promise<T> {
  const res = await fetch(BASE + path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: body ? JSON.stringify(body) : undefined,
  });
  return res.json();
}

async function get<T>(path: string): Promise<T> {
  const res = await fetch(BASE + path);
  return res.json();
}

export const api = {
  getConfig: () => get<any>('/config'),
  createEquipment: (slot: string, tier: string, setId?: string) =>
    post<any>('/equipment/create', { slot, tier, setId }),
  autoGenerate: (slot: string, tier?: string) =>
    post<any>('/equipment/auto-generate', { slot, tier }),
  enhance: (equipment: any) =>
    post<any>('/equipment/enhance', { equipment }),
  enhanceTo: (equipment: any, targetLevel: number) =>
    post<any>('/equipment/enhance-to', { equipment, targetLevel }),
  calculatePlayer: (playerLevel: number, equipment: any[]) =>
    post<any>('/player/calculate', { playerLevel, equipment }),
  calculateCombat: (attacker: any, skillMultiplier: number, defender?: any) =>
    post<any>('/combat/calculate', { attacker, skillMultiplier, defender }),
  simulateCombat: (attacker: any, skillMultiplier: number, defender?: any, iterations?: number) =>
    post<any>('/combat/simulate', { attacker, skillMultiplier, defender, iterations }),
  getSetBonuses: () => get<any>('/set-bonuses'),
};
