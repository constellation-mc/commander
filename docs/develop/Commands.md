# Commands

In general, you should prefer to implement Brigadier `/` commands, but there are some cases where some additional flexibility is required.

## Creating commands

Creating new commands is easy, you'll have to implement the `Command` interface, create a [MapCodec](https://forge.gemwire.uk/wiki/Codecs) to serialize/deserialize the command and register the codec with `CommandTypes.register()`.

Let's create a simple command which will print a string to standard output.

```java
public record DummyCommand(String text) implements Command {

    @Override
    public boolean execute(EventContext context) {
        System.out.println(text());
        return true; //Return if execution was successful.
    }

    @Override
    public CommandType type() {
        return null;
    }
}
```

Now create a [Codec](https://forge.gemwire.uk/wiki/Codecs) for your command.

```java
public static final MapCodec<DummyCommand> CODEC = Codec.STRING.fieldOf("text").xmap(DummyCommand::new, DummyCommand::text);
```

With that done, we'll have to register the command to get our `CommandType`.

```java
public static final CommandType DUMMY = CommandType.register(new Identifier("modid", "print"), DummyCommand.CODEC);
```

Now return this type in `type()`.

```java
@Override
public CommandType type() {
    return MyModInit.DUMMY;
}
```

## EventContext

EventContext allows you to retrieve the `LootContext` which is passed with the event type.

```java
context.lootContext().getWorld(); //Returns a ServerWorld.

context.lootContext().get(LootContextParameters.TOOL); //Returns the prameter or null if not present.

context.lootContext().requireParameter(LootContextParameters.TOOL); //Returns the parameter or throws an exception if not present.
```