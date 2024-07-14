package me.melontini.commander.impl.expression.functions.registry;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.Token;
import me.melontini.commander.impl.expression.extensions.ProxyMap;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.NoSuchElementException;

@FunctionParameter(name = "identifier")
public class RegistryFunction extends AbstractFunction {

    private final Registry<?> registry;

    public RegistryFunction(Registry<?> registry) {
        this.registry = registry;
    }

    @Override
    public EvaluationValue evaluate(Expression expression, Token functionToken, EvaluationValue... par) throws EvaluationException {
        try {
            var id = new Identifier(par[0].getStringValue());
            return ProxyMap.convert(this.registry.getOrEmpty(id).orElseThrow(() -> new NoSuchElementException("No such %s: %s".formatted(registry.getKey().getValue(), id))));
        } catch (Exception e) {
            throw new EvaluationException(functionToken, e.getMessage());
        }
    }
}
