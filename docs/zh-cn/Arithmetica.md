# 算术

命令官模组凭借动态实体数据，实现了运算表达式的功能。

算术使用 [exp4j](https://www.objecthunter.net/exp4j/index.html) 来实现运算功能。掌握它并非必要，但多有好处。

## 额外功能

本模组在 exp4j 的基础上，添加了一些额外功能。

- `round` 能够四舍五入一个值。接受 1 个值。
- `random` 能在指定范围内生成随机数。接受 2 个值（最小，最大）。
- `clamp` 将其他两个值约束在范围内。接受 3 个值（值，最小，最大）。
- `min` 返回两数中的最小数。接受 2 个值（a，b）。
- `max` 返回两数中的最大数。接受 2 个值（a，b）。
- `lerp` 平滑地将初值过渡到末值。`初值 + 平滑系数 * (末值 - 初值)`。接受 3 个值（平滑系数，初值，末值）。

## 读取源数据

因为算术以游戏的战利品功能为情境，你可以通过一种叫"提取"的特殊句法来读取源数据。
```
标识符[字段]
标识符[字段$动态数据]
```
字段对准确度要求极高，在错误的情境下使用会直接使游戏崩溃。

示例：
```
minecraft:this_entity[x]
minecraft:this_entity[living/attribute$generic.max_health]
minecraft:this_entity[rot/x]
minecraft:origin[world/day_time]
```

前景：说明所有可用字段。

## 使用算术

通过使用 `cmd:arithmetica` 命令，你可以快速上手。记得用 `"` 括住表达式来满足格式要求。

其他使用情境见 [命令宏](Commands#command-macros) 和 `commander:arithmetica` 的战利品数字提供器。

提供 condition（条件）的例子如下，非常简单：

```json
{
  "condition": "minecraft:value_check",
  "value": {
    "type": "commander:arithmetica",
    "arithmetica": "round(random(5, 15))"
  },
  "range": {
    "min": 12.3,
    "max": 32
  }
}
```
