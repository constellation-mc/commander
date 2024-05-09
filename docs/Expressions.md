# Expressions

Commander introduces support for evaluating math expressions with dynamic context data.

Expressions use [EvalEx](https://ezylang.github.io/EvalEx/) for evaluation, it may be useful to familiarize yourself with it. One major difference is that all function, variable, contant names are case-sensitive and all funtion names are camelCase and not UPPER_SNAKE_CASE.

## Additional functions

Commander adds some additional functions to EvalEx.

A "Lambda" here means a regular expression, but with the `it` parameter. Example: `arrayFind(arrayOf(0, 1, 2), it == 1)`. Lambda parameters are marked with `λ`.

Variable Arguments (VarArgs) are marked with `...`. (This means you can specify as many arguments as you need)

::: details Math

| Function  |  Description |  Arguments | Example |
|---|---|---|---|
| `random` | Generates a random number in range. | `min`, `max` | `random(0, 23)` |
| `clamp` | Clamps the value between two other arguments.  | `value`, `min`, `max` | `clamp(12, 14, 16)` |
| `lerp` | Smoothly progresses the value to the target.  | `delta`, `start`, `end` | `lerp(0.5, 10, 16)` |

:::

::: details Arrays

All functions construct new arrays and do not mutate the original array.

| Function  |  Description |  Arguments | Example |
|---|---|---|---|
| `arrayOf` | Construct an array from the specified objects. | `args...` | `arrayOf(0, 23)` |
| `arrayMap` | Mutates all objects in this array. | `array`, `function(λ)` | `arrayMap(arrayOf(0,1,2), sqrt(it))` |
| `arrayFind` | Filters all objects not matching the predicate. | `array`, `predicate(λ)` | `arrayFind(arrayOf(0,1,2), it == 1)` |
| `arrayAnyMatch` | Checks if any of the objects in this array match the predicate. | `array`, `predicate(λ)` | `arrayAnyMatch(arrayOf(0,1,2), it == 1)` |
| `arrayNoneMatch` | Checks if none of the objects in this array match the predicate. | `array`, `predicate(λ)` | `arrayNoneMatch(arrayOf(0,1,2), it == 1)` |
| `arrayAllMatch` | Checks if all objects in this array match the predicate. | `array`, `predicate(λ)` | `arrayAllMatch(arrayOf(0,1,2), it == 1)` |

:::

::: details Misc

| Function  |  Description |  Arguments | Example |
|---|---|---|---|
| `structContainsKey` | Checks if a struct contains some key. | `struct`, `key...` | `structContainsKey(block_state.properties, 'candles')` |
| `hasContext` | Checks if a expression parameter is available  | `key...` | `hasContext('tool')` |
| `length` | Returns the length of the specified object or 0.  | `value` | `length('Hello World!')` |
| `strFormat` | Formats a string according to the pattern.  | `pattern`, `args...` | `strFormat('Hello %s World!', 23)` |
| `ifMatches` | Simillar to built-in `if`, but with Lambdas.  | `value`, `predicate(λ)`, `ifTrue(λ)`, `ifFalse(λ)` | `ifMatches(arrayFind(arrayOf(0,1,2), it == 1), length(it) > 0, it[0], 0)` |

:::

## Context data access

Because Expressions require minecraft's loot context, you can read source data using a special syntax.
```
identifier.field
identifier.method
identifier.method.field.method
```
Keep in mind that requested fields and context **must** be present. Expression will fail without them.

Some examples:
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

Expressions use Mojang Mappings (downloaded on first load), this means that almost all public fields and getter methods are available in expressions. If Commander fails to setup mappings, you'll have to rely on platform names. (e.g. intermediary on Fabric)

::: details Random examples

Default minecart speed is computed using this formula:

`if(this_entity.isInWater, 4, 8) / 20`, and furnace: `if(this_entity.isInWater, 3, 4) / 20`

***

This ridicilous expression can print the current level time in human time. https://bukkit.org/threads/how-can-i-convert-minecraft-long-time-to-real-hours-and-minutes.122912/

`strFormat('%02.0f:%02.0f', floor((level.getDayTime / 1000 + 8) % 24), floor(60 * (level.getDayTime % 1000) / 1000))`

So the output can be: `00:00`, or `13:45`, etc.

:::

## Using Expressions

The fastest way to get started is to use the `cmd:arithmetica` command. You should wrap your expression with `"` to satisfy Brigadier.

Other places are [command macros](Commands#command-macros), the `commander:arithmetica` number provider and the `commander:expression` predicate.

Using the predicate:

```json
{
  "condition": "commander:expression",
  "value": "level.isDay"
}
```

Using the provider in conditions:

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