# 命令

命令官模组中的命令是你的订阅文件中的主角。

命令相当于 JSON 文件中的对象。事件被触发时，`type` 字段将决定哪些命令被执行。

`condition` 字段可以应用于所有类型的命令，它能够在当下事件的情境中执行。这一字段使用了原版的条件系统，这个工具 [misode.github.io](https://misode.github.io/predicate/) 可以帮你快速创建条件。

部分种类的命令有额外参数。

尽管本模组尽量避免在事件外使用命令，其他的项目还是可能把命令整合到其他情境中。这就不在支持范围内了。

[[toc]]

## 选择器

一些命令需要你指定“选择器”。选择器用于选择执行者，它可以是一个位置，也可以（可选）是一个实体。一些例外除外，选择器模仿了原版的战利品情境。你可以在[内置选择器](https://github.com/constellation-mc/commander/blob/main/src/main/java/me/melontini/commander/impl/builtin/BuiltInSelectors.java)页面，了解所有的内置选择器：。

## 内置命令
本模组内置了少量命令，因为游戏内交互应该交给 `/` 样式的命令，或者函数。

### `commander:commands`
这是你在订阅文件中，主要会用到的命令类型。

这一种类需要有一个 selector（选择器），以及一系列命令（或函数）。推荐使用函数，因为游戏的函数加载器会在重载时校验它们。

::: details 示例
```json
{
  "type": "commander:commands",
  "selector": "commander:random_player",
  "commands": [
    "/cmd:explode ~ ~1 ~ 2"
  ]
}
```
:::

#### 命令宏

对于想要在命令中插入一些东西的人，命令宏是不二之选。

命令宏就是 `${{}}`，一个能让你通过表达式，动态插入内容的代码块。示例如下：
```
"/say 我的名字是${{level.dimension.location}}！"
```
这段命令在主世界中执行，将输出 `我的名字是minecraft:overworld`。

你可以在[表达式](Expressions)页面中，了解更多关于它的信息。

命令宏默认返回字符串，但你可以像这样将它转化为其它类型：

```
$(long){{random(0, 34)}} 34.52946 -> 34
$(double){{true}} true -> 1.0
$(bool){{0}} 0 -> false
```

### `commander:cancel`
这种类型比较特别，只能在特定类型的事件里使用。

这一类型只要求具备有合适返回值的 `value` 字段。

::: details 示例
```json
{
  "type": "commander:cancel",
  "value": false
}
```

```json
{
  "type": "commander:cancel",
  "value": "fail"
}
```
:::

### `commander:all_of`, `commander:any_of`, `commander:defaulted`
这三种类型的功能相似。如果 condition（条件）返回 true，就执行在可选的 `then` 代码块中的命令。

`all_of` 要求所有命令成功执行，`any_of` 要求其中任一命令成功执行，`defaulted` 要求其中任一命令执行失败。

注意，就算条件在很前面就返回 true 了，还是会执行全部命令！但是，在启用 `short_circuit` 后，条件不满足时，将立即退出执行。

::: details 示例
```json
{
  "event": "commander:player_use/item",
  "commands": [
    {
      "type": "commander:all_of",
      "condition": {
        "condition": "minecraft:match_tool",
        "predicate": {
          "items": [
            "minecraft:diamond"
          ]
        }
      },
      "commands": [
        {
          "type": "commander:commands",
          "selector": "this_entity",
          "commands": [
            "/say 是钻石..."
          ]
        },
        {
          "type": "commander:cancel",
          "value": "success"
        }
      ]
    }
  ]
}
```
:::

### `commander:random`
这一类型将按权重随机执行其中的命令。默认执行一轮。你可以在非必要的 `rolls` 字段中指定轮次（支持[表达式](Expressions)）。
::: details 示例
```json
{
  "type": "commander:random",
  "rolls": 2,
  "commands": [
    {
      "weight": 2,
      "data": {
        "type": "commander:commands",
        "selector": "this_entity",
        "commands": [
          "/say weight 2"
        ]
      }
    },
    {
      "weight": 6,
      "data": {
        "type": "commander:commands",
        "selector": "this_entity",
        "commands": [
          "/say weight 6"
        ]
      }
    }
  ]
}
```
:::
