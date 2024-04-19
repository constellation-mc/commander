# Events

Commander introduces a new data pack event system, which allows data packs to listen to events just like mods.

The best practice is to have a generic Fabric event and register Commander as one of its listeners.

## Creating event types

To implement event support for your mod you must first register an event type for Commander to dispatch. You can build and register events with the `EventType.Builder` class.

```java
public static final EventType CUSTOM_EVENT = EventType.builder().build(new Identifier("modid", "custom_event"));
```

If your event requires a return type, you can specify a cancel term codec with `cancelTerm()`.

```java
EventType.builder()
    .cancelTerm(Codec.INT)
    .build(new Identifier("modid", "custom_event"));
```

Events can aceepts additional parameters using `extension()`. After specifying an extension you can return a custom type.

```java
EventType.builder()
    .extension(Codec.STRING, subscriptions -> {
        //handle data.
        return /*Return listeners*/;
    })
    .build(new Identifier("modid", "custom_event"));
```

The default return type is `List<Command.Conditioned>`.

## Invoking the event

If you didn't specify an extension, or opted to return a `List<Command.Conditioned>`, you can use the included `EventExecutors` util.

To use the util, you have to pass the event type, the execution world, and a loot context supplier.

```java
CustomEvent.EVENT.register((world, entity) -> runVoid(CUSTOM_EVENT, world, () -> makeContext(world, entity, entity.getPos())));
```

```java
private static LootContext makeContext(ServerWorld world, Entity entity, Vec3d origin) {
    LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(world);
    builder.add(THIS_ENTITY, entity).add(ORIGIN, origin);
    return new LootContext.Builder(builder.build(LootContextTypes.COMMAND)).build(null /*Optional.empty() in 1.20.4*/);
}
```

If you did specify an extension, or are using an unsupported return type, you'll have to write custom resolution logic.

To do that you'll simple have to create an `EventContext`. `EventContext` is used to pass execution parameters to commands. 

```java
EventContext context = EventContext.builder(type)
        .addParameter(EventKey.LOOT_CONTEXT, /*instance of loot context*/)
        .build();
for (Command.Conditioned subscriber : subscribers) subscriber.execute(context);
```
To get the return value, you can call `getReturnValue(def)`, the default value can be null. The return type is generic.

```java
boolean val = context.getReturnValue(def);
if (val != def) return val;
```