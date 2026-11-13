package uts.ds;

/**
 * STRUKTUR SEKUNDER: Stack berbasis array (Object[]) yang dibuat dari nol.
 * Dipakai sebagai riwayat/audit operasi pengguna.
 *
 * Pemetaan ke kontrak umum (lihat ADR):
 *  - add(v)      -> push(v)
 *  - get(p)      -> elemen pada kedalaman p dari puncak (0 = puncak), non-destruktif
 *  - remove(p)   -> hanya p == 0 (pop); selain itu UnsupportedOperationException (LIFO)
 *
 * Invariant: 0 <= size <= data.length; slot di atas size bernilai null.
 */
public class ArrayStack<T> implements StrukturData<T> {
    private static final int INITIAL_CAPACITY = 8;

    private Object[] data = new Object[INITIAL_CAPACITY];
    private int size;
    private int lastSearchSteps;

    public int getLastSearchSteps() { return lastSearchSteps; }

    public void push(T value) {
        if (size == data.length) resize(data.length * 2);
        data[size++] = value;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (size == 0) throw new IllegalStateException("Stack kosong");
        T v = (T) data[--size];
        data[size] = null;
        return v;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (size == 0) throw new IllegalStateException("Stack kosong");
        return (T) data[size - 1];
    }

    private void resize(int newCap) {
        Object[] n = new Object[newCap];
        for (int i = 0; i < size; i++) n[i] = data[i];
        data = n;
    }

    /**
     * Pencarian non-destruktif ala LIFO: elemen dipindah ke stack bantu, lalu
     * dikembalikan lewat restore(). Mengembalikan kedalaman (0 = puncak) atau -1.
     */
    public int searchPreserve(Matcher<T> matcher) {
        ArrayStack<T> aux = new ArrayStack<>();
        int steps = 0;
        int depth = -1;
        while (!isEmpty()) {
            T v = pop();
            aux.push(v);
            steps++;
            if (matcher.matches(v)) { depth = steps - 1; break; }
        }
        restore(aux);
        lastSearchSteps = steps;
        return depth;
    }

    /** Mengembalikan seluruh isi stack bantu ke stack ini sehingga urutan asli pulih. */
    public void restore(ArrayStack<T> aux) {
        while (!aux.isEmpty()) push(aux.pop());
    }

    @Override public void add(T value) { push(value); }

    @SuppressWarnings("unchecked")
    @Override
    public T get(int position) {
        if (position < 0 || position >= size)
            throw new IndexOutOfBoundsException("Kedalaman " + position + " di luar 0.." + (size - 1));
        return (T) data[size - 1 - position];
    }

    @Override
    public T remove(int position) {
        if (position != 0)
            throw new UnsupportedOperationException("Stack hanya mendukung penghapusan di puncak (posisi 0)");
        return pop();
    }

    @Override public int size() { return size; }
    @Override public boolean isEmpty() { return size == 0; }

    @Override
    public boolean contains(T value) {
        return searchPreserve(x -> x == null ? value == null : x.equals(value)) >= 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) data[i] = null;
        size = 0;
    }

    @Override
    public void validateInvariant() {
        if (size < 0 || size > data.length) throw new IllegalStateException("size di luar kapasitas");
        for (int i = size; i < data.length; i++)
            if (data[i] != null) throw new IllegalStateException("Slot di atas puncak tidak null pada indeks " + i);
    }
}
