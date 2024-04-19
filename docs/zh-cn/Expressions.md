# 表达式

命令官模组凭借动态实体数据，实现了运算表达式的功能。

算术使用 [EvalEx](https://ezylang.github.io/EvalEx/) 来实现运算功能。掌握它并非必要，但多有好处。

## 额外功能

本模组在 EvalEx 的基础上，添加了一些额外功能。

- `random`  能在指定范围内生成随机数。接受 2 个值（最小，最大）。
- `clamp` 将其他两个值约束在范围内。接受 3 个值（值，最小，最大）。
- `lerp` 平滑地将初值过渡到末值。`初值 + 平滑系数 * (末值 - 初值)`。接受 3 个值（平滑系数，初值，末值）。

## 读取情境数据

因为算术以游戏的战利品功能为情境，你可以通过一种叫"提取"的特殊句法来读取源数据。
```
标识符.字段
标识符.方法
标识符.方法.字段.方法
```
注意，你的用到的字段与情境**必须**存在，不然表达式是无效的。

示例：
```
minecraft:this_entity.getX
this_entity.getHealth
minecraft:this_entity.isInWaterOrRain
this_entity.blockPosition.getX
origin.x
origin.reverse.y
minecraft:level.getDayTime
```

表达式使用的是 Mojang 的映射（首次载入时下载），这意味着几乎所有公共的字段和 get 类型的方法都可以在表达式里使用。如果本模组无法设置映射，你可能需要依赖于平台的名称（比如 Fabric 的中间映射）。

## 使用表达式

通过使用 `cmd:arithmetica` 命令，你可以快速上手。记得用 `"` 括住表达式来满足格式要求。

其他使用情境见 [命令宏](Commands#command-macros)，`commander:arithmetica` 的数字提供器以及 `commander:expression` 接口。

使用接口的例子：

```json
{
  "condition": "commander:expression",
  "value": "level.isDay"
}
```

在条件下使用接口的例子：

```json
{
  "condition": "minecraft:value_check",
  "value": {
    "type": "commander:arithmetica",
    "value": "round(random(5, 15))"
  },
  "range": {
    "min": 12.3,
    "max": 32
  }
}
```
