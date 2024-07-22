### What's New:

User Changes:

* Added `arrayFindAny` and `arrayFindFirst` functions to expressions. As the name suggests, those functions will try to find an element of an array or return `null` if the array is empty.
* Added `chain` function to expressions. This function operates on the results of the first one and allows chaining multiple function calls together like so:
  ```
  //`it` is the result of the previous function, unless there's a lower level lamda.
  chain(arrayOf(2, 3, 6), arrayMap(it, sqrt(it)), arrayFindFirst(it));
  //this is the same as:
  arrayFindFirst(arrayMap(arrayOf(2, 3, 6), sqrt(it)))
  ```
* Added `remove` to `cmd:data`.
* Added `cmd:operate` to the `scoreboard players` command.
* Added registry access for expressions! Now you can access the game's static and dynamic content registries by using new `Registry` and `DynamicRegistry` functions.
  * Why is this useful? For example, this allows you to compare item stacks based on their item type:
  ```
  this_entity.getHandSlots[0].getItem == Registry('item').access.chest
  ```
  * For item and block registries there is a shortcut: `Item`, `Block`.
  ```
  this_entity.getHandSlots[0].getItem == Item('chest')
  ```
  * `Biome` and `DimensionType` are available dynamic registry shortcuts.

Dev Changes:

There's a handful new experimental APIs, which allow you to extend the expression system.

* Exposed `Object getValue()` and `Result convert(Object o)` in Expression.Result.
* Exposed `Expression eval(LootContext context, Map<String, Object> parameters)` in Expression.
* Exposed `ProxyMap` and `ObjectConverter`.
* Exposed `CustomFields`. One of the more interesting APIs. Allows adding new 'virtual' fields to reflective expression objects.
  ```java
  static {
    CustomFields.addVirtualField(Entity.class, "nbt", NbtPredicate::entityToNbt);
  }
  ```