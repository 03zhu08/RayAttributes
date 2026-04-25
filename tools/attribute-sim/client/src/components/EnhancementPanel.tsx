import React, { useState } from 'react';
import { api } from '../api';

const TIER_COLORS: Record<string, string> = { BLUE: '#55f', PURPLE: '#a55', GOLD: '#fa0' };

export default function EnhancementPanel() {
  const [equipment, setEquipment] = useState<any>(null);
  const [slot, setSlot] = useState('WEAPON');
  const [tier, setTier] = useState('GOLD');
  const [logs, setLogs] = useState<string[]>([]);
  const [targetLevel, setTargetLevel] = useState(10);

  const attrName = (type: string) => {
    const names: Record<string, string> = {
      ATTACK: '攻击力', ATTACK_PERCENT: '攻击力%', HEALTH: '生命',
      HEALTH_PERCENT: '生命%', DEFENSE: '防御', DEFENSE_PERCENT: '防御%',
      CRIT_RATE: '暴击率', CRIT_DAMAGE: '暴击伤害', SKILL_DAMAGE: '技能增伤',
      ENERGY_REGEN: '能量恢复效率', COOLDOWN_REDUCTION: '冷却缩减', DAMAGE_REDUCTION: '伤害减免',
    };
    return names[type] || type;
  };

  const fmtVal = (type: string, val: number) => {
    const isPct = type.includes('PERCENT') || type === 'CRIT_RATE' || type === 'CRIT_DAMAGE'
      || type === 'SKILL_DAMAGE' || type === 'ENERGY_REGEN' || type === 'COOLDOWN_REDUCTION'
      || type === 'DAMAGE_REDUCTION';
    return isPct ? `${(val * 100).toFixed(1)}%` : val.toFixed(1);
  };

  const handleCreate = async () => {
    const res = await api.autoGenerate(slot, tier);
    setEquipment(res.equipment);
    setLogs([`已创建 ${tier} ${slot} Lv.${res.equipment.level}`]);
  };

  const handleEnhance = async () => {
    if (!equipment || equipment.level >= 10) return;
    const res = await api.enhance(equipment);
    setEquipment(res.data);
    setLogs(prev => [...prev, ...res.messages]);
  };

  const handleEnhanceTo = async () => {
    if (!equipment) return;
    const res = await api.enhanceTo(equipment, targetLevel);
    setEquipment(res.data);
    setLogs(prev => [...prev, ...res.messages]);
  };

  const SLOT_NAMES: Record<string, string> = {
    WEAPON: '武器', HEAD: '头盔', CHEST: '胸甲', LEGS: '护腿', FEET: '靴子',
  };

  return (
    <div className="panel">
      <h2>强化模拟</h2>

      {!equipment ? (
        <div className="setup-section">
          <div className="form-row">
            <label>槽位:</label>
            <select value={slot} onChange={e => setSlot(e.target.value)}>
              {['WEAPON', 'HEAD', 'CHEST', 'LEGS', 'FEET'].map(s =>
                <option key={s} value={s}>{SLOT_NAMES[s]} ({s})</option>
              )}
            </select>
          </div>
          <div className="form-row">
            <label>品质:</label>
            <div className="tier-group">
              {['BLUE', 'PURPLE', 'GOLD'].map(t => (
                <button key={t}
                  className={`tier-btn ${tier === t ? 'active' : ''}`}
                  style={{ borderColor: tier === t ? TIER_COLORS[t] : '#444', color: TIER_COLORS[t] }}
                  onClick={() => setTier(t)}>{t}</button>
              ))}
            </div>
          </div>
          <button className="btn-primary" onClick={handleCreate}>创建装备</button>
        </div>
      ) : (
        <div className="enhance-content">
          <div className="result-card">
            <div className="eq-header">
              <span className="eq-tier" style={{ color: TIER_COLORS[equipment.tier] }}>{equipment.tier}</span>
              <span className="eq-slot">{SLOT_NAMES[equipment.slot] || equipment.slot}</span>
              <span className="eq-level">Lv.{equipment.level}</span>
            </div>
            <div className="stat-section">
              <div className="stat-label">【主词条】</div>
              <div className="stat-row main">
                <span>{attrName(equipment.mainType)}</span>
                <span className="stat-val">+{fmtVal(equipment.mainType, equipment.mainValue)}</span>
              </div>
            </div>
            {Object.keys(equipment.subStats).length > 0 && (
              <div className="stat-section">
                <div className="stat-label sub-label">【副词条】</div>
                {Object.entries(equipment.subStats).map(([type, val]) => (
                  <div key={type} className="stat-row sub">
                    <span>{attrName(type)}</span>
                    <span className="stat-val">+{fmtVal(type, val as number)}</span>
                  </div>
                ))}
              </div>
            )}
          </div>

          <div className="enhance-actions">
            <button className="btn-primary" onClick={handleEnhance}
              disabled={equipment.level >= 10}>
              强化 (+1)
            </button>
            <div className="inline-form">
              <input type="number" min={1} max={10} value={targetLevel}
                onChange={e => setTargetLevel(Number(e.target.value))} />
              <button className="btn-secondary" onClick={handleEnhanceTo}>直升</button>
            </div>
          </div>

          {logs.length > 0 && (
            <div className="log-box">
              {logs.map((msg, i) => <div key={i} className="log-line">{msg}</div>)}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
