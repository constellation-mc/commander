# 命令

大多情况下，你应该使用 `/` 类型的命令，但有时，我们会需要一些额外的发挥空间。

## 创建命令

创建新的命令很简单，你要做的是：实现 `Command` 接口，通过创建一个[映射编码器](https://forge.gemwire.uk/wiki/Codecs)来序列化或反序列化命令，然后用 `CommandTypes.register()` 来注册编码器。

让我们创建一个能够将字符串转化为标准输出的命令。

```java
public record DummyCommand(String text) implements Command {

    @Override
    public boolean execute(EventContext context) {
        System.out.println(text());
        return true; //执行成功则返回
    }

    @Override
    public CommandType type() {
        return null;
    }
}
```

接下来，我们再为这个命令[编码器](https://forge.gemwire.uk/wiki/Codecs)创建一个编码器。

```java
public static final MapCodec<DummyCommand> CODEC = Codec.STRING.fieldOf("text").xmap(DummyCommand::new, DummyCommand::text);
```

再然后，我们需要注册这一命令来获取 `CommandType`。

```java
public static final CommandType DUMMY = CommandType.register(new Identifier("modid", "print"), DummyCommand.CODEC);
```

最后，在 `type()` 返回这个类型。

```java
@Override
public CommandType type() {
    return MyModInit.DUMMY;
}
```

## 事件情境

你可以通过事件情境，检索与事件类型一起传递的 `LootContext`。

```java
context.lootContext().getWorld(); //返回服务端世界

context.lootContext().get(LootContextParameters.TOOL); //如果不存在，返回参数或空值。

context.lootContext().requireParameter(LootContextParameters.TOOL); //如果不存在，返回参数或抛出异常。
```
