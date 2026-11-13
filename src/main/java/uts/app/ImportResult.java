package uts.app;

import uts.ds.SinglyLinkedList;

/** Ringkasan hasil impor CSV. */
public class ImportResult {
    public int diterima;
    public int ditolak;
    public final SinglyLinkedList<String> pesanKesalahan = new SinglyLinkedList<>();
}
