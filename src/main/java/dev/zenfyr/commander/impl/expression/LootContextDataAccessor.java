package dev.zenfyr.commander.impl.expression;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.DataAccessorIfc;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.Token;
import dev.zenfyr.commander.api.expression.ExpressionLibrary;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;

public class LootContextDataAccessor implements DataAccessorIfc {

  private static final Map<ResourceLocation, Function<LootContext, Object>> overrides =
      Collections.unmodifiableMap(new Object2ReferenceOpenHashMap<>(Map.of(
          new ResourceLocation("level"), LootContext::getLevel,
          new ResourceLocation("luck"), LootContext::getLuck,
          new ResourceLocation("library"),
              context -> ExpressionLibrary.get(context.getLevel().getServer()))));
  // Cache the way we got the variable.
  // We use the same instance for all expressions, so this can help save some overhead.
  private final Map<String, Function<LootContext, EvaluationValue>> varCache =
      new Object2ReferenceOpenHashMap<>();

  @Override
  public EvaluationValue getData(String variable, Token token, EvaluationContext context)
      throws EvaluationException {
    var supplier = varCache.get(variable);
    if (supplier != null) return supplier.apply((LootContext) context.getContext());

    var r = ResourceLocation.read(variable);
    if (r.error().isPresent()) {
      throw new EvaluationException(token, r.error().orElseThrow().message());
    }

    var id = r.result().orElseThrow();
    var func = overrides.get(id);
    if (func != null) {
      varCache.put(
          variable,
          supplier = (lootContext) -> Expression.convertValue(func.apply(lootContext), context));
      return supplier.apply((LootContext) context.getContext());
    }

    var param = ExtractionTypes.getParameter(id);
    if (param == null) {
      varCache.put(variable, lootContext -> EvaluationValue.UNDEFINED);
      return EvaluationValue.UNDEFINED;
    }
    varCache.put(
        variable,
        supplier = (lootContext) -> {
          var object = lootContext.getParamOrNull(param);
          if (object == null) return EvaluationValue.UNDEFINED;
          return Expression.convertValue(object, context);
        });
    return supplier.apply((LootContext) context.getContext());
  }
}
