Commands in Commander are the main actors in your subscription.

In JSON, a command is an object. The required `type` field selects the command that will be executed when the event is invoked.

All command types accept a special `condition` field, that can operate on the current event context. The field uses the vanilla predicate system, so [misode.github.io](https://misode.github.io/predicate/) can be used to quickly create predicates.

Various command types can take additional parameters.

Although Commander avoids using commands outside events, other projects might integrate commands to other contexts. This is not unsupported.

[[toc]]

## Built-in commands
The mod comes with a minimal set of commands, this is by design, as game interactions should be handled by brigadier `/` commands, or functions.

### `commander:commands`
This is the main type of command you will use when writing subscriptions.

This command type expects a selector and either a list of commands or a function identifier. It's recommended that you provide a function identifier, as these are verified by the function loader on reload.

::: details Example
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

#### Command Macros

If you opt into specifying commands using the array, you are a given an option of using command macros.

A macro is a simple `${{}}` block, which allows you to dynamically insert info using extractions. There are two types of macros: string and arithmetic.

A string macro is self-explanatory, it will insert a string into the command, for example:
```
"/say hi, my name is ${{origin[world/key]}}!"
```
will say `hi, my name is minecraft:overworld` in the overworld.

An arithmetic macro is a lot more interesting. Arithmetic macros are supported by [Arithmetica](Arithmetica). You can learn more about mathematical expressions on that page.

Arithmetic macros always return a floating point number (`1.7480`), but you can truncate the floating point by adding `(long)` to your expression like so:
```
$(long){{random(0, 34)}}
```

### `commander:cancel`
This is a special command type that can only be used with supported event types.

The only field required by this command is `value` with an appropriate return value.

::: details Example
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
All three of these commands have a similar function. They accept a list of commands and if the condition is true, they execute the command specified in the optional `then` block.

Note: even if the condition is true early, the command will still execute all commands!

::: details Example
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
            "/say mmm... diamond..."
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
This command type takes a weighted list of other commands and randomly executes some of them. By default, it rolls only once, but the number of rolls can be adjusted using the optional `rolls` field.

::: details Example
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