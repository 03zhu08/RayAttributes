import React from 'react';

const MAX_ENERGY = 100;
const RECOVERY = 10;

export default function EnergySystem() {
  return (
    <div className="panel">
      <h2>能量系统</h2>

      <div className="result-card">
        <div className="eq-header">能量参数</div>
        <div className="energy-bar-container">
          <div className="energy-bar-fill" style={{ width: '100%' }} />
          <span className="energy-bar-text">{MAX_ENERGY} / {MAX_ENERGY}</span>
        </div>

        <div className="stat-section">
          <div className="stat-row">
            <span>最大能量</span>
            <span className="stat-val">{MAX_ENERGY}</span>
          </div>
          <div className="stat-row">
            <span>普攻回复</span>
            <span className="stat-val green">+{RECOVERY}/次</span>
          </div>
          <div className="stat-row">
            <span>满能所需普攻</span>
            <span className="stat-val">{MAX_ENERGY / RECOVERY} 次</span>
          </div>
        </div>
      </div>

      <div className="info-card">
        <div className="info-title">能量机制说明</div>
        <ul>
          <li><strong>基础回复：</strong>每次普通攻击回复 <code>{RECOVERY}</code> 点能量</li>
          <li><strong>能量恢复效率：</strong>可通过装备副词条堆叠此属性</li>
          <li><strong>计算方式：</strong>每次回复 = {RECOVERY} × (1 + 能量恢复效率%)</li>
          <li><strong>示例：</strong>能量恢复效率 +10% → 每次回复 +{(RECOVERY * 0.1).toFixed(1)} (共 {RECOVERY + RECOVERY * 0.1})</li>
        </ul>
      </div>

      <div className="simulation-section">
        <div className="stat-label">回复效率模拟</div>
        <table className="sim-table">
          <thead>
            <tr>
              <th>能量恢复效率</th>
              <th>每次回复</th>
              <th>满能所需次数</th>
            </tr>
          </thead>
          <tbody>
            {[0, 0.05, 0.1, 0.2, 0.3, 0.5].map(pct => (
              <tr key={pct}>
                <td>+{(pct * 100).toFixed(0)}%</td>
                <td>+{(RECOVERY * (1 + pct)).toFixed(1)}</td>
                <td>{Math.ceil(MAX_ENERGY / (RECOVERY * (1 + pct)))}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
