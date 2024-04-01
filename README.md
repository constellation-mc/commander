## Commander

Checkout the wiki to learn more! https://github.com/constellation-mc/commander/wiki

If you have a suggestion or a feature request, be sure to share it here: https://github.com/constellation-mc/commander/discussions/categories/ideas

### Quick Introduction

**Commander** introduces a new event system to the data pack format.

Each file placed in `commander/events` represents an event subscription. Commander mirrors some fabric events under the `commander` namespace. e.g. `server_tick/start`

```json
{
  "event": "namespace:event", //the event this file subscribes to.
  "parameters": null, //optional parameters block
  "commands": [
    {
      "type": "namespace:command", //command type
      "condition": { //optional conditions block. Uses the vanilla predicates system
        "condition": "minecraft:random_chance",
        "chance": 0.5
      },
      "parameter": null, //misc command parameters
      "parameter_2": 2
    }
  ]
}
```

`parameters` is a block of addition subscription info. Can be omitted if the event does not expect any parameters.

`commands` block defines actions the event will perform when invoked. Commands don't always interact with the game world, some commands are purely logical (`commander:random`), some are service commands (`commander:cancel`).

***

- [exp4j](https://www.objecthunter.net/exp4j/index.html) - math expressions eval. [Apache License 2.0](https://www.objecthunter.net/exp4j/license.html)
