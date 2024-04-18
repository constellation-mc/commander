### What's New:

* Added `structContainsKey` function to expressions.
* Moved custom `random`, `lerp` and `clamp` functions to BigDecimal.
* There was an attempt at optimizing reflective field lookups by caching invalid fields and constructing a lazy class hierarchy which propagates found fields and methods downstream.