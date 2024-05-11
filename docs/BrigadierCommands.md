# Brigadier Commands

Commander introduces a handful of `/` commands. All commands require perm level 2.

## `cmd:arithmetica`

This command allows you evaluate expressions right in the chat. Context: `minecraft:level`, `minecraft:origin` and optional `minecraft:this_entity`. This command requires a string expression and an optional cast.

```
cmd:arithmetica ->
    \- expression
        \- cast (default: none)
```

Example: `cmd:arithmetica "sin(level.getDayTime)" bool`

## `cmd:explode`

This simple command allows you to spawn explosions. The command tree is as follows:

```
cmd:explode ->
    \- entity (who caused the explosion)
        \- position
            \- [power] (default: 4)
                \- [fire] (default: false)
    \- position
        \- [power] (default: 4)
            \- [fire] (default: false)
```

Example: `cmd:explode @s ~ ~ ~ 6.4 true`

## `cmd:data`

This command allows you to read and write special, persistent data which can later be referenced in expressions. Provided data must be either a number or a string. The command tree is as follows:

::: details Tree
```
cmd:data
    \- read
        \- level
            \- key
        \- chunk
            \- position
                \- key
        \- entity
            \- entity
                \- key
        \- block_entity
            \- position
                \- key
    \- write
        \- level
            \- key
                \- data
        \- chunk
            \- position
                \- key
                    \- data
        \- entity
            \- entity
                \- key
                    \- data
        \- block_entity
            \- position
                \- key
                    \- data
```
:::

Example: `cmd:data read entity @s "my_test_data"` or `cmd:data write entity @s "my_test_data" "Hello "`

Example reference: `this_entity.storage.my_test_data + 'World!'`