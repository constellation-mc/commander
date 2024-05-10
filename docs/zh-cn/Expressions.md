# 表达式

命令官模组凭借动态实体数据，实现了运算表达式的功能。

算术使用 [EvalEx](https://ezylang.github.io/EvalEx/) 来实现运算功能。掌握它并非必要，但多有好处。这里的区别是，所有函数、变量、常量都对大小写有严格的准确度要求，并且所有函数名都是驼峰式大小写（除第一个词语，其它首字母大写，不加标点），而不是蛇式（全大写，且用_划分）。

## 额外功能

本模组在 EvalEx 的基础上，添加了一些额外功能。

A "Lambda" here means a regular expression, but with the `it` parameter. Example: `arrayFind(arrayOf(0, 1, 2), it == 1)`. Lambda parameters are marked with `λ`.

Variable Arguments (VarArgs) are marked with `...`. (This means you can specify as many arguments as you need)

::: details 计算相关

| 函数  |  描述 |  参数 | 示例 |
|---|---|---|---|
| `random` | Generates a random number in range. | `min`, `max` | `random(0, 23)` |
| `clamp` | Clamps the value between two other arguments.  | `value`, `min`, `max` | `clamp(12, 14, 16)` |
| `lerp` | Smoothly progresses the value to the target.  | `delta`, `start`, `end` | `lerp(0.5, 10, 16)` |

:::

::: details 数组

All functions construct new arrays and do not mutate the original array.

| 函数  |  描述 |  参数 | 示例 |
|---|---|---|---|
| `arrayOf` | Construct an array from the specified objects. | `args...` | `arrayOf(0, 23)` |
| `arrayMap` | Mutates all objects in this array. | `array`, `function(λ)` | `arrayMap(arrayOf(0,1,2), sqrt(it))` |
| `arrayFind` | Filters all objects not matching the predicate. | `array`, `predicate(λ)` | `arrayFind(arrayOf(0,1,2), it == 1)` |
| `arrayAnyMatch` | Checks if any of the objects in this array match the predicate. | `array`, `predicate(λ)` | `arrayAnyMatch(arrayOf(0,1,2), it == 1)` |
| `arrayNoneMatch` | Checks if none of the objects in this array match the predicate. | `array`, `predicate(λ)` | `arrayNoneMatch(arrayOf(0,1,2), it == 1)` |
| `arrayAllMatch` | Checks if all objects in this array match the predicate. | `array`, `predicate(λ)` | `arrayAllMatch(arrayOf(0,1,2), it == 1)` |

:::

::: details 杂项

| 函数  |  描述 |  参数 | 示例 |
|---|---|---|---|
| `structContainsKey` | Checks if a struct contains some key. | `struct`, `key...` | `structContainsKey(block_state.properties, 'candles')` |
| `hasContext` | Checks if a expression parameter is available  | `key...` | `hasContext('tool')` |
| `length` | Returns the length of the specified object or 0.  | `value` | `length('Hello World!')` |
| `strFormat` | Formats a string according to the pattern.  | `pattern`, `args...` | `strFormat('Hello %s World!', 23)` |
| `ifMatches` | Simillar to built-in `if`, but with Lambdas.  | `value`, `predicate(λ)`, `ifTrue(λ)`, `ifFalse(λ)` | `ifMatches(arrayFind(arrayOf(0,1,2), it == 1), length(it) > 0, it[0], 0)` |

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

### Commander Extensions:

Commander adds special fields to objects to extend expression capabilities.

::: details `nbt` (Stacks, Entities, Block Entities)

Added to item stacks, entities, and block entities. Allows you to read the NBT of an object. While this can be convenient, NBT access is not fast, and when used in frequently invoked places (like tick events), can bring the game to a halt.

Example: `this_entity.nbt.Air`

:::

::: details `properties` (Block States)

Added to states, like block states.

Example: `block_state.properties.candles`

:::

### P.S.

If you have to do a lot of expensive operations in expressions in tick events, you should delay the execution, if possible, by checking `level.getDayTime % 20 == 0`, this will make sure that the expression is executed every second and not 20 times per second. `% 40` is every 2 seconds and `% 10` is every 1/2 second.

表达式使用的是 Mojang 的映射（首次载入时下载），这意味着几乎所有公共的字段和 get 类型的方法都可以在表达式里使用。如果本模组无法设置映射，你可能需要依赖于平台的名称（比如 Fabric 的中间映射）。

::: details Random examples

Default minecart speed is computed using this formula:

`if(this_entity.isInWater, 4, 8) / 20`, and furnace: `if(this_entity.isInWater, 3, 4) / 20`

***

This ridicilous expression can print the current level time in human time. https://bukkit.org/threads/how-can-i-convert-minecraft-long-time-to-real-hours-and-minutes.122912/

`strFormat('%02.0f:%02.0f', floor((level.getDayTime / 1000 + 8) % 24), floor(60 * (level.getDayTime % 1000) / 1000))`

So the output can be: `00:00`, or `13:45`, etc.

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
