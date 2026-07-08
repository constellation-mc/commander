package dev.zenfyr.commander.impl.expression;

import com.ezylang.evalex.BaseException;
import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.*;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.FunctionIfc;
import com.ezylang.evalex.functions.basic.*;
import com.ezylang.evalex.parser.ExpressionParser;
import com.ezylang.evalex.parser.solvables.Solvable;
import com.mojang.serialization.DataResult;
import dev.zenfyr.commander.impl.expression.adapter.*;
import dev.zenfyr.commander.impl.expression.conversion.ReflectiveValueConverter;
import dev.zenfyr.commander.impl.expression.function.*;
import dev.zenfyr.commander.impl.expression.function.ArrayOf;
import dev.zenfyr.commander.impl.expression.operator.SafeOrOperator;
import dev.zenfyr.pulsar.api.util.ExceptionUtil;
import dev.zenfyr.pulsar.api.util.functions.Memoize;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.util.*;
import java.util.function.Function;
import lombok.extern.log4j.Log4j2;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;

@Log4j2
public class EvalUtils {

  public static final ExpressionConfiguration CONFIGURATION;
  public static final ExpressionParser PARSER;

  static {
    CONFIGURATION = ExpressionConfiguration.builder()
        .dataAccessor(new LootContextDataAccessor())
        .evaluationValueConverter(new ReflectiveValueConverter())
        .singleQuoteStringLiteralsAllowed(true)
        .typeAdapterRegistry(BuiltInAdapters.init())
        .parameterMapSupplier(Object2ReferenceOpenHashMap::new)
        .defaultConstants(ConstantCollection.builder()
            .addConstant("true", EvaluationValue.TRUE)
            .addConstant("false", EvaluationValue.FALSE)
            .addConstant("null", EvaluationValue.NULL_VALUE)
            .addConstant("math", convertFunctions(FunctionContainers.initMath()))
            .addConstant("date", convertFunctions(FunctionContainers.initDateTime()))
            .addConstant("duration", convertFunctions(FunctionContainers.initDurations()))
            .build())
        .functionDictionary(FunctionDictionary.builder()
            .addFunction("not", new NotFunction())
            .addFunction("if", new IfFunction())
            .addFunction("ifMatches", new MatchesFunction())
            .addFunction("switch", new SwitchFunction())
            .addFunction("chain", new ChainFunction())
            .addFunction("len", new LengthFunction())
            .addFunction("structContainsKey", new StructContainsKeyFunction())
            .addFunction("hasContext", new HasContextFunction())
            .addFunction("array", new ArrayOf())
            .build())
        .operatorDictionary(OperatorDictionary.builder(
                ExpressionConfiguration.defaultConfiguration().getOperatorDictionary(),
                Object2ReferenceOpenHashMap::new)
            .addOperator("?", new SafeOrOperator())
            .build())
        .build();
    PARSER = new ExpressionParser(CONFIGURATION);
  }

  private static EvaluationValue convertFunctions(Map<String, FunctionIfc> container) {
    Map<String, EvaluationValue> map = new Object2ReferenceOpenHashMap<>();
    container.forEach((s, functionIfc) -> map.put(s, EvaluationValue.binaryValue(functionIfc)));
    return EvaluationValue.structureValue(map);
  }

  // Quickly evaluate a fake "lambda".
  public static EvaluationValue runLambda(
      EvaluationContext context, EvaluationValue value, Solvable predicate)
      throws EvaluationException {
    return predicate.solve(context.withParameter("it", value));
  }

  public static EvaluationValue evaluate(
      LootContext context, Expression exp, @Nullable Map<String, EvaluationValue> params) {
    try {
      var builder = EvaluationContext.builder(CONFIGURATION, exp).context(context);
      if (params != null && !params.isEmpty()) params.forEach(builder::parameter);
      return exp.solve(builder.build());
    } catch (BaseException e) {
      throw new CmdEvalException(
          Objects.requireNonNullElseGet(
              e.getMessage(),
              () -> "Failed to evaluate expression %s".formatted(exp.getExpressionString())),
          e.getCause());
    }
  }

  private static final Object CACHE_LOCK = new Object();
  private static Function<String, Expression> EXPRESSION_CACHE;

  static {
    resetCache();
  }

  public static void resetCache() {
    synchronized (CACHE_LOCK) {
      EXPRESSION_CACHE = Memoize.lruFunction(ExceptionUtil.function(PARSER::parse), 60);
    }
  }

  public static DataResult<Expression> parseExpression(String expression) {
    try {
      Expression result;
      synchronized (CACHE_LOCK) {
        result = EXPRESSION_CACHE.apply(expression);
      }
      return DataResult.success(result);
    } catch (Throwable throwable) {
      return DataResult.error(ExceptionUtil.unwrap(throwable)::getMessage);
    }
  }

  public static <T> ResourceLocation fromHolder(Holder<T> holder, Registry<T> registry) {
    if (holder.kind() == Holder.Kind.REFERENCE) {
      return holder.unwrapKey().orElseThrow().location();
    } else if (holder.isBound()) {
      return registry.getKey(holder.value());
    } else {
      return null;
    }
  }

  public static void init() {}
}
