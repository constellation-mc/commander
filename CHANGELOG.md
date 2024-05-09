### What's New:

User Changes:

This update extends the expression syntax to support nbt and block properties.

* Returned to downloading mappings at startup.
* Compound and List NBT tags are now properly supported.
* Added `nbt` field to item stacks, entities and block entities.

This Allows you to access NBT data like so: `this_entity.nbt.Air`. You should avoid NBT access, as it can get extremely slow.

* Added `properties` field to *states.

This can be used to access block state properties like so: `block_state.properties.candles`.

* Added lots of new functions. Consult the wiki for more info!
* Added `short_circuit` to `defaulted`, `all_of`, `any_of`. If true, commands will terminate immediately upon the condition failing.
* `hasContext` and `structContainsKey` now accept VarArgs.
* Removed arbitrary map support, as it was pretty poorly implemented.
* Constants are now case-sensitive.

Dev Changes:

* Moved Command codecs to MapCodec.
* Added BooleanExpression, similar to Arithmtica.
* Added `runTriState` to EventExecutors.