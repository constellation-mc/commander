### What's New:

User Changes:

This update introduces new JSON commands to interact with persistent data.

`commander:store_nbt_data` which allows you to store a simple string or number value,
and `commander:store_expression_data` just like `store_nbt_data`, but with expressions.

```json5
{
  "type": "commander:store_nbt_data", //command type
  "target": "level", //the target we want to attach data to.
  "selector": "origin", //Selector for position and entity targets.
  "key": "cmd_my_cool_data", //The data key for later reference.
  "element": "Hello World!" //Our string or number value. This is a nbt element, so true and false are not supported.
}
```
`store_expression_data` is identical, but `element` is renamed to `expression` and only accepts expressions.

The target can be one of the following:

- `level` current level. Any selector.
- `chunk` selected chunk. Position selector.
- `entity` selected entity. Entity selector.
- `block_entity` selected block entity. Position selector.

Later on this data can be used in expressions like so:

`level.storage.my_cool_data * 0.7` or `this_entity.storage.even_cooler_data == 'freak''`.

Along with the JSON command this update introduces a the `cmd:data` brigadier command. This command allows you to read and write data just like the JSON command.

The syntax goes like this:

`cmd:data` -> operation (`read`/`write`) -> target -> selector (`@s`/`~ ~ ~`) -> key (-> data (for `write`))

Other Changes:

* Moved mappings cache to `~/.commander`.
* Mapping are now saved as compressed TSRG2.

Dev Changes:

* Added `is(Type)Value` to expression result.