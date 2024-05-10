# Expressions

As explained in [Expressions](/Expressions), Commander introduces an extensive expression system. This system is exposed as part of the API. On this page we will go over usage examples and a possible way to make expressions an optional integration.

## Using expressions directly

The built-in `Expression` class provides a string -> expression codec and a `parse` method. An expression is a `LootContext -> Expression.Result` function. `Expression.Result` represents a result value that can be converted to `BigDecimal`, `boolean`, `String`, `Instant`and `Duration`.

To apply any of the expression functions you must have an instance of `LootContext`.

```java
public static final Expression EXP = Expression.parse("strFormat('%02.0f:%02.0f', floor((level.getDayTime / 1000 + 8) % 24), floor(60 * (level.getDayTime % 1000) / 1000))").result().orElseThrow();

public String worldTimeInHumanTime(LootContext context) {
    return EXP.apply(context).getAsString();
}
```

## Special functions

Commander comes with `Arithmetica` and `BooleanExpression` which are `double` and `boolean` functions that can be encoded either as a constant or as an expression. It's worth noting that `"true"` will be decoded as an expression, but `true` will not.

```json
2.2, "random(0, 3)" //Arithmetica

true, "level.isDay" //BooleanExpression
```

## Expressions as optional integration.

When implementing support for Commander, you may want to make this integration optional. The easiest way to do this is to create a common interface that is then delegated to one of the implementations. This is how expressions are set-up in Andromeda's new config system.

Let's start by defining a simple boolean intermediary interface. I recommend using a supplier as the parameter, so you don't construct a `LootContext` for constant values.

```java
public interface BooleanIntermediary {
    boolean asBoolean(Supplier<LootContext> supplier);
}
```

Now let's implement the constant delegate.

```java
public record ConstantBooleanIntermediary(boolean value) implements BooleanIntermediary {

    @Override
    public boolean asBoolean(Supplier<LootContext> supplier) {
        return this.value;
    }
}
```

And our commander delegate.

```java
public final class CommanderBooleanIntermediary implements BooleanIntermediary {

    private final BooleanExpression expression;
    private final boolean constant;

    public CommanderBooleanIntermediary(BooleanExpression expression) {
        this.expression = expression;
        this.constant = expression.toSource().left().isPresent(); //micro optimization
    }

    @Override
    public boolean asBoolean(Supplier<LootContext> supplier) {
        return this.expression.applyAsBoolean(constant ? null : supplier.get());
    }

    public BooleanExpression getExpression() {
        return this.expression;
    }
}
```

With our delegates set-up we have to dynamically pick one or the other. Let's return to our intermediary interface and create a factory for constant values. Here I'm using `Support` from Dark Matter, but you can just copy this method:

```java
public static <T, F extends T, S extends T> T support(String mod, Supplier<F> expected, Supplier<S> fallback) {
    return FabricLoader.getInstance().isModLoaded(mod) ? expected.get() : fallback.get();
}
```

The method will check if Commander is installed and will pick the correct factory.

```java
public interface BooleanIntermediary {
    Function<Boolean, BooleanIntermediary> FACTORY = Support.support("commander",
            () -> b -> new CommanderBooleanIntermediary(BooleanExpression.constant(b)),
            () -> ConstantBooleanIntermediary::new);

    static BooleanIntermediary of(boolean value) {
        return FACTORY.apply(value);
    }
    //...
}
```

Now we can define expressions in our config!

```java
public class Config {
    public BooleanIntermediary value = BooleanIntermediary.of(true);
}
```

But wait, what about encoding/decoding? Let's actually get to that. Here we can create a codec for our delegates and use `support` to pick the correct one.

```java
public record ConstantBooleanIntermediary(boolean value) implements BooleanIntermediary {
    public static final Codec<ConstantBooleanIntermediary> CODEC = Codec.BOOL.xmap(ConstantBooleanIntermediary::new, ConstantBooleanIntermediary::value);
    //...
}

public final class CommanderBooleanIntermediary implements BooleanIntermediary {
    public static final Codec<CommanderBooleanIntermediary> CODEC = BooleanExpression.CODEC.xmap(CommanderBooleanIntermediary::new, CommanderBooleanIntermediary::getExpression);
    //...
}
```

Now we can use our codecs.

```java
Codec<BooleanIntermediary> codec = (Codec<BooleanIntermediary>) Support.fallback("commander", () -> CommanderBooleanIntermediary.CODEC, () -> ConstantBooleanIntermediary.CODEC);
```

### Using intermediaries in configs.

::: info Note

This section only applies if you use Gson to read/write your configs. 

If you use a third-party library and the library doesn't allow you to pass a custom Gson instance, you could modify it using mixins.
:::
In Gson you can provide custom JsonSerializers/JsonDeserializers, which allows us to encode the intermediary using Codecs.
Let's create our `CodecSerializer` class.

::: details Code

```java
public record CodecSerializer<C>(Codec<C> codec) implements JsonSerializer<C>, JsonDeserializer<C> {

    public static <C> CodecSerializer<C> of(Codec<C> codec) {
        return new CodecSerializer<>(codec);
    }

    @Override
    public C deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        var r = this.codec.parse(JsonOps.INSTANCE, json);
        if (r.error().isPresent()) throw new JsonParseException(r.error().orElseThrow().message());
        return r.result().orElseThrow();
    }

    @Override
    public JsonElement serialize(C src, Type typeOfSrc, JsonSerializationContext context) {
        var r = codec.encodeStart(JsonOps.INSTANCE, src);
        if (r.error().isPresent()) throw new IllegalStateException(r.error().orElseThrow().message());
        return r.result().orElseThrow();
    }
}
```

:::

And now we can register a type hierarchy adapter with our GsonBuilder.

```java
builder.registerTypeHierarchyAdapter(BooleanIntermediary.class, CodecSerializer.of(codec));
```

And we're done!
