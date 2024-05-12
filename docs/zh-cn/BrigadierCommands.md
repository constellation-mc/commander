# 斜杠命令

命令官模组引入了一系列 `/` 样式的命令。这些命令都要求权限等级 2.

## `cmd:arithmetica`

这个命令可以让你在聊天栏中运行表达式。情境：`minecraft:level`（存档），`minecraft:origin`（维度）和可选的 `minecraft:this_entity`（实体）。这个命令需要一个字符串类型的表达式，以及可选的数据类型转换。

```
cmd:arithmetica ->
    \- expression
        \- cast （默认：无）
```

示例：`cmd:arithmetica "sin(level.getDayTime)" bool`

## `cmd:explode`

这个简单的命令可以生成爆炸。它的结构树如下：

```
cmd:explode ->
    \- entity （爆炸的造成者）
        \- position
            \- [power] （默认：4）
                \- [fire] （默认：否）
    \- position
        \- [power] （默认：4）
            \- [fire] （默认：否）
```

示例：`cmd:explode @s ~ ~ ~ 6.4 true`

## `cmd:data`

这一命令使你能够读写持久型数据，以便在后续的表达式中使用。提供的数据必须是数字或字符串。它的结构树如下：

::: details 结构树
```
cmd:data
    \- read （读取）
        \- level （存档）
            \- key （键值）
        \- chunk （区块）
            \- position （坐标位置）
                \- key （键值）
        \- entity （实体）
            \- entity （实体）
                \- key （键值）
        \- block_entity （方块实体）
            \- position （坐标位置）
                \- key （键值）
    \- write （写入）
        \- level （存档）
            \- key （键值）
                \- data （数据）
        \- chunk （区块）
            \- position （坐标位置）
                \- key （键值）
                    \- data （数据）
        \- entity （实体）
            \- entity （实体）
                \- key （键值）
                    \- data （数据）
        \- block_entity （方块实体）
            \- position （坐标位置）
                \- key （键值）
                    \- data （数据）
```
:::

读写示例：`cmd:data read entity @s "my_test_data"` 或 `cmd:data write entity @s "my_test_data" "Hello "`

使用示例：`this_entity.storage.my_test_data + 'World!'`
