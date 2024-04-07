# Arithmetica

Commander introduces support for evaluating math expressions with dynamic entity data.

Arithmetica uses [exp4j](https://www.objecthunter.net/exp4j/index.html) for evaluation, while not required, it may be useful to familiarize yourself with it.

## Additional functions

Commander adds some additional functions to exp4j.

- `round` rounds the value. Accepts 1 argument.
- `random` generates a random number in range. Accepts 2 arguments (min, max)
- `clamp` clamps the value between two other arguments. Accepts 3 arguments (value, min, max) 
- `min` returns the smallest of two numbers. Accepts 2 arguments (a, b) 
- `max` returns the largest of two numbers. Accepts 2 arguments (a, b) 
- `lerp` smoothly progresses the value to the target. `start + delta * (end - start)`. Accepts 3 arguments (delta, start, end)

## Reading source data

Because Arithmetica requires minecraft's loot context, you can read source data using a special syntax called an extraction.
```
identifier[field]
identifier[field$dynamic]
```
Extractions are super picky and, if used in the wrong context, could crash your game.

Some examples:
```
minecraft:this_entity[x]
minecraft:this_entity[living/attribute$generic.max_health]
minecraft:this_entity[rot/x]
minecraft:origin[world/day_time]
```

TODO: document all available extractions.

## Using Arithmetica

The fastest way to get started is to use the `cmd:arithmetica` command. You should wrap your expression with `"` to satisfy Brigadier.

Other places are [command macros](Commands#command-macros) and the `commander:arithmetica` loot number provider.

Using the provider in conditions is as simple as it gets:

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