package uts.app;

import uts.ds.ArrayStack;
import uts.ds.SinglyLinkedList;
import uts.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Logika aplikasi. Seluruh putusan disimpan di SinglyLinkedList (struktur utama);
 * audit operasi disimpan di ArrayStack (struktur sekunder).
 */
public class PutusanService {
    public static final int MAX_IMPORT = 50;

    private final SinglyLinkedList<Putusan> arsip = new SinglyLinkedList<>();
    private final ArrayStack<AuditEntry> audit = new ArrayStack<>();

    public SinglyLinkedList<Putusan> getArsip() { return arsip; }
    public ArrayStack<AuditEntry> getAudit() { return audit; }

    // ---------------- Impor ----------------
    public ImportResult importCsv(Path path) throws IOException {
        ImportResult res = new ImportResult();
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line = br.readLine(); // header
            int lineNo = 1;
            while ((line = br.readLine()) != null) {
                lineNo++;
                if (line.trim().isEmpty()) continue;
                try {
                    if (arsip.size() >= MAX_IMPORT)
                        throw new IllegalArgumentException("melebihi batas " + MAX_IMPORT + " record");
                    addRecord(Putusan.fromFields(CsvUtil.parseLine(line)), false);
                    res.diterima++;
                } catch (IllegalArgumentException e) {
                    res.ditolak++;
                    res.pesanKesalahan.addLast("Baris " + lineNo + ": " + e.getMessage());
                }
            }
        }
        if (res.diterima != MAX_IMPORT)
            res.pesanKesalahan.addLast("PERINGATAN: record valid = " + res.diterima + " (seharusnya " + MAX_IMPORT + ")");
        audit.push(new AuditEntry("IMPOR", path + " diterima=" + res.diterima + " ditolak=" + res.ditolak));
        arsip.validateInvariant();
        return res;
    }

    // ---------------- Tambah / hapus ----------------
    public void addRecord(Putusan p) { addRecord(p, true); }

    private void addRecord(Putusan p, boolean log) {
        if (p == null) throw new IllegalArgumentException("Putusan null");
        if (arsip.search(x -> x.getCaseId().equals(p.getCaseId())) >= 0)
            throw new IllegalArgumentException("caseId duplikat: " + p.getCaseId());
        arsip.addLast(p);
        if (log) audit.push(new AuditEntry("TAMBAH", p.getCaseId() + " di akhir"));
    }

    /** Menambah dari kolom CSV mentah; bila tidak valid, struktur tidak berubah. */
    public void addFromFields(String[] fields) {
        addRecord(Putusan.fromFields(fields), true);
    }

    public void insertAt(int position, Putusan p) {
        if (arsip.search(x -> x.getCaseId().equals(p.getCaseId())) >= 0)
            throw new IllegalArgumentException("caseId duplikat: " + p.getCaseId());
        arsip.insert(position, p);
        audit.push(new AuditEntry("SISIP", p.getCaseId() + " pada posisi " + position));
    }

    public Putusan removeAt(int position) {
        Putusan p = arsip.remove(position);
        audit.push(new AuditEntry("HAPUS", p.getCaseId() + " dari posisi " + position));
        return p;
    }

    // ---------------- Cari ----------------
    /** Mencari berdasarkan nomor perkara atau caseId. */
    public SearchResult cari(String kunci) {
        String k = kunci.trim();
        int idx = arsip.search(p -> p.getNomorPerkara().equalsIgnoreCase(k) || p.getCaseId().equalsIgnoreCase(k));
        int steps = arsip.getLastSearchSteps();
        audit.push(new AuditEntry("CARI", k + " -> " + (idx >= 0 ? "ditemukan di " + idx : "tidak ada") + ", langkah=" + steps));
        return new SearchResult(idx, steps, idx >= 0 ? arsip.get(idx) : null);
    }

    // ---------------- Filter ----------------
    public SinglyLinkedList<Putusan> filter(FilterCriteria c) {
        SinglyLinkedList<Putusan> hasil = arsip.filter(c::matches);
        audit.push(new AuditEntry("FILTER", c + " -> " + hasil.size() + " hasil"));
        return hasil;
    }

    // ---------------- Agregasi ----------------
    public Aggregate aggregate(SinglyLinkedList<Putusan> sumber) {
        Aggregate a = new Aggregate();
        long totalVonis = 0;
        for (SinglyLinkedList.Node<Putusan> n = sumber.headNode(); n != null; n = n.getNext()) {
            Putusan p = n.getValue();
            a.jumlahPerkara++;
            totalVonis += p.getVonisBulan();
            a.totalDenda += p.getDendaRupiah();
            tambahJenis(a.distribusiJenis, p.getJenisNarkotika());
        }
        a.rataRataVonisBulan = a.jumlahPerkara == 0 ? 0 : (double) totalVonis / a.jumlahPerkara;
        audit.push(new AuditEntry("AGREGASI", "n=" + a.jumlahPerkara));
        return a;
    }

    /** Jenis dipisah dengan ';' bila lebih dari satu. */
    private void tambahJenis(SinglyLinkedList<Counter> dist, String jenisField) {
        String[] parts = jenisField.isEmpty() ? new String[] {"(kosong)"} : jenisField.split(";");
        for (int i = 0; i < parts.length; i++) {
            final String key = parts[i].trim().toLowerCase();
            if (key.isEmpty()) continue;
            Counter c = dist.find(x -> x.getKey().equals(key));
            if (c == null) { c = new Counter(key); dist.addLast(c); }
            c.increment();
        }
    }

    // ---------------- Urutan (view terurut, arsip asli tidak berubah) ----------------
    /** Insertion sort stabil ke list baru. O(n^2) waktu, O(n) memori tambahan. */
    public SinglyLinkedList<Putusan> urutkanBerdasarkanVonis(boolean menaik) {
        SinglyLinkedList<Putusan> out = new SinglyLinkedList<>();
        for (SinglyLinkedList.Node<Putusan> n = arsip.headNode(); n != null; n = n.getNext()) {
            Putusan p = n.getValue();
            int pos = 0;
            for (SinglyLinkedList.Node<Putusan> c = out.headNode(); c != null; c = c.getNext()) {
                int a = p.getVonisBulan(), b = c.getValue().getVonisBulan();
                if (menaik ? a < b : a > b) break;
                pos++;
            }
            out.insert(pos, p);
        }
        audit.push(new AuditEntry("URUT", "vonis " + (menaik ? "menaik" : "menurun")));
        return out;
    }

    // ---------------- Tampil ----------------
    public String tampilkan(SinglyLinkedList<Putusan> list) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (SinglyLinkedList.Node<Putusan> n = list.headNode(); n != null; n = n.getNext())
            sb.append(String.format("%3d. %s%n", i++, n.getValue().ringkas()));
        return sb.toString();
    }

    // ---------------- Audit ----------------
    /** Menampilkan riwayat (terbaru dulu) tanpa mengubah isi stack. */
    public String tampilkanAudit() {
        StringBuilder sb = new StringBuilder();
        for (int d = 0; d < audit.size(); d++)
            sb.append(String.format("%3d. %s%n", d + 1, audit.get(d)));
        return sb.length() == 0 ? "(riwayat kosong)\n" : sb.toString();
    }

    // ---------------- Ekspor ----------------
    public void eksporCsv(SinglyLinkedList<Putusan> list, Path path) throws IOException {
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8))) {
            w.println(CsvUtil.toLine(Putusan.HEADER));
            for (SinglyLinkedList.Node<Putusan> n = list.headNode(); n != null; n = n.getNext())
                w.println(CsvUtil.toLine(n.getValue().toFields()));
        }
        audit.push(new AuditEntry("EKSPOR", path + " (" + list.size() + " baris)"));
    }

    public void eksporLaporan(SinglyLinkedList<Putusan> list, Path path) throws IOException {
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8))) {
            w.println("LAPORAN PUTUSAN NARKOTIKA");
            w.print(tampilkan(list));
            w.println();
            w.print(aggregate(list).ringkas());
        }
        audit.push(new AuditEntry("EKSPOR", path + " (laporan)"));
    }
}
