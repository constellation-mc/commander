# 表达式

命令官模组凭借动态实体数据，实现了运算表达式的功能。

算术使用 [EvalEx](https://ezylang.github.io/EvalEx/) 来实现运算功能。掌握它并非必要，但多有好处。这里的区别是，所有函数、变量、常量都对大小写有严格的准确度要求，并且所有函数名都是驼峰式大小写（除第一个词语，其它首字母大写，不加标点），而不是蛇式（全大写，且用_划分）。

## 额外功能

本模组在 EvalEx 的基础上，添加了一些额外功能。

这里的“匿名函数”指带有 `it` 形式参数的一般表达式。比如：`arrayFind(arrayOf(0, 1, 2), it == 1)`。匿名函数的形式参数都有 `λ` 作为标记。

可变实际参数（VarArgs）都有 `...` 标记（你可以无限指定实际参数）

::: details 计算相关

| 函数  |  描述 |  实参 | 示例 |
|---|---|---|---|
| `random` | 在指定范围内生成随机数。 | `min`, `max` | `random(0, 23)` |
| `clamp` | 将其他两个实际参数约束在范围内。  | `value`, `min`, `max` | `clamp(12, 14, 16)` |
| `lerp` | 平滑地将初值过渡到末值。  | `delta`, `start`, `end` | `lerp(0.5, 10, 16)` |

:::

::: details 数组

所有的函数都会构建新的数组，不对原本的造成影响。

| 函数  |  描述 |  实参 | 示例 |
|---|---|---|---|
| `arrayOf` | 指定对象构建数组。 | `args...` | `arrayOf(0, 23)` |
| `arrayMap` | 对数组中的所有对象应用更改。 | `array`, `function(λ)` | `arrayMap(arrayOf(0,1,2), sqrt(it))` |
| `arrayFind` | 过滤掉所有不符合条件的对象。 | `array`, `predicate(λ)` | `arrayFind(arrayOf(0,1,2), it == 1)` |
| `arrayAnyMatch` | 检查是否数组中存在符合条件的对象。 | `array`, `predicate(λ)` | `arrayAnyMatch(arrayOf(0,1,2), it == 1)` |
| `arrayNoneMatch` | 检查是否数组中完全没有符合条件的对象。 | `array`, `predicate(λ)` | `arrayNoneMatch(arrayOf(0,1,2), it == 1)` |
| `arrayAllMatch` | 检查是否数组中的全部对象符合条件。 | `array`, `predicate(λ)` | `arrayAllMatch(arrayOf(0,1,2), it == 1)` |

:::

::: details 杂项

| 函数  |  描述 |  实参 | 示例 |
|---|---|---|---|
| `structContainsKey` | 检查是否结构中包含指定键值。 | `struct`, `key...` | `structContainsKey(block_state.properties, 'candles')` |
| `hasContext` | 检查是否表达式的形式参数可用。  | `key...` | `hasContext('tool')` |
| `length` | 返回指定对象的长度或 0。  | `value` | `length('Hello World!')` |
| `strFormat` | 将字符串转化为指定格式。  | `pattern`, `args...` | `strFormat('Hello %s World!', 23)` |
| `ifMatches` | 类似于内置的 `if`，但介入匿名函数。  | `value`, `predicate(λ)`, `ifTrue(λ)`, `ifFalse(λ)` | `ifMatches(arrayFind(arrayOf(0,1,2), it == 1), length(it) > 0, it[0], 0)` |

:::

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

### 命令官拓展：

为拓展用途，本模组为对象新增了一些特别的字段。

::: details `nbt` （物品，实体，方块实体）

可用于物品，实体和方块实体。你能够用它读取对象的 NBT 数据。尽管便捷，这个检查的性能不尽人意，频繁调用（比如每刻）可能会导致游戏卡顿。

示例：`this_entity.nbt.Air`

:::

::: details `properties`（方块状态）

可用于状态，比如方块状态。

示例：`block_state.properties.candles`

:::

::: details `attributes`（活体）

你可以用它获取活体的数据。这个键值是标识符，所以可以写 `generic.luck`，也可以写 `minecraft:generic.luck`。

示例：`this_entity.attributes.'generic.luck'`

:::

::: details `storage`（存档，区块，实体，方块实体）

你可以用它获取持久型数据。这一数据可以通过 [`/cmd:data`](/zh-cn/BrigadierCommands#cmd-data)，[`commander:store_nbt_data`](/zh-cn/Commands#commander-store-expression-data-and-commander-store-nbt-data) 或 [`commander:store_expression_data`](/zh-cn/Commands#commander-store-expression-data-and-commander-store-nbt-data) 来读取或修改。

:::

### P.S.

如果你不得不在表达式中进行复杂的检查，你可以考虑延迟执行它，如果可以，你还可以通过 `level.getDayTime % 20 == 0` 为表达式添加 1 秒的“冷却”。同理，`% 40` 对应 2 秒，`% 10` 对应 0.5 秒。

表达式使用的是 Mojang 的映射（首次载入时下载），这意味着几乎所有公共的字段和 get 类型的方法都可以在表达式里使用。如果本模组无法设置映射，你可能需要依赖于平台的名称（比如 Fabric 的中间映射）。

::: details 一些例子

这是原版矿车速度的计算公式：

`if(this_entity.isInWater, 4, 8) / 20`, and furnace: `if(this_entity.isInWater, 3, 4) / 20`

***

这个表达式能够将游戏时间转化为现实时间。https://bukkit.org/threads/how-can-i-convert-minecraft-long-time-to-real-hours-and-minutes.122912/

`strFormat('%02.0f:%02.0f', floor((level.getDayTime / 1000 + 8) % 24), floor(60 * (level.getDayTime % 1000) / 1000))`

所以它的结果会是：`00:00`，或 `13:45` 这样的。

:::

## 使用表达式

通过使用 `cmd:arithmetica` 命令，你可以快速上手。记得用 `"` 括住表达式来满足格式要求。

其他使用情境见[命令宏](Commands#command-macros)，`commander:arithmetica` 的数字提供器以及 `commander:expression` 接口。

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
