# Expressions

Commander introduces support for evaluating math expressions with dynamic context data.

Expressions use [EvalEx](https://ezylang.github.io/EvalEx/) for evaluation, it may be useful to familiarize yourself with it.

## Additional functions

Commander adds some additional functions to EvalEx.

| Function  |  Description |  Arguments | Example |
|---|---|---|---|
| `random`  | Generates a random number in range. | `min`, `max` | `random(0, 23)` |
| `clamp`  | Clamps the value between two other arguments.  | `value`, `min`, `max` | `clamp(12, 14, 16)` |
| `lerp`  | Smoothly progresses the value to the target.  | `delta`, `start`, `end` | `lerp(0.5, 10, 16)` |
| `structContainsKey`  | Checks if a sctruct contains the specified key.  | `struct`, `key` | `structContainsKey(this_entity, "getHealth")` |
| `hasContext`  | Checks if the specified context is avalable.  | `key` | `hasContext("killer_entity")` |

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

Expressions use Mojang Mappings (downloaded on first load), this means that almost all public fields and getter methods are available in expressions. If Commander fails to setup mappings, you'll have to rely on platform names. (e.g. intermediary on Fabric)

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