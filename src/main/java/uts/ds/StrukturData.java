package uts.ds;

/** Kontrak umum struktur data (Bagian 3.2 panduan). */
public interface StrukturData<T> {
    void add(T value);
    T get(int position);
    T remove(int position);
    int size();
    boolean isEmpty();
    boolean contains(T value);
    void clear();
    /** Melempar IllegalStateException bila invariant rusak. */
    void validateInvariant();
}
