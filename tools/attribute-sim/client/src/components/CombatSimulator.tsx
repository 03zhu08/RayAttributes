import React, { useState } from 'react';
import { api } from '../api';

export default function CombatSimulator() {
  const [atkLevel, setAtkLevel] = useState(80);
  const [atkEqCount, setAtkEqCount] = useState(3);
  const [defLevel, setDefLevel] = useState(80);
  const [defEqCount, setDefEqCount] = useState(3);
  const [skillMult, setSkillMult] = useState(1.0);
  const [iterations, setIterations] = useState(10);
  const [results, setResults] = useState<any[] | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSimulate = async () => {
    setLoading(true);
    try {
      // Create attacker equipment
      const atkEq: any[] = [];
      const slots = ['WEAPON', 'HEAD', 'CHEST', 'LEGS', 'FEET'];
      for (let i = 0; i < Math.min(atkEqCount, 5); i++) {
        const res = await api.autoGenerate(slots[i], 'GOLD');
        atkEq.push(res.equipment);
      }
      const atkRes = await api.calculatePlayer(atkLevel, atkEq);
      const attacker = atkRes.attributes;

      // Create defender equipment
      const defEq: any[] = [];
      for (let i = 0; i < Math.min(defEqCount, 5); i++) {
        const res = await api.autoGenerate(slots[i], 'BLUE');
        defEq.push(res.equipment);
      }
      let defender = null;
      if (defEqCount > 0) {
        const defRes = await api.calculatePlayer(defLevel, defEq);
        defender = defRes.attributes;
      }

      // Simulate
      const simRes = await api.simulateCombat(attacker, skillMult, defender, iterations);
      setResults({
        attacker: { level: atkLevel, ...attacker },
        defender: defender ? { level: defLevel, ...defender } : null,
        hits: simRes.results,
      });
    } finally {
      setLoading(false);
    }
  };

  const statName = (type: string) => {
    const names: Record<string, string> = {
      ATTACK: '攻击力', CRIT_RATE: '暴击率', CRIT_DAMAGE: '暴击伤害',
      SKILL_DAMAGE: '技能增伤', DEFENSE: '防御', DAMAGE_REDUCTION: '伤害减免',
    };
    return names[type] || type;
  };

  const fmt = (type: string, val: number) => {
    const isPct = ['CRIT_RATE', 'CRIT_DAMAGE', 'SKILL_DAMAGE', 'DAMAGE_REDUCTION'].includes(type);
    return isPct ? `${(val * 100).toFixed(1)}%` : val.toFixed(1);
  };

  return (
    <div className="panel">
      <h2>伤害模拟 (战斗)</h2>

      <div className="split-form">
        <div className="form-half">
          <h3>攻击方</h3>
          <div className="form-row">
            <label>等级:</label>
            <input type="number" min={1} max={100} value={atkLevel}
              onChange={e => setAtkLevel(Number(e.target.value))} />
          </div>
          <div className="form-row">
            <label>装备数:</label>
            <input type="number" min={0} max={5} value={atkEqCount}
              onChange={e => setAtkEqCount(Number(e.target.value))} />
          </div>
        </div>
        <div className="form-half">
          <h3>防御方</h3>
          <div className="form-row">
            <label>等级:</label>
            <input type="number" min={1} max={100} value={defLevel}
              onChange={e => setDefLevel(Number(e.target.value))} />
          </div>
          <div className="form-row">
            <label>装备数:</label>
            <input type="number" min={0} max={5} value={defEqCount}
              onChange={e => setDefEqCount(Number(e.target.value))} />
          </div>
        </div>
      </div>

      <div className="form-row">
        <label>技能倍率:</label>
        <input type="number" step={0.1} min={0.1} max={10} value={skillMult}
          onChange={e => setSkillMult(Number(e.target.value))} />
      </div>
      <div className="form-row">
        <label>模拟次数:</label>
        <input type="number" min={1} max={50} value={iterations}
          onChange={e => setIterations(Number(e.target.value))} />
      </div>

      <button className="btn-primary" onClick={handleSimulate} disabled={loading}>
        {loading ? '模拟中...' : '开始模拟'}
      </button>

      {results && (
        <div className="result-card">
          <div className="stat-section">
            <div className="stat-label">【攻击方】Lv.{results.attacker.level}</div>
            <div className="stat-row">攻击力 <span className="stat-val">{fmt('ATTACK', results.attacker.final?.ATTACK)}</span></div>
            <div className="stat-row">暴击率 <span className="stat-val">{fmt('CRIT_RATE', results.attacker.final?.CRIT_RATE)}</span></div>
            <div className="stat-row">暴击伤害 <span className="stat-val">{fmt('CRIT_DAMAGE', results.attacker.final?.CRIT_DAMAGE)}</span></div>
          </div>

          {results.defender && (
            <div className="stat-section">
              <div className="stat-label">【防御方】Lv.{results.defender.level}</div>
              <div className="stat-row">防御 <span className="stat-val">{fmt('DEFENSE', results.defender.final?.DEFENSE)}</span></div>
              <div className="stat-row">伤害减免 <span className="stat-val">{fmt('DAMAGE_REDUCTION', results.defender.final?.DAMAGE_REDUCTION)}</span></div>
            </div>
          )}

          <div className="stat-section">
            <div className="stat-label">【战斗结果 ({results.hits.length} 次)】</div>
            <div className="hits-grid">
              {results.hits.map((r: any, i: number) => (
                <div key={i} className={`hit-card ${r.isCrit ? 'crit' : ''}`}>
                  <div className="hit-num">#{i + 1}</div>
                  <div className="hit-dmg">{r.finalDamage}</div>
                  <div className="hit-detail">{r.isCrit ? '暴击!' : '普通'}</div>
                  <div className="hit-detail">减伤 {r.totalReduction}%</div>
                </div>
              ))}
            </div>
            <div className="summary-row">
              平均伤害: {(results.hits.reduce((s: number, r: any) => s + r.finalDamage, 0) / results.hits.length).toFixed(1)}
              {' | '}
              暴击率: {(results.hits.filter((r: any) => r.isCrit).length / results.hits.length * 100).toFixed(0)}%
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
