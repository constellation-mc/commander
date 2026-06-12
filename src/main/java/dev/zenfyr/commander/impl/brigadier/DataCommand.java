package dev.zenfyr.commander.impl.brigadier;

import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.JsonOps;
import dev.zenfyr.commander.api.expression.Expression;
import dev.zenfyr.commander.api.expression.ExpressionLibrary;
import dev.zenfyr.commander.impl.Commander;
import dev.zenfyr.commander.impl.expression.CmdEvalException;
import dev.zenfyr.commander.impl.expression.library.ExpressionLibraryLoader;
import dev.zenfyr.commander.impl.util.DataTarget;
import dev.zenfyr.pulsar.loot.LootContextBuilder;
import dev.zenfyr.pulsar.util.MakeSure;
import dev.zenfyr.pulsar.util.TextUtil;
import java.util.Locale;
import java.util.function.BiFunction;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;

public class DataCommand {

  private static final DynamicCommandExceptionType NO_BE_EXCEPTION =
      new DynamicCommandExceptionType(
          (a) -> TextUtil.literal("No block entity at position %s".formatted(a)));
  private static final DynamicCommandExceptionType NO_KEY_EXCEPTION =
      new DynamicCommandExceptionType(
          (a) -> TextUtil.literal("No such key '%s' in target".formatted(a)));
  private static final SimpleCommandExceptionType WRONG_DATA_TYPE_EXCEPTION =
      new SimpleCommandExceptionType(TextUtil.literal("Nbt element must be a string or a number!"));

  public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    var cmd = Commands.literal("cmd:data").requires(source -> source.hasPermission(2));

    attachCommand(
        cmd,
        "read",
        (target, builder) -> builder.executes(context -> readValue(
            context.getSource(),
            toTarget(context, target),
            StringArgumentType.getString(context, "key"))));

    attachWrite(cmd);

    attachCommand(
        cmd,
        "remove",
        (target, builder) -> builder.executes(context -> removeValue(
            context.getSource(),
            toTarget(context, target),
            StringArgumentType.getString(context, "key"))));

    dispatcher.register(cmd);
  }

  private static void attachWrite(LiteralArgumentBuilder<CommandSourceStack> base) {
    var command = Commands.literal("write");
    String[] casts = new String[] {"long", "double", "string"};

    for (DataTarget value : DataTarget.values()) {
      var targetArg = Commands.literal(value.name().toLowerCase(Locale.ROOT));

      ArgumentBuilder<CommandSourceStack, ?> args = getArgs(value);

      var valArg = Commands.literal("value")
          .then(Commands.argument("key", StringArgumentType.string())
              .then(Commands.argument("data", NbtTagArgument.nbtTag())
                  .executes(context -> writeValue(
                      context,
                      value,
                      StringArgumentType.getString(context, "key"),
                      NbtTagArgument.getNbtTag(context, "data")))));
      targetArg.then(args != null ? args.then(valArg) : valArg);

      for (String cast : casts) {
        var expArg = Commands.literal("expression")
            .then(Commands.argument("key", StringArgumentType.string())
                .then(Commands.argument("expression", StringArgumentType.string())
                    .then(Commands.literal(cast)
                        .executes(context -> writeWithInline(
                            context,
                            value,
                            StringArgumentType.getString(context, "key"),
                            StringArgumentType.getString(context, "expression"),
                            cast)))));
        targetArg.then(args != null ? args.then(expArg) : expArg);

        var libArg = Commands.literal("library")
            .then(Commands.argument("key", StringArgumentType.string())
                .then(Commands.argument("data", ResourceLocationArgument.id())
                    .then(Commands.literal(cast)
                        .executes(context -> writeWithLibrary(
                            context,
                            value,
                            StringArgumentType.getString(context, "key"),
                            ResourceLocationArgument.getId(context, "data"),
                            cast)))));
        targetArg.then(args != null ? args.then(libArg) : libArg);
      }

      command.then(targetArg);
    }
    base.then(command);
  }

  private static void attachCommand(
      LiteralArgumentBuilder<CommandSourceStack> base,
      String name,
      BiFunction<
              DataTarget,
              ArgumentBuilder<CommandSourceStack, ?>,
              ArgumentBuilder<CommandSourceStack, ?>>
          keyAttachment) {
    var command = Commands.literal(name);
    for (DataTarget value : DataTarget.values()) {
      ArgumentBuilder<CommandSourceStack, ?> keyArg =
          keyAttachment.apply(value, Commands.argument("key", StringArgumentType.string()));

      ArgumentBuilder<CommandSourceStack, ?> args = getArgs(value);
      if (args != null) args.then(keyArg);
      else args = keyArg;

      command.then(Commands.literal(value.name().toLowerCase(Locale.ROOT)).then(args));
    }
    base.then(command);
  }

  private static int removeValue(CommandSourceStack source, AttachmentTarget target, String key) {
    var nbt = target.getAttachedOrCreate(Commander.DATA_ATTACHMENT);
    if (nbt.contains(key)) {
      nbt.remove(key);
      source.sendSuccess(
          () -> TextUtil.literal("Removed key '%s' from target.".formatted(key)), false);
      return 1;
    }
    source.sendFailure(TextUtil.literal("Target has no '%s' key!".formatted(key)));
    return 0;
  }

  private static int writeWithInline(
      CommandContext<CommandSourceStack> context,
      DataTarget dataTarget,
      String key,
      String expression,
      String cast)
      throws CommandSyntaxException {
    var r = Expression.CODEC.parse(JsonOps.INSTANCE, new JsonPrimitive(expression));
    if (r.error().isPresent())
      throw Commander.EXPRESSION_EXCEPTION.create(r.error().get().message());

    return writeWithExpression(context, dataTarget, key, r.result().orElseThrow(), cast);
  }

  private static int writeWithLibrary(
      CommandContext<CommandSourceStack> context,
      DataTarget dataTarget,
      String key,
      ResourceLocation location,
      String cast)
      throws CommandSyntaxException {
    var library = ExpressionLibrary.get(context.getSource().getServer());
    var expression = library.getExpression(location);
    if (expression == null) throw ExpressionLibraryLoader.NO_EXPRESSION_EXCEPTION.create(location);

    return writeWithExpression(context, dataTarget, key, expression, cast);
  }

  private static int writeWithExpression(
      CommandContext<CommandSourceStack> context,
      DataTarget dataTarget,
      String key,
      Expression expression,
      String cast)
      throws CommandSyntaxException {
    try {
      var lootContext = LootContextBuilder.command(
          context.getSource().getLevel(),
          builder -> builder
              .origin(context.getSource().getPosition())
              .thisEntity(context.getSource().getEntity()));
      Tag result = castResult(expression.eval(lootContext), cast);
      return writeValue(context, dataTarget, key, result);
    } catch (CmdEvalException e) {
      throw Commander.EXPRESSION_EXCEPTION.create(e.getMessage());
    }
  }

  private static Tag castResult(Expression.Result result, String cast) {
    MakeSure.notNull(cast);

    return switch (cast) {
      case "long" -> LongTag.valueOf(result.getAsLong());
      case "double" -> DoubleTag.valueOf(result.getAsDouble());
      case "string" -> StringTag.valueOf(result.getAsString());
      default -> throw new CmdEvalException("Illegal cast '%s'".formatted(cast));
    };
  }

  public static int writeValue(
      CommandContext<CommandSourceStack> context, DataTarget dataTarget, String key, Tag element)
      throws CommandSyntaxException {
    if (element instanceof NumericTag || element instanceof StringTag) {
      var target = toTarget(context, dataTarget);
      target.getAttachedOrCreate(Commander.DATA_ATTACHMENT).put(key, element);
      finishWrite(dataTarget, target);
      context
          .getSource()
          .sendSuccess(
              () -> TextUtil.literal("Wrote '%s=%s' to target!".formatted(key, element)), false);
      return 1;
    }
    throw WRONG_DATA_TYPE_EXCEPTION.create();
  }

  public static int readValue(CommandSourceStack source, AttachmentTarget target, String key)
      throws CommandSyntaxException {
    var r = target.getAttachedOrCreate(Commander.DATA_ATTACHMENT).get(key);
    if (r == null) {
      throw NO_KEY_EXCEPTION.create(key);
    }
    source.sendSuccess(() -> TextUtil.literal(r.getAsString()), false);
    return 1;
  }

  private static ArgumentBuilder<CommandSourceStack, ?> getArgs(DataTarget target) {
    return switch (target) {
      case LEVEL -> null;
      case CHUNK, BLOCK_ENTITY -> Commands.argument("position", BlockPosArgument.blockPos());
      case ENTITY -> Commands.argument("entity", EntityArgument.entity());
    };
  }

  private static AttachmentTarget toTarget(
      CommandContext<CommandSourceStack> context, DataTarget target) throws CommandSyntaxException {
    return switch (target) {
      case LEVEL -> context.getSource().getLevel();
      case CHUNK ->
        context.getSource().getLevel().getChunk(BlockPosArgument.getBlockPos(context, "position"));
      case ENTITY -> EntityArgument.getEntity(context, "entity");
      case BLOCK_ENTITY -> {
        var pos = BlockPosArgument.getBlockPos(context, "position");
        var be = context.getSource().getLevel().getBlockEntity(pos);
        if (be == null)
          throw NO_BE_EXCEPTION.create(
              "[%s, %s, %s]".formatted(pos.getX(), pos.getY(), pos.getZ()));
        yield be;
      }
    };
  }

  private static void finishWrite(DataTarget target, AttachmentTarget attachmentTarget) {
    switch (target) {
      case LEVEL, ENTITY -> {}
      case BLOCK_ENTITY -> ((BlockEntity) attachmentTarget).setChanged();
      case CHUNK -> ((ChunkAccess) attachmentTarget).setUnsaved(true);
    }
  }
}
