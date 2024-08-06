### What's New:

User Changes:

* Added the (data pack) expression library!

This feature allows adding common expressions to a library where each is identified using an Identifier.

```json5
{
  "replace": false, // Can be omitted.
  "expressions": {
    "test:cool_expression": "score * 2.5",
    "test:boolean_expression": "level.isDay && !level.isRaining"
  }
}
```

Later on, these expressions can be evaluated inside other expressions using the library container, like so:

```
sqrt(library.my_id:some_value) * library.my_id:other_value
```

Or in `execute`/`scoreboard` brigadier commands using `cmd:library`. 
Prefer using the library in Brigadier commands! 
It does not require parsing the expression in-place!

* Brigadier macros in JSON commands should now correctly fail on dangling braces.

Dev Changes:

* Added more javadoc to the `api` package.
* Removed `evalex` and `mapping-io` from pom.xml
* Added `LongExpression`. Similar to `Arithmetica` and `BooleanExpression`, but for longs!
* Tried to fix expression equality.
* Added missing 'parameter' methods to `Arithmetica`, `BooleanExpression` and `BrigadierMacro`.
* Added `Expression.Result#NULL`.
  
Other Changes:

* The mod should now fail with slightly better error messages.
* Inlined constant `BooleanExpression` instances.