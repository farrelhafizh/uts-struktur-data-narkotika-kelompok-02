package uts.ds;

/** Pemberi label ringkas untuk keperluan trace pointer. */
@FunctionalInterface
public interface Labeler<T> {
    String label(T item);
}
