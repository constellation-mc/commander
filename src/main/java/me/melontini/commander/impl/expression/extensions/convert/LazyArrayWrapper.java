package me.melontini.commander.impl.expression.extensions.convert;

import com.ezylang.evalex.data.EvaluationValue;
import me.melontini.commander.impl.expression.extensions.ProxyMap;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.IntFunction;

public class LazyArrayWrapper implements List<EvaluationValue> {

    private final IntFunction<Object> function;
    private final int size;

    public LazyArrayWrapper(IntFunction<Object> function, int size) {
        this.function = function;
        this.size = size;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @NotNull @Override
    public ListIterator<EvaluationValue> listIterator(int index) {
        return new ArrayIterator(size, index) {
            @Override
            protected EvaluationValue get(int index) {
                return ProxyMap.convert(LazyArrayWrapper.this.function.apply(index));
            }
        };
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (int i = 0; i < size; i++) {
            joiner.add(String.valueOf(function.apply(i)));
        }
        return joiner.toString();
    }

    @Override
    public EvaluationValue get(int index) {
        return ProxyMap.convert(this.function.apply(index));
    }

    @Override
    public boolean contains(Object o) {
        throw new IllegalStateException();
    }

    @NotNull @Override
    public Iterator<EvaluationValue> iterator() {
        return listIterator();
    }

    @NotNull @Override
    public Object[] toArray() {
        throw new IllegalStateException();
    }

    @NotNull @Override
    public <T> T[] toArray(@NotNull T[] a) {
        throw new IllegalStateException();
    }

    @Override
    public boolean add(EvaluationValue object) {
        throw new IllegalStateException();
    }

    @Override
    public boolean remove(Object o) {
        throw new IllegalStateException();
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        throw new IllegalStateException();
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends EvaluationValue> c) {
        throw new IllegalStateException();
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends EvaluationValue> c) {
        throw new IllegalStateException();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new IllegalStateException();
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new IllegalStateException();
    }

    @Override
    public void clear() {
        throw new IllegalStateException();
    }

    @Override
    public EvaluationValue set(int index, EvaluationValue element) {
        throw new IllegalStateException();
    }

    @Override
    public void add(int index, EvaluationValue element) {
        throw new IllegalStateException();
    }

    @Override
    public EvaluationValue remove(int index) {
        throw new IllegalStateException();
    }

    @Override
    public int indexOf(Object o) {
        throw new IllegalStateException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new IllegalStateException();
    }

    @NotNull @Override
    public ListIterator<EvaluationValue> listIterator() {
        return listIterator(0);
    }

    @NotNull @Override
    public List<EvaluationValue> subList(int fromIndex, int toIndex) {
        throw new IllegalStateException();
    }

    private abstract static class ArrayIterator implements ListIterator<EvaluationValue> {

        private final int size;
        private int position;

        public ArrayIterator(int size, int position) {
            this.size = size;
            this.position = position;
        }

        protected abstract EvaluationValue get(int index);

        @Override
        public boolean hasNext() {
            return position < size;
        }

        @Override
        public EvaluationValue next() {
            if (!hasNext()) throw new NoSuchElementException();
            return get(position++);
        }

        @Override
        public boolean hasPrevious() {
            return position > 0;
        }

        @Override
        public EvaluationValue previous() {
            if (!hasPrevious()) throw new NoSuchElementException();
            return get(position--);
        }

        @Override
        public int nextIndex() {
            return position;
        }

        @Override
        public int previousIndex() {
            return position - 1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(EvaluationValue object) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(EvaluationValue object) {
            throw new UnsupportedOperationException();
        }
    }
}
