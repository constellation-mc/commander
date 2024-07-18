package me.melontini.commander.impl.expression.functions.registry;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.AbstractFunction;
import com.ezylang.evalex.functions.FunctionParameter;
import com.ezylang.evalex.parser.ASTNode;
import com.ezylang.evalex.parser.ParseException;
import com.ezylang.evalex.parser.Token;
import me.melontini.commander.impl.Commander;
import me.melontini.commander.impl.expression.extensions.ReflectiveValueConverter;
import me.melontini.commander.impl.expression.functions.CustomInlinerFunction;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.NoSuchElementException;

@FunctionParameter(name = "identifier")
public class DynamicRegistryFunction extends AbstractFunction implements CustomInlinerFunction {

    protected final RegistryKey<? extends Registry<?>> registry;

    public DynamicRegistryFunction(RegistryKey<? extends Registry<?>> registry) {
        this.registry = registry;
    }

    @Override
    public EvaluationValue evaluate(Expression expression, Token functionToken, EvaluationValue... par) throws EvaluationException {
        try {
            var id = new Identifier(par[0].getStringValue());
            return ReflectiveValueConverter.convert(Commander.get().currentServer().getRegistryManager().get(registry).getOrEmpty(id).orElseThrow(() -> new NoSuchElementException("No such %s: %s".formatted(registry.getValue(), id))));
        } catch (Exception e) {
            throw new EvaluationException(functionToken, e.getMessage());
        }
    }

    @Override
    public EvaluationValue cmd$inlineFunction(Expression expression, ASTNode node) throws ParseException, EvaluationException {
        return null;
    }
}
