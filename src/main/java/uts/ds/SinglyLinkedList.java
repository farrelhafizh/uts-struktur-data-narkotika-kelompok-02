package uts.ds;

/**
 * STRUKTUR UTAMA: Singly Linked List generik, dibuat dari nol.
 * Invariant: head/tail konsisten dengan size; tail.next == null;
 * list kosong <=> head == null && tail == null && size == 0.
 */
public class SinglyLinkedList<T> implements StrukturData<T> {

    /** Node publik agar dapat ditelusuri manual (tanpa Iterator). */
    public static class Node<E> {
        private final E value;
        private Node<E> next;

        private Node(E value) { this.value = value; }
        public E getValue() { return value; }
        public Node<E> getNext() { return next; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;
    private int lastSearchSteps;   // pencacah langkah pencarian terakhir
    private long totalSteps;       // pencacah langkah kumulatif (untuk analisis)

    public Node<T> headNode() { return head; }
    public int getLastSearchSteps() { return lastSearchSteps; }
    public long getTotalSteps() { return totalSteps; }

    // ---------- operasi penambahan ----------
    /** O(1) */
    public void addFirst(T value) {
        Node<T> n = new Node<>(value);
        n.next = head;
        head = n;
        if (tail == null) tail = n;
        size++;
    }

    /** O(1) berkat pointer tail */
    public void addLast(T value) {
        Node<T> n = new Node<>(value);
        if (tail == null) {
            head = tail = n;
        } else {
            tail.next = n;
            tail = n;
        }
        size++;
    }

    @Override
    public void add(T value) { addLast(value); }

    /** Sisip pada posisi 0..size. O(n). */
    public void insert(int position, T value) {
        if (position < 0 || position > size)
            throw new IndexOutOfBoundsException("Posisi " + position + " di luar 0.." + size);
        if (position == 0) { addFirst(value); return; }
        if (position == size) { addLast(value); return; }
        Node<T> prev = nodeAt(position - 1);
        Node<T> n = new Node<>(value);
        n.next = prev.next;
        prev.next = n;
        size++;
    }

    // ---------- akses ----------
    @Override
    public T get(int position) {
        checkIndex(position);
        return nodeAt(position).value;
    }

    private Node<T> nodeAt(int position) {
        Node<T> cur = head;
        for (int i = 0; i < position; i++) cur = cur.next;
        return cur;
    }

    private void checkIndex(int position) {
        if (position < 0 || position >= size)
            throw new IndexOutOfBoundsException("Posisi " + position + " di luar 0.." + (size - 1));
    }

    // ---------- penghapusan ----------
    @Override
    public T remove(int position) {
        checkIndex(position);
        Node<T> removed;
        if (position == 0) {
            removed = head;
            head = head.next;
            if (head == null) tail = null;
        } else {
            Node<T> prev = nodeAt(position - 1);
            removed = prev.next;
            prev.next = removed.next;
            if (removed == tail) tail = prev;
        }
        removed.next = null;
        size--;
        return removed.value;
    }

    // ---------- pencarian ----------
    /** Pencarian linear; mengembalikan indeks atau -1. Mencatat jumlah langkah. */
    public int search(Matcher<T> matcher) {
        int steps = 0;
        int index = 0;
        Node<T> cur = head;
        while (cur != null) {
            steps++;
            if (matcher.matches(cur.value)) {
                lastSearchSteps = steps;
                totalSteps += steps;
                return index;
            }
            cur = cur.next;
            index++;
        }
        lastSearchSteps = steps;
        totalSteps += steps;
        return -1;
    }

    /** Mengembalikan elemen pertama yang cocok atau null. */
    public T find(Matcher<T> matcher) {
        int idx = search(matcher);
        return idx < 0 ? null : get(idx);
    }

    @Override
    public boolean contains(T value) {
        return search(x -> x == null ? value == null : x.equals(value)) >= 0;
    }

    /** Membuat list baru berisi elemen yang cocok (urutan asli dipertahankan). */
    public SinglyLinkedList<T> filter(Matcher<T> matcher) {
        SinglyLinkedList<T> out = new SinglyLinkedList<>();
        for (Node<T> cur = head; cur != null; cur = cur.next)
            if (matcher.matches(cur.value)) out.addLast(cur.value);
        return out;
    }

    @Override public int size() { return size; }
    @Override public boolean isEmpty() { return size == 0; }

    @Override
    public void clear() {
        // putuskan rantai agar bersih
        Node<T> cur = head;
        while (cur != null) {
            Node<T> nx = cur.next;
            cur.next = null;
            cur = nx;
        }
        head = tail = null;
        size = 0;
    }

    // ---------- invariant & trace ----------
    @Override
    public void validateInvariant() {
        if (size < 0) throw new IllegalStateException("size negatif");
        if (size == 0) {
            if (head != null || tail != null)
                throw new IllegalStateException("List kosong tetapi head/tail tidak null");
            return;
        }
        if (head == null || tail == null)
            throw new IllegalStateException("List tidak kosong tetapi head/tail null");
        int count = 0;
        Node<T> cur = head, last = null;
        while (cur != null) {
            count++;
            if (count > size) throw new IllegalStateException("Jumlah node melebihi size (siklus?)");
            last = cur;
            cur = cur.next;
        }
        if (count != size) throw new IllegalStateException("Jumlah node " + count + " != size " + size);
        if (last != tail) throw new IllegalStateException("tail tidak menunjuk node terakhir");
        if (tail.next != null) throw new IllegalStateException("tail.next tidak null");
    }

    /** Representasi pointer, misal: head -> [A] -> [B] -> null (tail=[B]). */
    public String traceString(Labeler<T> labeler) {
        StringBuilder sb = new StringBuilder("head");
        for (Node<T> cur = head; cur != null; cur = cur.next)
            sb.append(" -> [").append(labeler.label(cur.value)).append("]");
        sb.append(" -> null");
        sb.append("  (size=").append(size);
        sb.append(", tail=").append(tail == null ? "null" : "[" + labeler.label(tail.value) + "]").append(")");
        return sb.toString();
    }
}
