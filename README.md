# RayAttributes

基于 Lore 文本识别属性的 Minecraft Bukkit RPG 属性插件。

## 工作原理

RayAttributes 通过物品 Lore 中嵌入的 `RA|` 编码字符串来识别装备属性。插件**从不随机生成或添加属性**，除非显式选择加入。

### Lore 编码格式

```
RA|<槽位>|<品质>|<等级>|<主词条类型:主词条值>|<副词条列表>|<套装ID>
```

Lore 中的编码行示例：
```
RA|WEAPON|BLUE|1|ATTACK:12|CRIT_RATE:0.02,SKILL_DAMAGE:0.05|NONE
```

`LoreParser` 读取该行并解析所有属性数据。没有此编码行的物品会被 RayAttributes 忽略。

## 装备创建

### `/raya give <玩家> <槽位> <品质> [套装ID]`

创建一件**仅含 1 条主词条、无副词条**的 Lv.1 基础装备。不会随机生成任何额外属性。副词条必须通过强化手动添加。

### 自动生成 (`<RayAttributes-Automatic>`)

自动属性生成仅在物品 Lore 包含以下标记时触发：

```
<RayAttributes-Automatic>
```

该标记可由外部插件（如 FantasyWeapon）或服主手动写入物品 Lore。检测到该标记后，RayAttributes 会：

1. 根据物品槽位类型自动生成 1 条随机主词条和 2 条随机副词条
2. 将强化等级设为 **Lv.1**
3. 用生成的 `RA|` 编码数据覆写原 Lore

**不含此标记时，RayAttributes 永远不会自动生成属性。**

### 外部插件 API

FantasyWeapon 等插件可通过代码调用自动生成：

```java
RayAttributes raya = (RayAttributes) Bukkit.getPluginManager().getPlugin("RayAttributes");
raya.getEquipmentManager().autoGenerate(itemStack);
```

或直接在给予物品的 Lore 中包含 `<RayAttributes-Automatic>`，`autoGenerate()` 方法会自动处理。

## 强化

强化**完全手动**。手持装备使用 `/raya enhance`：

- 提升强化等级
- 在配置的阈值等级解锁新副词条（如 Lv.3 解锁第 3 条、Lv.5 解锁第 4 条）
- 在特定等级（Lv.6、Lv.8、Lv.10）提升现有副词条数值

## 命令

| 命令 | 说明 |
|---|---|
| `/raya give <玩家> <槽位> <品质> [套装ID]` | 给予一件 Lv.1 基础装备 |
| `/raya info [玩家]` | 查看玩家简要属性 |
| `/raya stats [玩家]` | 查看玩家详细属性面板（分类展示+属性明细） |
| `/raya enhance` | 强化手持装备 |
| `/raya setlevel <玩家> <等级>` | 设置玩家等级 |
| `/raya reload` | 重载配置文件 |

## 配置文件

- **equipment.yml** — 各槽位主/副词条池、最大值、强化阈值
- **lore-colors.yml** — Lore 显示配色方案
- **sets.yml** — 套装加成定义
- **config.yml** — 基础属性、暴击设置、能量系统

## PlaceholderAPI 支持

安装 PlaceholderAPI 后自动注册，提供以下占位符变量：

| 占位符 | 说明 |
|---|---|
| `%raya_level%` | 玩家等级 |
| `%raya_energy%` | 当前能量值 |
| `%raya_max_energy%` | 最大能量值 |
| `%raya_<属性名>%` | 属性最终值 |
| `%raya_<属性名>_flat%` | 属性基础值 |
| `%raya_<属性名>_percent%` | 属性百分比值 |
| `%raya_<属性名>_final%` | 属性最终值（显式别名） |

属性名使用 AttributeType 枚举名转小写，例如：
`attack`、`health`、`defense`、`crit_rate`、`crit_damage`、`skill_damage`、
`energy_regen`、`cooldown_reduction`、`damage_reduction`、`attack_percent`、
`health_percent`、`defense_percent`。

示例：
```
%raya_attack%        → 攻击力最终值  (如 1200)
%raya_crit_rate%     → 暴击率最终值  (如 35.0)
%raya_health_flat%   → 生命基础值    (如 1700)
```

## 属性模拟工具

`tools/attribute-sim/` 目录下提供了一个 TypeScript 全栈属性计算模拟器，涵盖插件所有核心机制：

- **后端**（Express + TypeScript）：装备生成、强化、玩家属性计算、伤害公式，逻辑与插件完全对齐
- **前端**（React + Vite + TypeScript）：图形化界面，支持装备创建、强化模拟、玩家属性计算、战斗模拟、自动生成演示、套装加成预览、能量系统展示

启动方式：
```bash
cd tools/attribute-sim
npm run dev
# 服务端 http://localhost:3001  |  前端 http://localhost:5173
```

## 核心设计原则

- **不随机注入属性** — 物品只拥有其 `RA|` 编码 Lore 中指定的属性
- **选择加入的自动生成** — 仅当存在显式的 `<RayAttributes-Automatic>` 标记时触发
- **Lv.1 起点** — 所有创建的装备从 Lv.1 开始，强化永远手动
- **Lore 驱动** — 属性从 Lore 文本中识别，而非硬编码物品 ID
