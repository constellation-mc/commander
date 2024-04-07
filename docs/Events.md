# Events

Events in Commander are points during gameplay when your [commands](Commands) will be invoked.

## Introduction to subscriptions

An event declaration in your subscription file is an identifier of the event you want to subscribe to. An event can accept additional parameters in the `parameters` block, but built-in events do not require them (yet).

::: details Example
```json
{
  "event": "commander:after_killed_by_other"
}
```
<br/>

```json
{
  "event": "modid:custom_event",
  "parameters": {
  }
}
```
:::

The subscription file can contain multiple subscriptions to different (or identical) events.

::: details Example
```json
{
  "events": [
    {
      "event": "commander:after_killed_by_other"
    },
    {
      "event": "commander:allow_damage"
    },
    {
      "event": "commander:player_attack/block"
    }
  ]
}
```
:::

## Built-in events

Commander wraps most compatible fabric events under the `commander` namespace. "Return" here means the [cancel command](Commands#commandercancel)

Currently available events can be seen here: [EntityEvents](https://github.com/constellation-mc/commander/blob/main/src/main/java/me/melontini/commander/impl/builtin/events/EntityEvents.java), [PlayerEvents](https://github.com/constellation-mc/commander/blob/main/src/main/java/me/melontini/commander/impl/builtin/events/PlayerEvents.java), [ServerLifecycle](https://github.com/constellation-mc/commander/blob/main/src/main/java/me/melontini/commander/impl/builtin/events/ServerLifecycle.java), [ServerTick](https://github.com/constellation-mc/commander/blob/main/src/main/java/me/melontini/commander/impl/builtin/events/ServerTick.java)

TODO: Automatically generate event list.