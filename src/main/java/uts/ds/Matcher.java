package uts.ds;

/** Predikat buatan sendiri (menghindari ketergantungan pada java.util.function). */
@FunctionalInterface
public interface Matcher<T> {
    boolean matches(T item);
}
