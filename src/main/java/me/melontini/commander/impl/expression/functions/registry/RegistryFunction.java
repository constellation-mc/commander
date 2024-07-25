package me.melontini.commander.impl.expression.functions.registry;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;
import java.util.NoSuchElementException;
import me.melontini.commander.impl.expression.extensions.ReflectiveValueConverter;
import me.melontini.dark_matter.api.base.util.Exceptions;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

@FunctionParameter(name = "identifier")
public class RegistryFunction extends AbstractFunction {

  private final Registry<?> registry;

  public RegistryFunction(Registry<?> registry) {
    this.registry = registry;
  }

  @Override
  public EvaluationValue evaluate(
      EvaluationContext context, Token functionToken, EvaluationValue... par)
      throws EvaluationException {
    try {
      var id = Identifier.of(par[0].getStringValue());
      return ReflectiveValueConverter.convert(this.registry
          .getOrEmpty(id)
          .orElseThrow(() -> new NoSuchElementException(
              "No such %s: %s".formatted(registry.getKey().getValue(), id))));
    } catch (Exception e) {
      throw new EvaluationException(functionToken, Exceptions.unwrap(e).getMessage());
    }
  }
}
