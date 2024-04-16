### What's New:

* Switched to [EvalEX](https://ezylang.github.io/EvalEx/) for expression parsing.

This comes with a bunch of improvements. The main of which is support for all object fields and methods, for example:

Before you had a limited number of "extractions" you could use on an object, like `x` and `player/level`, but now, you can specify **any** field or method:

Old:

```
/say hi ${{this_entity[x]}}
```

New:
```
/say hi ${{this_entity.getX}}

/say hi ${{this_entity.server.getModStatus.confidence}}

/say hi ${{round(sqrt(abs(world.getSeed)), 0)}}
```

* Added additional casts to the `$(){{}}` syntax. `bool`, `int`, `double`
* `cmd:arithmetica` now supports casts and returns strings by default. Now it also requires permission level 2.