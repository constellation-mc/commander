package me.melontini.commander.impl.expression;

import com.ezylang.evalex.BaseException;
import com.ezylang.evalex.EvaluationContext;
import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.config.ExpressionConfiguration;
import com.ezylang.evalex.data.DataAccessorIfc;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.data.types.BooleanValue;
import com.ezylang.evalex.data.types.NullValue;
import com.ezylang.evalex.data.types.NumberValue;
import com.ezylang.evalex.data.types.StringValue;
import com.ezylang.evalex.parser.ASTNode;
import com.ezylang.evalex.parser.ExpressionParser;
import com.ezylang.evalex.parser.InlinedASTNode;
import com.ezylang.evalex.parser.Token;
import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMaps;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import lombok.extern.log4j.Log4j2;
import me.melontini.commander.api.expression.extensions.ProxyMap;
import me.melontini.commander.impl.event.data.types.ExtractionTypes;
import me.melontini.commander.impl.expression.extensions.ReflectiveValueConverter;
import me.melontini.commander.impl.expression.functions.*;
import me.melontini.commander.impl.expression.functions.arrays.*;
import me.melontini.commander.impl.expression.functions.math.ClampFunction;
import me.melontini.commander.impl.expression.functions.math.LerpFunction;
import me.melontini.commander.impl.expression.functions.math.RangedRandomFunction;
import me.melontini.commander.impl.expression.functions.registry.DynamicRegistryFunction;
import me.melontini.commander.impl.expression.functions.registry.DynamicRegistryRegistryFunction;
import me.melontini.commander.impl.expression.functions.registry.RegistryFunction;
import me.melontini.commander.impl.util.ThrowingOptional;
import me.melontini.dark_matter.api.base.util.Exceptions;
import me.melontini.dark_matter.api.base.util.functions.Memoize;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Function;

@Log4j2
public class EvalUtils {

    public static final ExpressionConfiguration CONFIGURATION;
    public static final ExpressionParser PARSER;
    public static final Object2ReferenceMap<String, EvaluationValue> CONSTANTS = Object2ReferenceMaps.unmodifiable(new Object2ReferenceOpenHashMap<>(ImmutableMap.of(
            "true", BooleanValue.TRUE,
            "false", BooleanValue.FALSE,
            "PI", NumberValue.of(new BigDecimal("3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679")),
            "E", NumberValue.of(new BigDecimal("2.71828182845904523536028747135266249775724709369995957496696762772407663")),
            "null", NullValue.of(),
            "DT_FORMAT_ISO_DATE_TIME", StringValue.of("yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]['['VV']']"),
            "DT_FORMAT_LOCAL_DATE_TIME", StringValue.of("yyyy-MM-dd'T'HH:mm:ss[.SSS]"),
            "DT_FORMAT_LOCAL_DATE", StringValue.of("yyyy-MM-dd")
    )));

    static {
        var builder = ExpressionConfiguration.builder()
                .dataAccessorSupplier(LootContextDataAccessor::new)
                .parameterMapSupplier(Object2ReferenceOpenHashMap::new)
                .evaluationValueConverter(new ReflectiveValueConverter())
                .allowOverwriteConstants(false)
                .additionalAllowedIdentifierChars(new char[] {':'})
                .singleQuoteStringLiteralsAllowed(true)
                .mathContext(MathContext.DECIMAL64)
                .constants(CONSTANTS);

        Set<String> toCache = ImmutableSet.of(
                "fact", "sqrt",
                "acos", "acosh", "acosr", "acot", "acoth", "acotr", "asin", "asinh", "asinr",
                "atan", "atan2", "atan2r", "atanh", "atanr", "cosh", "cosr", "cot", "coth", "cotr",
                "csc", "csch", "cscr", "sinh", "sinr", "sec", "sech", "secr", "tanh", "tanr",
                "strContains", "strEndsWith", "strLower", "strStartsWith", "strTrim", "strUpper"
        );

        var fd = ExpressionConfiguration.defaultConfiguration().getFunctionDictionary();
        var dictionary = fd.toBuilder(Object2ReferenceOpenHashMap::new);
        fd.forEach((string, functionIfc) -> {
            var newName = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, string);
            dictionary.add(newName, toCache.contains(newName) ? LruCachingFunction.of(functionIfc) : functionIfc);
        });

        dictionary.add("random", new RangedRandomFunction());
        dictionary.add("lerp", new LerpFunction());
        dictionary.add("clamp", new ClampFunction());
        dictionary.add("ifMatches", new MatchesFunction());
        dictionary.add("chain", new ChainFunction());
        dictionary.add("length", new LengthFunction());

        dictionary.add("arrayOf", new ArrayOf());
        dictionary.add("arrayMap", new ArrayMap());
        dictionary.add("arrayFind", new ArrayFind());
        dictionary.add("arrayFindAny", new ArrayFindAny());
        dictionary.add("arrayFindFirst", new ArrayFindFirst());
        dictionary.add("arrayAnyMatch", new ArrayAnyMatch());
        dictionary.add("arrayNoneMatch", new ArrayNoneMatch());
        dictionary.add("arrayAllMatch", new ArrayAllMatch());

        dictionary.add("structContainsKey", new StructContainsKeyFunction());
        dictionary.add("hasContext", new HasContextFunction());

        dictionary.add("Registry", new RegistryFunction(Registries.REGISTRIES));
        dictionary.add("Item", LruCachingFunction.of(new RegistryFunction(Registries.ITEM)));
        dictionary.add("Block", LruCachingFunction.of(new RegistryFunction(Registries.BLOCK)));

        dictionary.add("DynamicRegistry", new DynamicRegistryRegistryFunction());
        dictionary.add("Biome", new DynamicRegistryFunction(RegistryKeys.BIOME));
        dictionary.add("DimensionType", new DynamicRegistryFunction(RegistryKeys.DIMENSION_TYPE));
        builder.functionDictionary(dictionary.build());

        CONFIGURATION = builder.build();
        PARSER = new ExpressionParser(CONFIGURATION);
    }

    public static String prettyToString(EvaluationValue value) {
        if (value.isNumberValue()) return '\'' + value.getStringValue() + '\'';
        if (value.isBooleanValue()) return value.getBooleanValue() ? "true" : "false";
        if (value.isStringValue()) return value.getStringValue();

        if (value.isArrayValue()) {
            StringJoiner joiner = new StringJoiner(", ", "[", "]");
            value.getArrayValue().forEach(value1 -> joiner.add(prettyToString(value1)));
            return joiner.toString();
        }

        if (value.isStructureValue()) {
            var map = value.getStructureValue();
            //TODO consider a DataAccessor type to avoid posing as structures.
            if ((Object) map instanceof ProxyMap) return String.valueOf(map); //Those do not implement any itr methods.
            StringJoiner joiner = new StringJoiner(", ", "{", "}");
            map.forEach((string, value1) -> joiner.add(string + "=" + prettyToString(value1)));
            return joiner.toString();
        }

        if (value.isNullValue()) return "null";
        return Objects.toString(value.getValue());
    }

    public static ThrowingOptional<EvaluationValue> valueOrEmpty(ASTNode node) {
        if (node instanceof InlinedASTNode inlined) return ThrowingOptional.of(inlined.value());
        return ThrowingOptional.empty();
    }

    public static EvaluationValue runLambda(EvaluationContext context, EvaluationValue value, ASTNode predicate) throws EvaluationException {
        return context.expression().evaluateSubtree(predicate, context.withParameter("it", value));
    }

    public static EvaluationValue evaluate(LootContext context, Expression exp, @Nullable Map<String, Object> params) {
        try {
            var builder = EvaluationContext.builder(exp).context(context);
            if (params != null && !params.isEmpty()) builder.parameters(params);
            return exp.evaluate(builder.build());
        } catch (BaseException e) {
            throw new CmdEvalException(Objects.requireNonNullElseGet(e.getMessage(), () -> "Failed to evaluate expression %s".formatted(exp.getExpressionString())), e);
        }
    }

    private static final Object CACHE_LOCK = new Object();
    private static Function<String, Expression> EXPRESSION_CACHE;

    static {
        resetCache();
    }

    public static void resetCache() {
        synchronized (CACHE_LOCK) {
            EXPRESSION_CACHE = Memoize.lruFunction(Exceptions.function(PARSER::parseAndInline), 60);
        }
    }

    public static DataResult<Expression> parseExpression(String expression) {
        try {
            Expression result;
            synchronized (CACHE_LOCK) {
                result = EXPRESSION_CACHE.apply(expression);
            }
            return DataResult.success(result.copy());
        } catch (Throwable throwable) {
            var unwrapped = Exceptions.unwrap(throwable);
            return DataResult.error(unwrapped::getMessage);
        }
    }

    public static class LootContextDataAccessor implements DataAccessorIfc {

        private static final Map<Identifier, Function<LootContext, Object>> overrides = new Object2ReferenceOpenHashMap<>(Map.of(
                new Identifier("level"), LootContext::getWorld,
                new Identifier("luck"), LootContext::getLuck
        ));
        //In most cases the expression is reused, so caching this helps us avoid some big overhead.
        private final Map<String, Function<LootContext, EvaluationValue>> varCache = new Object2ReferenceOpenHashMap<>();

        @Override
        public EvaluationValue getData(String variable, Token token, EvaluationContext context) throws EvaluationException {
            var supplier = varCache.get(variable);
            if (supplier != null) return supplier.apply((LootContext) context.context()[0]); //Parameters are cached by setData, so this is fine.

            var r = Identifier.validate(variable);
            if (r.error().isPresent()) {
                throw new EvaluationException(token, "%s - no such variable or %s".formatted(variable, r.error().orElseThrow().message()));
            }

            var id = r.result().orElseThrow();
            var func = overrides.get(id);
            if (func != null) {
                varCache.put(variable, supplier = (lootContext) -> ReflectiveValueConverter.convert(func.apply(lootContext)));
                return supplier.apply((LootContext) context.context()[0]);
            }

            var param = ExtractionTypes.getParameter(id);
            if (param == null) {
                throw new EvaluationException(token, "%s is not a registered loot context parameter, variable or override!".formatted(id));
            }
            varCache.put(variable, supplier = (lootContext) -> {
                var object = lootContext.get(param);
                if (object == null) return null;
                return ReflectiveValueConverter.convert(object);
            });
            return supplier.apply((LootContext) context.context()[0]);
        }
    }

    public static void init() {
    }
}
