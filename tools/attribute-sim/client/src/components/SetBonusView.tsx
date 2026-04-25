import React, { useEffect, useState } from 'react';
import { api } from '../api';

export default function SetBonusView() {
  const [setBonuses, setSetBonuses] = useState<any>(null);

  useEffect(() => {
    api.getSetBonuses().then(res => setSetBonuses(res.setBonuses));
  }, []);

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

  return (
    <div className="panel">
      <h2>套装加成预览</h2>

      {!setBonuses ? (
        <div className="dim-text">加载中...</div>
      ) : Object.keys(setBonuses).length === 0 ? (
        <div className="dim-text">暂无可用的套装配置</div>
      ) : (
        Object.entries(setBonuses).map(([id, sb]: [string, any]) => (
          <div key={id} className="result-card">
            <div className="eq-header">{sb.name} ({id})</div>

            <div className="stat-section">
              <div className="stat-label">【2件套效果】</div>
              {sb.twoPiece.map((stat: any, i: number) => (
                <div key={i} className="stat-row">
                  <span>{attrName(stat.type)}</span>
                  <span className="stat-val green">+{fmtVal(stat.type, stat.value)}</span>
                </div>
              ))}
            </div>

            <div className="set-note">
              需装备同套装 {Object.keys(sb.twoPiece).length + 1}+ 件触发
            </div>
          </div>
        ))
      )}

      <div className="info-card">
        <div className="info-title">套装机制说明</div>
        <ul>
          <li>套装效果通过装备的 <code>setId</code> 匹配</li>
          <li>装备 2 件同套装即可激活 2 件套属性加成</li>
          <li>套装加成以百分比形式作用于最终属性</li>
          <li>每件装备在 <code>sets.yml</code> 中定义套装配置</li>
        </ul>
      </div>
    </div>
  );
}
