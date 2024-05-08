package me.melontini.commander.impl.expression.extensions.convert;

import lombok.RequiredArgsConstructor;
import me.melontini.commander.impl.expression.extensions.ProxyMap;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LazyArrayWrappers {

    @RequiredArgsConstructor
    public static class ObjectArray extends Base {
        private final Object[] objects;

        @Override
        public Object get(int index) {
            return ProxyMap.convert(objects[index]);
        }

        @Override
        public int size() {
            return objects.length;
        }
    }

    @RequiredArgsConstructor
    public static class DoubleArray extends Base {
        private final double[] objects;

        @Override
        public Object get(int index) {
            return ProxyMap.convert(objects[index]);
        }

        @Override
        public int size() {
            return objects.length;
        }
    }

    @RequiredArgsConstructor
    public static class IntArray extends Base {
        private final int[] objects;

        @Override
        public Object get(int index) {
            return ProxyMap.convert(objects[index]);
        }

        @Override
        public int size() {
            return objects.length;
        }
    }

    @RequiredArgsConstructor
    public static class FloatArray extends Base {
        private final float[] objects;

        @Override
        public Object get(int index) {
            return ProxyMap.convert(objects[index]);
        }

        @Override
        public int size() {
            return objects.length;
        }
    }

    @RequiredArgsConstructor
    public static class LongArray extends Base {
        private final long[] objects;

        @Override
        public Object get(int index) {
            return ProxyMap.convert(objects[index]);
        }

        @Override
        public int size() {
            return objects.length;
        }
    }

    @RequiredArgsConstructor
    public static class ShortArray extends Base {
        private final short[] objects;

        @Override
        public Object get(int index) {
            return ProxyMap.convert(objects[index]);
        }

        @Override
        public int size() {
            return objects.length;
        }
    }

    @RequiredArgsConstructor
    public static class CharArray extends Base {
        private final char[] objects;

        @Override
        public Object get(int index) {
            return ProxyMap.convert(objects[index]);
        }

        @Override
        public int size() {
            return objects.length;
        }
    }

    @RequiredArgsConstructor
    public static class ByteArray extends Base {
        private final byte[] objects;

        @Override
        public Object get(int index) {
            return ProxyMap.convert(objects[index]);
        }

        @Override
        public int size() {
            return objects.length;
        }
    }

    @RequiredArgsConstructor
    public static class BooleanArray extends Base {
        private final boolean[] objects;

        @Override
        public Object get(int index) {
            return ProxyMap.convert(objects[index]);
        }

        @Override
        public int size() {
            return objects.length;
        }
    }

    private abstract static class ArrayIterator implements ListIterator<Object> {

        private final int size;
        private int position;

        public ArrayIterator(int size, int position) {
            this.size = size;
            this.position = position;
        }

        protected abstract Object get(int index);

        @Override
        public boolean hasNext() {
            return position < size;
        }

        @Override
        public Object next() {
            if (!hasNext()) throw new NoSuchElementException();
            return get(position++);
        }

        @Override
        public boolean hasPrevious() {
            return position > 0;
        }

        @Override
        public Object previous() {
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
        public void set(Object object) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Object object) {
            throw new UnsupportedOperationException();
        }
    }

    private abstract static class Base implements List<Object> {

        @Override
        public boolean isEmpty() {
            return size() == 0;
        }

        @NotNull @Override
        public ListIterator<Object> listIterator(int index) {
            return new ArrayIterator(size(), index) {
                @Override
                protected Object get(int index) {
                    return Base.this.get(index);
                }
            };
        }

        @Override
        public boolean contains(Object o) {
            throw new IllegalStateException();
        }

        @NotNull @Override
        public Iterator<Object> iterator() {
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
        public boolean add(Object object) {
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
        public boolean addAll(@NotNull Collection<?> c) {
            throw new IllegalStateException();
        }

        @Override
        public boolean addAll(int index, @NotNull Collection<?> c) {
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
        public Object set(int index, Object element) {
            throw new IllegalStateException();
        }

        @Override
        public void add(int index, Object element) {
            throw new IllegalStateException();
        }

        @Override
        public Object remove(int index) {
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
        public ListIterator<Object> listIterator() {
            return listIterator(0);
        }

        @NotNull @Override
        public List<Object> subList(int fromIndex, int toIndex) {
            throw new IllegalStateException();
        }
    }
}
