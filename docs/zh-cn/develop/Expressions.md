# 表达式

正如在[表达式](/zh-cn/Expressions)，章节所介绍的那样，命令官模组引入了一个具有高度可拓展性的表达式系统。这个系统可以通过接口从外部调用。在这个页面中，我们将一起创建示例，并探索使表达式成为可选集成的方式。

## 直接使用表达式

内置的 `Expression` 类提供了将字符串转化为表达式的编码器，以及 `parse` 方法。一个表达式相当于 `LootContext -> Expression.Result` 的函数。`Expression.Result` 代表可以被转化为 `BigDecimal`（高精度小数），`boolean`（布尔型），`String`（字符串），`Instant`（时间戳）和 `Duration`（持续时间）的返回值。

在应用表达表达式函数前，你必须提供 `LootContext` 的实例。

```java
public static final Expression EXP = Expression.parse("strFormat('%02.0f:%02.0f', floor((level.getDayTime / 1000 + 8) % 24), floor(60 * (level.getDayTime % 1000) / 1000))").result().orElseThrow();

public String worldTimeInHumanTime(LootContext context) {
    return EXP.apply(context).getAsString();
}
```

## 特殊函数

命令官模组附带了 `Arithmetica` 和 `BooleanExpression`。作为 `double` 和 `boolean` 函数，它们可以被编码为常量或表达式。值得注意的是，`"true"` 会被当成表达式解码，而 `true` 不会。

```json
2.2, "random(0, 3)" //Arithmetica（算术）

true, "level.isDay" //BooleanExpression（布尔型表达式）
```

## 让表达式作为可选集成。

在实现对命令官模组的支持时，你可能希望让这种集成是非强制的。最简单的方法是，创建一个通用接口，然后委托给其中一个实现。实际上，这就是表达式在群星模组的新配置系统的配置方式。

让我们从定义一个简单的布尔型中介接口开始、我建议使用 supplier 作为形式参数，因为这样就不用为常量构建 `LootContext` 了。

```java
public interface BooleanIntermediary {
    boolean asBoolean(Supplier<LootContext> supplier);
}
```

接下来，让我们实现常量委托。
```java
public record ConstantBooleanIntermediary(boolean value) implements BooleanIntermediary {

    @Override
    public boolean asBoolean(Supplier<LootContext> supplier) {
        return this.value;
    }
}
```

以及命令官委托。

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

委托做好后，接下来，我们需要动态地选择其中一个。让我们回到中介接口，为常量值创建一个 factory（工厂）。在这里，我用的是 Dark Matter 的 `Support`，你也可以直接复制这个方法：

```java
public static <T, F extends T, S extends T> T support(String mod, Supplier<F> expected, Supplier<S> fallback) {
    return FabricLoader.getInstance().isModLoaded(mod) ? expected.get() : fallback.get();
}
```

这个方法将检查是否命令官模组已被安装，并选择正确的工厂。

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

很棒，现在我们可以在配置文件中定义表达式了！

```java
public class Config {
    public BooleanIntermediary value = BooleanIntermediary.of(true);
}
```

等等，编码和解码该怎么办？这正是我们接下来要了解的。在这里，我们可以为我们的委托创建一个编解码器，并使用 `support` 来进行正确选择。

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

现在可以使用我们的编解码器了。

```java
Codec<BooleanIntermediary> codec = (Codec<BooleanIntermediary>) Support.fallback("commander", () -> CommanderBooleanIntermediary.CODEC, () -> ConstantBooleanIntermediary.CODEC);
```

### 在配置文件中使用中介。

::: info 提示

本节只在你使用 Gson 来读写配置文件时有用。

如果你使用了第三方库，且该库不支持传递自定义 Gson 示例，你可以通过 mixin 来修改它。
:::
在 Gson 中，你可以通过提供自定义 JsonSerializers（Json 序列化器）或 JsonDeserializers（Json 反序列化器），使用编解码器来对中介进行编码。
接下来将创建我们的 `CodecSerializer` 类。

::: details 编解码器
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

现在可以通过我们的 GsonBuilder 来注册类型层次适配器了。

```java
builder.registerTypeHierarchyAdapter(BooleanIntermediary.class, CodecSerializer.of(codec));
```

大功告成！
