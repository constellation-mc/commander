### What's New:

User Changes:

* Added `cmd:expression` to `/execute if`.
* Updated EvalEx to v3.3.0. [Release Notes...](https://github.com/ezylang/EvalEx/releases/tag/3.3.0)
* Removed `commander:store_expression_data` and `commander:store_nbt_data` JSON commands, as they can be easily replaced by the brigadier command and don't offer any advantages.

Example replacement:

```json
{
  "type": "commander:store_expression_data",
  "target": "level",
  "selector": "origin",
  "key": "my_cool_data",
  "expression": "random(0, 2)"
}
```

Becomes:

```json
{
  "type": "commander:commands",
  "selector": "origin",
  "commands": [
    "cmd:data write level my_cool_data ${{random(0, 2)}}"
  ]
}
```