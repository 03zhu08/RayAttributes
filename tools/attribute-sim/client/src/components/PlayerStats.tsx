import React, { useState } from 'react';
import { api } from '../api';

const SLOTS = ['WEAPON', 'HEAD', 'CHEST', 'LEGS', 'FEET'];
const TIER_COLORS: Record<string, string> = { BLUE: '#55f', PURPLE: '#a55', GOLD: '#fa0' };

export default function PlayerStats() {
  const [playerLevel, setPlayerLevel] = useState(80);
  const [equipment, setEquipment] = useState<any[]>([]);
  const [attributes, setAttributes] = useState<any>(null);
  const [creatingSlot, setCreatingSlot] = useState('WEAPON');

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
    const isPct = type.includes('PERCENT') || ['CRIT_RATE', 'CRIT_DAMAGE', 'SKILL_DAMAGE',
      'ENERGY_REGEN', 'COOLDOWN_REDUCTION', 'DAMAGE_REDUCTION'].includes(type);
    return isPct ? `${(val * 100).toFixed(1)}%` : val.toFixed(1);
  };

  const handleAddEquipment = async () => {
    const res = await api.autoGenerate(creatingSlot);
    setEquipment(prev => [...prev, res.equipment]);
  };

  const handleRemoveEquipment = (index: number) => {
    setEquipment(prev => prev.filter((_, i) => i !== index));
  };

  const handleCalculate = async () => {
    if (equipment.length === 0) return;
    const res = await api.calculatePlayer(playerLevel, equipment);
    setAttributes(res.attributes);
  };

  const SLOT_NAMES: Record<string, string> = {
    WEAPON: '武器', HEAD: '头盔', CHEST: '胸甲', LEGS: '护腿', FEET: '靴子',
  };

  return (
    <div className="panel">
      <h2>玩家属性计算</h2>

      <div className="form-row">
        <label>玩家等级:</label>
        <input type="number" min={1} max={100} value={playerLevel}
          onChange={e => setPlayerLevel(Number(e.target.value))} />
      </div>

      <div className="form-row">
        <label>添加装备:</label>
        <div className="inline-group">
          <select value={creatingSlot} onChange={e => setCreatingSlot(e.target.value)}>
            {SLOTS.map(s => <option key={s} value={s}>{SLOT_NAMES[s]}</option>)}
          </select>
          <button className="btn-secondary" onClick={handleAddEquipment}>随机生成 +</button>
        </div>
      </div>

      {equipment.length > 0 && (
        <div className="equipped-list">
          <div className="stat-label">已装备 ({equipment.length} 件):</div>
          {equipment.map((eq, i) => (
            <div key={i} className="equipped-item">
              <span style={{ color: TIER_COLORS[eq.tier] }}>{eq.tier}</span>
              <span>{SLOT_NAMES[eq.slot] || eq.slot}</span>
              <span className="dim">Lv.{eq.level}</span>
              <button className="btn-remove" onClick={() => handleRemoveEquipment(i)}>✕</button>
            </div>
          ))}
        </div>
      )}

      <button className="btn-primary" onClick={handleCalculate}
        disabled={equipment.length === 0}>
        计算属性
      </button>

      {attributes && (
        <div className="result-card">
          <div className="eq-header">Lv.{attributes.level} 玩家属性</div>

          <div className="stat-section">
            <div className="stat-label">【基础成长】</div>
            {Object.entries(attributes.flat).filter(([_, v]) => (v as number) > 0).map(([type, val]) => (
              <div key={type} className="stat-row flat">
                <span>{attrName(type)} (基础)</span>
                <span className="stat-val">{fmtVal(type, val as number)}</span>
              </div>
            ))}
          </div>

          {Object.values(attributes.pct).some((v: any) => (v as number) > 0) && (
            <div className="stat-section">
              <div className="stat-label">【百分比加成】</div>
              {Object.entries(attributes.pct).filter(([_, v]) => (v as number) > 0).map(([type, val]) => (
                <div key={type} className="stat-row pct">
                  <span>{attrName(type)}</span>
                  <span className="stat-val">+{fmtVal(type, val as number)}</span>
                </div>
              ))}
            </div>
          )}

          <div className="stat-section">
            <div className="stat-label final-label">【最终属性】</div>
            {Object.entries(attributes.final).filter(([_, v]) => (v as number) > 0).map(([type, val]) => (
              <div key={type} className="stat-row final">
                <span>{attrName(type)}</span>
                <span className="stat-val">{fmtVal(type, val as number)}</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
