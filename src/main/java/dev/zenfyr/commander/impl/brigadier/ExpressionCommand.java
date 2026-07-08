package dev.zenfyr.commander.impl.brigadier;

import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.FunctionIfc;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JsonOps;
import dev.zenfyr.commander.api.expression.Expression;
import dev.zenfyr.commander.api.expression.ExpressionLibrary;
import dev.zenfyr.commander.impl.Commander;
import dev.zenfyr.commander.impl.expression.CmdEvalException;
import dev.zenfyr.commander.impl.expression.library.ExpressionLibraryLoader;
import dev.zenfyr.pulsar.api.loot.LootContextBuilder;
import java.math.BigDecimal;
import java.util.List;
import java.util.StringJoiner;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ExpressionCommand {

  private static final List<String> CASTS =
      List.of("int", "long", "float", "double", "boolean", "string");

  public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    var base = Commands.literal("cmd:eval").requires(source -> source.hasPermission(2));

    base.then(registerExpression(Commands.literal("expression")));
    base.then(registerLibrary(Commands.literal("library")));

    dispatcher.register(base);
  }

  public static ArgumentBuilder<CommandSourceStack, ?> registerExpression(
      ArgumentBuilder<CommandSourceStack, ?> builder) {
    var cmd = Commands.argument("expression", StringArgumentType.string())
        .executes(context ->
            executeInline(context, StringArgumentType.getString(context, "expression"), null));

    for (String cast : CASTS) {
      cmd.then(Commands.literal(cast)
          .executes(context ->
              executeInline(context, StringArgumentType.getString(context, "expression"), cast)));
    }

    return builder.then(cmd);
  }

  public static ArgumentBuilder<CommandSourceStack, ?> registerLibrary(
      ArgumentBuilder<CommandSourceStack, ?> builder) {
    var cmd = Commands.argument("expression", ResourceLocationArgument.id())
        .suggests((context, builder1) -> SharedSuggestionProvider.suggestResource(
            ExpressionLibrary.get(context.getSource().getServer())
                .allExpressions()
                .keySet(),
            builder1))
        .executes(context ->
            executeLibrary(context, ResourceLocationArgument.getId(context, "expression"), null));

    for (String cast : CASTS) {
      cmd.then(Commands.literal(cast)
          .executes(context -> executeLibrary(
              context, ResourceLocationArgument.getId(context, "expression"), cast)));
    }

    return builder.then(cmd);
  }

  private static int executeInline(
      CommandContext<CommandSourceStack> context, String expression, @Nullable String cast)
      throws CommandSyntaxException {
    var r = Expression.CODEC.parse(JsonOps.INSTANCE, new JsonPrimitive(expression));
    if (r.error().isPresent())
      throw Commander.EXPRESSION_EXCEPTION.create(r.error().get().message());

    return execute(context, r.result().orElseThrow(), cast);
  }

  private static int executeLibrary(
      CommandContext<CommandSourceStack> context, ResourceLocation location, @Nullable String cast)
      throws CommandSyntaxException {
    var library = ExpressionLibrary.get(context.getSource().getServer());
    var expression = library.getExpression(location);
    if (expression == null) throw ExpressionLibraryLoader.NO_EXPRESSION_EXCEPTION.create(location);

    return execute(context, expression, cast);
  }

  private static int execute(
      CommandContext<CommandSourceStack> context, Expression expression, @Nullable String cast)
      throws CommandSyntaxException {
    try {
      var lootContext = LootContextBuilder.command(
          context.getSource().getLevel(),
          builder -> builder
              .origin(context.getSource().getPosition())
              .thisEntity(context.getSource().getEntity()));
      String result = castResult(expression.eval(lootContext), cast);
      context.getSource().sendSystemMessage(Component.literal(result));
      return 1;
    } catch (CmdEvalException e) {
      throw Commander.EXPRESSION_EXCEPTION.create(e.getMessage());
    }
  }

  private static String castResult(Expression.Result value, String cast) {
    if (cast == null) return prettyString((EvaluationValue) (Object) value);

    return switch (cast) {
      case "int" -> Integer.toString((int) value.getAsLong());
      case "long" -> Long.toString(value.getAsLong());
      case "float" -> new BigDecimal((float) value.getAsDouble()).toPlainString();
      case "double" -> new BigDecimal(value.getAsDouble()).toPlainString();
      case "boolean" -> Boolean.toString(value.getAsBoolean());
      case "string" -> '"' + value.getAsString() + '"';
      default -> throw new CmdEvalException("Illegal cast '%s'".formatted(cast));
    };
  }

  private static String prettyString(EvaluationValue value) {
    return switch (value.getDataType()) {
      case NULL -> "null";
      case UNDEFINED -> "undefined";
      case STRING -> '"' + value.getStringValue() + '"';
      case BOOLEAN -> String.valueOf(value.getBooleanValue());
      case NUMBER -> value.getNumberValue().toPlainString();
      case DATE_TIME -> value.getDateTimeValue().toString();
      case DURATION -> value.getDurationValue().toString();
      case ARRAY -> {
        var joiner = new StringJoiner(", ");
        value.getArrayValue().forEach(value1 -> joiner.add(prettyString(value1)));
        yield "[" + joiner + "]";
      }
      case STRUCTURE -> {
        var joiner = new StringJoiner(", ");
        value
            .getStructureValue()
            .forEach((s, value1) -> joiner.add(s + ": " + prettyString(value1)));
        yield "{" + joiner + "}";
      }
      case EXPRESSION_NODE -> "Callable()";
      case BINARY -> {
        var value1 = value.getValue();
        if (value1 instanceof FunctionIfc) {
          yield "Function()";
        } else {
          yield "Obj(" + value1 + ")";
        }
      }
    };
  }
}
