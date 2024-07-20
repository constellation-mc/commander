package me.melontini.commander.impl.expression.functions;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.ezylang.evalex.functions.FunctionIfc;
import com.ezylang.evalex.functions.FunctionParameterDefinition;
import com.ezylang.evalex.parser.Token;

import java.util.*;
import java.util.function.Function;

public class LruCachingFunction implements FunctionIfc {

    public static FunctionIfc of(FunctionIfc delegate) {
        return new LruCachingFunction(delegate);
    }

    private final FunctionIfc delegate;
    private final Function<EvaluationValue[], Object> keyGetter;
    private final Map<Object, EvaluationValue> cache = Collections.synchronizedMap(new LinkedHashMap<>(128 + 1, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Object, EvaluationValue> eldest) {
            return size() > 128;
        }
    });

    public LruCachingFunction(FunctionIfc delegate) {
        this.delegate = delegate;

        if (this.delegate.getFunctionParameterDefinitions().size() == 1) {
            this.keyGetter = vals -> vals[0].getValue();
        } else {
            this.keyGetter = ArrayEqualityWrapper::new;
        }
    }

    @Override
    public List<FunctionParameterDefinition> getFunctionParameterDefinitions() {
        return delegate.getFunctionParameterDefinitions();
    }

    @Override
    public EvaluationValue evaluate(Expression expression, Token functionToken, EvaluationValue... parameterValues) throws EvaluationException {
        var key = this.keyGetter.apply(parameterValues);
        var r = this.cache.get(key);
        if (r == null) {
            r = delegate.evaluate(expression, functionToken, parameterValues);
            this.cache.put(key, r);
            return r;
        }
        return r;
    }

    @Override
    public void validatePreEvaluation(Token token, EvaluationValue... parameterValues) throws EvaluationException {
        delegate.validatePreEvaluation(token, parameterValues);
    }

    @Override
    public boolean hasVarArgs() {
        return delegate.hasVarArgs();
    }

    @Override
    public boolean isParameterLazy(int parameterIndex) {
        return delegate.isParameterLazy(parameterIndex);
    }

    @Override
    public int getCountOfNonVarArgParameters() {
        return delegate.getCountOfNonVarArgParameters();
    }

    private record ArrayEqualityWrapper(EvaluationValue[] arr) {

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ArrayEqualityWrapper o)) return false;
            return Arrays.equals(arr, o.arr);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(arr);
        }
    }
}
