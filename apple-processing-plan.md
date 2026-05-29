# More Apple 加工流程实现计划

## 目标

实现苹果加工流程的核心能力，但不接入任何调用入口。

- 苹果栈可以保存多个属性。
- 属性以 `id + level` 表示。
- 加工流程可以给苹果添加属性、升级已有属性、拒绝非法加工。
- 不做“最多 3 个 upgrade”这类总数限制。
- 不实现配方 JSON、按钮、工作台交互、实际食用效果。

## 数据模型

### `AppleModifier`

- `id`：属性唯一标识，例如 `moreapple:golden`。
- `maxLevel`：该属性自己的最高等级。限制单个属性，不限制苹果总属性数。
- `category`：分类，例如 `nutrition`、`effect`、`utility`、`cosmetic`。
- `exclusiveGroup`：可选互斥组。同组属性不能同时存在。
- `priority`：显示和未来结算顺序。

### `AppleModifierEntry`

- `id`：属性 id。
- `level`：当前等级。

栈上只存这两个字段，不保存显示名、描述和最终效果值。

### `AppleStack`

负责从 `ItemStack` 读写：

```text
MoreApple: {
  SchemaVersion: 1,
  Modifiers: [
    { Id: "moreapple:golden", Level: 1 },
    { Id: "moreapple:healing", Level: 2 }
  ]
}
```

同时兼容旧字段：

```text
MoreAppleDefinition: "moreapple:golden_apple"
```

### `AppleProcessing`

提供：

```text
applyModifier(stack, modifierId, amount)
```

行为：

- 属性不存在时新增 entry。
- 属性已存在时提升等级。
- 超过 `maxLevel` 时失败且不改输入。
- 互斥组冲突时失败且不改输入。
- 不限制苹果总属性数量。

## 不做

- 不实现配方 JSON。
- 不实现 UI。
- 不实现创造物品栏入口。
- 不实现食用效果。
- 不实现材料消耗。
- 不限制苹果总属性数。
