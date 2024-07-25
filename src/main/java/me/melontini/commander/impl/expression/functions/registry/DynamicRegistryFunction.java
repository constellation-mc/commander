package me.melontini.commander.impl.expression.functions.registry;

import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.ASTNode;
import com.ezylang.evalex.parser.Token;
import java.util.List;
import java.util.NoSuchElementException;
import me.melontini.commander.impl.expression.extensions.ReflectiveValueConverter;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@FunctionParameter(name = "identifier")
public class DynamicRegistryFunction extends AbstractFunction {

  protected final RegistryKey<? extends Registry<?>> registry;

  public DynamicRegistryFunction(RegistryKey<? extends Registry<?>> registry) {
    this.registry = registry;
  }

  @Override
  public EvaluationValue evaluate(
      EvaluationContext context, Token functionToken, EvaluationValue... par)
      throws EvaluationException {
    try {
      var id = new Identifier(par[0].getStringValue());
      var server = ((LootContext) context.context()[0]).getWorld().getServer();
      return ReflectiveValueConverter.convert(server
          .getRegistryManager()
          .get(registry)
          .getOrEmpty(id)
          .orElseThrow(() ->
              new NoSuchElementException("No such %s: %s".formatted(registry.getValue(), id))));
    } catch (Exception e) {
      throw new EvaluationException(functionToken, e.getMessage());
    }
  }

  @Override
  public @Nullable EvaluationValue inlineFunction(
      Expression expression, Token token, List<ASTNode> parameters) throws EvaluationException {
    return null;
  }
}
