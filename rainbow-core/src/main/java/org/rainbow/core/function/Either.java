package org.rainbow.core.function;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

/**
 * 封装异常 stream 流的的异常
 *
 * @author K
 * @date 2021/1/25  15:20
 */
public class Either<L, R> {
    /**
     * 左值表示异常
     */
    private final L left;
    /**
     * 右值表示成功的数据
     */
    private final R right;

    private Either(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> Either<L, R> left(L left) {
        return new Either<>(left, null);
    }

    public static <L, R> Either<L, R> right(R right) {
        return new Either<>(null, right);
    }

    /**
     * 处理类
     */
    public static <T, R> Function<T, Either> lift(CheckedException<T, R> function) {
        return t -> {
            try {
                return Either.right(function.apply(t));
            } catch (Exception e) {
                return Either.left(e);
            }
        };
    }

    public static <T, R> Function<T, Either> liftWithValue(CheckedException<T, R> function) {
        return t -> {
            try {
                return Either.right(function.apply(t));
            } catch (Exception e) {
                return Either.left(Pair.of(e, t));
            }
        };
    }

    public Optional<L> getLeft() {
        return Optional.ofNullable(left);
    }

    public Optional<R> getRight() {
        return Optional.ofNullable(right);
    }

    public boolean isLeft() {
        return left != null;
    }

    public boolean isRight() {
        return right != null;
    }

    public <T> Optional<T> mapLeft(Function<? super L, T> mapper) {
        if (isLeft()) {
            return Optional.of(mapper.apply(left));
        }
        return Optional.empty();
    }

    public <T> Optional<T> mapRight(Function<? super R, T> mapper) {
        if (isRight()) {
            return Optional.of(mapper.apply(right));
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        if (isLeft()) {
            return "Left(" + left + ")";
        }
        return "Right(" + right + ")";
    }

}
