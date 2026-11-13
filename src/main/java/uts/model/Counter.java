package uts.model;

/** Pasangan kunci-jumlah untuk distribusi (pengganti Map bawaan). */
public class Counter {
    private final String key;
    private int count;

    public Counter(String key) { this.key = key; }
    public void increment() { count++; }
    public String getKey() { return key; }
    public int getCount() { return count; }
}
