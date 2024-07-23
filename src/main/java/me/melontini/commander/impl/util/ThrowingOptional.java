package me.melontini.commander.impl.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import me.melontini.dark_matter.api.base.util.functions.ThrowingConsumer;
import me.melontini.dark_matter.api.base.util.functions.ThrowingFunction;
import me.melontini.dark_matter.api.base.util.functions.ThrowingRunnable;
import me.melontini.dark_matter.api.base.util.functions.ThrowingSupplier;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode
public class ThrowingOptional<T> {

  private static final ThrowingOptional<?> EMPTY = new ThrowingOptional<>(null);

  private final T value;

  public static <T> ThrowingOptional<T> empty() {
    return (ThrowingOptional<T>) EMPTY;
  }

  public static <T> ThrowingOptional<T> of(T value) {
    return new ThrowingOptional<>(Objects.requireNonNull(value));
  }

  public static <T> ThrowingOptional<T> ofNullable(T value) {
    return value == null ? empty() : new ThrowingOptional<>(value);
  }

  private ThrowingOptional(T value) {
    this.value = value;
  }

  public T get() {
    if (value == null) {
      throw new NoSuchElementException("No value present");
    }
    return value;
  }

  public boolean isPresent() {
    return value != null;
  }

  public boolean isEmpty() {
    return value == null;
  }

  public <E extends Throwable> void ifPresent(ThrowingConsumer<? super T, E> action) throws E {
    if (value != null) {
      action.accept(value);
    }
  }

  public <E extends Throwable> void ifPresentOrElse(
      ThrowingConsumer<? super T, E> action, ThrowingRunnable<E> emptyAction) throws E {
    if (value != null) {
      action.accept(value);
    } else {
      emptyAction.run();
    }
  }

  public <E extends Throwable> ThrowingOptional<T> filter(
      ThrowingFunction<? super T, @NotNull Boolean, E> predicate) throws E {
    Objects.requireNonNull(predicate);
    if (isEmpty()) {
      return this;
    } else {
      return predicate.apply(value) ? this : empty();
    }
  }

  public <E extends Throwable, U> ThrowingOptional<U> map(
      ThrowingFunction<? super T, ? extends U, E> mapper) throws E {
    Objects.requireNonNull(mapper);
    if (isEmpty()) {
      return empty();
    } else {
      return ofNullable(mapper.apply(value));
    }
  }

  public <E extends Throwable, U> ThrowingOptional<U> flatMap(
      ThrowingFunction<? super T, ? extends ThrowingOptional<? extends U>, E> mapper) throws E {
    Objects.requireNonNull(mapper);
    if (isEmpty()) {
      return empty();
    } else {
      @SuppressWarnings("unchecked")
      ThrowingOptional<U> r = (ThrowingOptional<U>) mapper.apply(value);
      return Objects.requireNonNull(r);
    }
  }

  public <E extends Throwable> ThrowingOptional<T> or(
      ThrowingSupplier<? extends ThrowingOptional<? extends T>, E> supplier) throws E {
    Objects.requireNonNull(supplier);
    if (isPresent()) {
      return this;
    } else {
      @SuppressWarnings("unchecked")
      ThrowingOptional<T> r = (ThrowingOptional<T>) supplier.get();
      return Objects.requireNonNull(r);
    }
  }

  public Stream<T> stream() {
    if (isEmpty()) {
      return Stream.empty();
    } else {
      return Stream.of(value);
    }
  }

  public T orElse(T other) {
    return value != null ? value : other;
  }

  public <E extends Throwable> T orElseGet(ThrowingSupplier<? extends T, E> supplier) throws E {
    return value != null ? value : supplier.get();
  }

  public T orElseThrow() {
    if (value == null) {
      throw new NoSuchElementException("No value present");
    }
    return value;
  }

  public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
    if (value != null) {
      return value;
    } else {
      throw exceptionSupplier.get();
    }
  }

  @Override
  public String toString() {
    return value != null ? ("ThrowingOptional[" + value + "]") : "ThrowingOptional.empty";
  }
}
