package uts.app;

import uts.model.Putusan;

/** Hasil pencarian beserta jumlah langkah. */
public class SearchResult {
    public final int index;      // -1 bila tidak ditemukan
    public final int steps;
    public final Putusan putusan;

    public SearchResult(int index, int steps, Putusan putusan) {
        this.index = index;
        this.steps = steps;
        this.putusan = putusan;
    }

    public boolean found() { return index >= 0; }
}

