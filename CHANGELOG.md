### What's New:

* Added entity parameter to `cmd:explode`. The entity will be marked as the "creator" of the explosion and other entities will redirect their anger to the entity if affected by the explosion.
* Added fire parameter to `cmd:explode`. Creates fire, if true.
* Fixed `double` casts converting numbers to scientific notation.
* `long` and `int` casts no longer actually convert to their types, instead they truncate the trailing zeros.