# 事件

本模组引入了全新的数据包事件系统，使数据包能够像模组一样监听事件。

最好的实践办法就是找到一个 Fabric 的事件，让本模组对它进行监听。

## 创建事件类型

为了实现模组的事件支持，你需要先注册一个事件类型，以便本模组进行分发。你可以通过 `EventType.Builder` 类来构建并注册事件。

```java
public static final EventType CUSTOM_EVENT = EventType.builder().build(new Identifier("modid", "custom_event"));
```

如果你的事件需要返回类型，你可以通过 `cancelTerm()` 来指定取消条件编解码器。

```java
EventType.builder()
    .cancelTerm(Codec.INT)
    .build(new Identifier("modid", "custom_event"));
```

通过使用 `extension()`，事件可以接受额外的形式参数。在指定扩展后，你可以返回自定义类型。

```java
EventType.builder()
    .extension(Codec.STRING, subscriptions -> {
        //处理数据
        return /*返回监听者*/;
    })
    .build(new Identifier("modid", "custom_event"));
```

默认的返回类型是 `List<Command.Conditioned>`。

## 调用事件

如果你没有指定扩展，或选择返回 `List<Command.Conditioned>`，你可以使用附带的 `EventExecutors` 辅助单元。

为了使用这一辅助单元，你需要传递：事件类型、执行的世界，以及战利品的上下文提供者。

```java
CustomEvent.EVENT.register((world, entity) -> runVoid(CUSTOM_EVENT, world, () -> makeContext(world, entity, entity.getPos())));
```

```java
private static LootContext makeContext(ServerWorld world, Entity entity, Vec3d origin) {
    LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder(world);
    builder.add(THIS_ENTITY, entity).add(ORIGIN, origin);
    return new LootContext.Builder(builder.build(LootContextTypes.COMMAND)).build(null /*在 1.20.4，.empty()可选*/);
}
```

如果你指定了扩展，或者使用了不受支持的返回类型，就需要编写自定义的解析逻辑。

为了编写这一逻辑，你需要创建 `EventContext`。`EventContext` 用于把执行形式参数传递给命令。

```java
EventContext context = EventContext.builder(type)
        .addParameter(EventKey.LOOT_CONTEXT, /*战利品的上下文的实例*/)
        .build();
for (Command.Conditioned subscriber : subscribers) subscriber.execute(context);
```
你可以通过调用 `getReturnValue(def)` 来获取返回的值，默认值可以为空值。返回的类型是 generic。

```java
boolean val = context.getReturnValue(def);
if (val != def) return val;
```
