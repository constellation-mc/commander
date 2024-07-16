package me.melontini.commander.impl.expression.extensions.convert;

import me.melontini.commander.api.expression.Expression;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.IntFunction;

public class LazyArrayWrapper implements List<Expression.Result> {

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
    public ListIterator<Expression.Result> listIterator(int index) {
        return new ArrayIterator(size, index) {
            @Override
            protected Expression.Result get(int index) {
                return Expression.Result.convert(LazyArrayWrapper.this.function.apply(index));
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
    public Expression.Result get(int index) {
        return Expression.Result.convert(this.function.apply(index));
    }

    @Override
    public boolean contains(Object o) {
        throw new IllegalStateException();
    }

    @NotNull @Override
    public Iterator<Expression.Result> iterator() {
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
    public boolean add(Expression.Result object) {
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
    public boolean addAll(@NotNull Collection<? extends Expression.Result> c) {
        throw new IllegalStateException();
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends Expression.Result> c) {
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
    public Expression.Result set(int index, Expression.Result element) {
        throw new IllegalStateException();
    }

    @Override
    public void add(int index, Expression.Result element) {
        throw new IllegalStateException();
    }

    @Override
    public Expression.Result remove(int index) {
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
    public ListIterator<Expression.Result> listIterator() {
        return listIterator(0);
    }

    @NotNull @Override
    public List<Expression.Result> subList(int fromIndex, int toIndex) {
        throw new IllegalStateException();
    }

    private abstract static class ArrayIterator implements ListIterator<Expression.Result> {

        private final int size;
        private int position;

        public ArrayIterator(int size, int position) {
            this.size = size;
            this.position = position;
        }

        protected abstract Expression.Result get(int index);

        @Override
        public boolean hasNext() {
            return position < size;
        }

        @Override
        public Expression.Result next() {
            if (!hasNext()) throw new NoSuchElementException();
            return get(position++);
        }

        @Override
        public boolean hasPrevious() {
            return position > 0;
        }

        @Override
        public Expression.Result previous() {
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
        public void set(Expression.Result object) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Expression.Result object) {
            throw new UnsupportedOperationException();
        }
    }
}
