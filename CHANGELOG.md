### What's New:

User Changes:

* Added `arrayFindAny` and `arrayFindFirst` functions to expressions. As the name suggests, those functions will try to find an element of an array or return `null` if the array is empty.
* Java `Iterable`s can now be converted to arrays.
* Added registry access for expressions! Now you can access the game's static and dynamic content registries by using new `Registry` and `DynamicRegistry` functions.
  * Why is this useful? For example, this allows you to compare item stacks based on their item type:
  ```
  this_entity.getHandItems[0].getItem == Registry('item').chest
  ```
  * For item and block registries there is a shortcut: `Item`, `Block`.
  ```
  this_entity.getHandItems[0].getItem == Item('chest')
  ```
  * `Biome` and `DimensionType` are available dynamic registry shortcuts.