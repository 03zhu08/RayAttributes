import React, { useState } from 'react';
import { api } from '../api';

const SLOTS = ['WEAPON', 'HEAD', 'CHEST', 'LEGS', 'FEET'];
const TIERS = ['BLUE', 'PURPLE', 'GOLD'];
const TIER_COLORS: Record<string, string> = { BLUE: '#55f', PURPLE: '#a55', GOLD: '#fa0' };

const SLOT_NAMES: Record<string, string> = {
  WEAPON: '武器', HEAD: '头盔', CHEST: '胸甲', LEGS: '护腿', FEET: '靴子',
};

export default function EquipmentCreator() {
  const [slot, setSlot] = useState('WEAPON');
  const [tier, setTier] = useState('BLUE');
  const [setId, setSetId] = useState('');
  const [mode, setMode] = useState<'create' | 'auto'>('create');
  const [result, setResult] = useState<any>(null);

  const handleCreate = async () => {
    const res = mode === 'create'
      ? await api.createEquipment(slot, tier, setId || undefined)
      : await api.autoGenerate(slot, tier);
    setResult(res.equipment);
  };

  const fmtVal = (type: string, val: number) => {
    const isPct = type.includes('PERCENT') || type === 'CRIT_RATE' || type === 'CRIT_DAMAGE'
      || type === 'SKILL_DAMAGE' || type === 'ENERGY_REGEN' || type === 'COOLDOWN_REDUCTION'
      || type === 'DAMAGE_REDUCTION';
    return isPct ? `${(val * 100).toFixed(1)}%` : val.toFixed(1);
  };

  const attrName = (type: string) => {
    const names: Record<string, string> = {
      ATTACK: '攻击力', ATTACK_PERCENT: '攻击力%', HEALTH: '生命',
      HEALTH_PERCENT: '生命%', DEFENSE: '防御', DEFENSE_PERCENT: '防御%',
      CRIT_RATE: '暴击率', CRIT_DAMAGE: '暴击伤害', SKILL_DAMAGE: '技能增伤',
      ENERGY_REGEN: '能量恢复效率', COOLDOWN_REDUCTION: '冷却缩减', DAMAGE_REDUCTION: '伤害减免',
    };
    return names[type] || type;
  };

  return (
    <div className="panel">
      <h2>装备创建</h2>

      <div className="form-row">
        <label>槽位:</label>
        <select value={slot} onChange={e => setSlot(e.target.value)}>
          {SLOTS.map(s => <option key={s} value={s}>{SLOT_NAMES[s]} ({s})</option>)}
        </select>
      </div>

      <div className="form-row">
        <label>品质:</label>
        <div className="tier-group">
          {TIERS.map(t => (
            <button key={t}
              className={`tier-btn ${tier === t ? 'active' : ''}`}
              style={{ borderColor: tier === t ? TIER_COLORS[t] : '#444', color: TIER_COLORS[t] }}
              onClick={() => setTier(t)}>
              {t}
            </button>
          ))}
        </div>
      </div>

      <div className="form-row">
        <label>生成方式:</label>
        <div className="mode-group">
          <button className={`mode-btn ${mode === 'create' ? 'active' : ''}`}
            onClick={() => setMode('create')}>仅主词条 (Lv.1)</button>
          <button className={`mode-btn ${mode === 'auto' ? 'active' : ''}`}
            onClick={() => setMode('auto')}>自动生成 (含副词条)</button>
        </div>
      </div>

      {mode === 'create' && (
        <div className="form-row">
          <label>套装ID:</label>
          <input value={setId} onChange={e => setSetId(e.target.value)} placeholder="留空无套装" />
        </div>
      )}

      <button className="btn-primary" onClick={handleCreate}>生成装备</button>

      {result && (
        <div className="result-card">
          <div className="eq-header">
            <span className="eq-tier" style={{ color: TIER_COLORS[result.tier] }}>
              {result.tier}
            </span>
            <span className="eq-slot">{SLOT_NAMES[result.slot] || result.slot}</span>
            <span className="eq-level">Lv.{result.level}</span>
          </div>
          {result.setId && <div className="eq-set">套装: {result.setId}</div>}
          <div className="stat-section">
            <div className="stat-label">【主词条】</div>
            <div className="stat-row main">
              <span>{attrName(result.mainType)}</span>
              <span className="stat-val">+{fmtVal(result.mainType, result.mainValue)}</span>
            </div>
          </div>
          {Object.keys(result.subStats).length > 0 && (
            <div className="stat-section">
              <div className="stat-label sub-label">【副词条】</div>
              {Object.entries(result.subStats).map(([type, val]) => (
                <div key={type} className="stat-row sub">
                  <span>{attrName(type)}</span>
                  <span className="stat-val">+{fmtVal(type, val as number)}</span>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
