### What's New:

User Changes:

* Added `?.` and `?` null safe operators.
  - `?.` Used on structures. Same as `.`, but returns null if there's no such field in structure.
  - `?` Used with anything that can be null. Returns the right operand if left is null or left if not.

These operators allow you to quickly check for nulls in your expressions. Let's image a situation like this:

You have a variable `struct.x` which might not exist and maybe null. Before, you'd have to write something like this:

`if(structContainsKey(struct, 'x') && struct.x != null, struct.x, valueElse())'`

Now this can be shortened to: `struct?.x ? valueElse()`. Do note that `?` has a very low precedence, so in ambiguous cases you'll have to wrap it in parentheses. e.g. `23 + struct?.x ? 23` -> `23 + (struct?.x ? 23)`.

* Java `Optional`s are now unwrapped in expressions.
* Minecraft `Identifier`s are now converted to strings in expressions.
* Gson elements can now be used in expressions.

Dev Changes:

* Fixed `equals` on CustomDataAccessors.
* Added `LootContext` as an argument for the `CustomFields#addVirtualField` function.
  
Other Changes:

* Updated mEvalEx to fix expression inlining.