package me.melontini.commander.impl.expression;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.DataAccessorIfc;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.parser.Token;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import me.melontini.commander.api.expression.ExpressionLibrary;
import me.melontini.commander.impl.event.data.types.ExtractionTypes;
import me.melontini.commander.impl.expression.extensions.ReflectiveValueConverter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.Identifier;

public class LootContextDataAccessor implements DataAccessorIfc {

  private static final Map<Identifier, Function<LootContext, Object>> overrides =
      Collections.unmodifiableMap(new Object2ReferenceOpenHashMap<>(Map.of(
          new Identifier("level"), LootContext::getWorld,
          new Identifier("luck"), LootContext::getLuck,
          new Identifier("library"),
              context -> ExpressionLibrary.get(context.getWorld().getServer()))));
  // We use the same instance for all expressions, so this can help save some overhead.
  private final Map<String, Function<LootContext, EvaluationValue>> varCache =
      new Object2ReferenceOpenHashMap<>();

  @Override
  public EvaluationValue getData(String variable, Token token, EvaluationContext context)
      throws EvaluationException {
    var supplier = varCache.get(variable);
    if (supplier != null)
      return supplier.apply(
          (LootContext) context.context()[0]);

    var r = Identifier.validate(variable);
    if (r.error().isPresent()) {
      throw new EvaluationException(token, r.error().orElseThrow().message());
    }

    var id = r.result().orElseThrow();
    var func = overrides.get(id);
    if (func != null) {
      varCache.put(
          variable,
          supplier = (lootContext) -> ReflectiveValueConverter.convert(func.apply(lootContext)));
      return supplier.apply((LootContext) context.context()[0]);
    }

    var param = ExtractionTypes.getParameter(id);
    if (param == null) {
      throw new EvaluationException(
          token, "%s is not a registered loot context parameter!".formatted(id));
    }
    varCache.put(
        variable,
        supplier = (lootContext) -> {
          var object = lootContext.get(param);
          if (object == null) return null;
          return ReflectiveValueConverter.convert(object);
        });
    return supplier.apply((LootContext) context.context()[0]);
  }
}
