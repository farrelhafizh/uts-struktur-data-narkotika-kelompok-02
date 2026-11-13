package uts.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Satu putusan pengadilan narkotika (skema minimum Bagian 2.2). */
public class Putusan {
    public static final String[] HEADER = {
        "caseId", "nomorPerkara", "pengadilan", "tanggalPutusan", "terdakwaKode",
        "jenisNarkotika", "beratBarangBuktiGram", "pasalDakwaan", "pasalTerbukti",
        "tuntutanBulan", "vonisBulan", "dendaRupiah", "barangBukti",
        "keadaanMemberatkan", "keadaanMeringankan", "sourcePages"
    };

    private final String caseId;
    private final String nomorPerkara;
    private final String pengadilan;
    private final LocalDate tanggalPutusan;   // null bila tidak ditemukan
    private final String terdakwaKode;
    private final String jenisNarkotika;
    private final double beratBarangBuktiGram; // 0 bila tidak ditemukan
    private final String pasalDakwaan;
    private final String pasalTerbukti;
    private final int tuntutanBulan;
    private final int vonisBulan;
    private final long dendaRupiah;
    private final String barangBukti;
    private final String keadaanMemberatkan;
    private final String keadaanMeringankan;
    private final String sourcePages;

    public Putusan(String caseId, String nomorPerkara, String pengadilan, LocalDate tanggalPutusan,
                   String terdakwaKode, String jenisNarkotika, double beratBarangBuktiGram,
                   String pasalDakwaan, String pasalTerbukti, int tuntutanBulan, int vonisBulan,
                   long dendaRupiah, String barangBukti, String keadaanMemberatkan,
                   String keadaanMeringankan, String sourcePages) {
        this.caseId = caseId;
        this.nomorPerkara = nomorPerkara;
        this.pengadilan = pengadilan;
        this.tanggalPutusan = tanggalPutusan;
        this.terdakwaKode = terdakwaKode;
        this.jenisNarkotika = jenisNarkotika;
        this.beratBarangBuktiGram = beratBarangBuktiGram;
        this.pasalDakwaan = pasalDakwaan;
        this.pasalTerbukti = pasalTerbukti;
        this.tuntutanBulan = tuntutanBulan;
        this.vonisBulan = vonisBulan;
        this.dendaRupiah = dendaRupiah;
        this.barangBukti = barangBukti;
        this.keadaanMemberatkan = keadaanMemberatkan;
        this.keadaanMeringankan = keadaanMeringankan;
        this.sourcePages = sourcePages;
    }

    /** Membangun Putusan dari 16 kolom CSV; melempar IllegalArgumentException bila tidak valid. */
    public static Putusan fromFields(String[] f) {
        if (f.length != HEADER.length)
            throw new IllegalArgumentException("Jumlah kolom " + f.length + ", seharusnya " + HEADER.length);
        String caseId = required(f[0], "caseId");
        String nomor = required(f[1], "nomorPerkara");
        String pengadilan = required(f[2], "pengadilan");
        LocalDate tgl = null;
        if (!f[3].trim().isEmpty()) {
            try { tgl = LocalDate.parse(f[3].trim()); }
            catch (DateTimeParseException e) {
                throw new IllegalArgumentException("tanggalPutusan harus berformat yyyy-MM-dd: '" + f[3] + "'");
            }
        }
        String kode = required(f[4], "terdakwaKode");
        if (!kode.matches("[A-Za-z]{1,4}-?\\d{1,5}"))
            throw new IllegalArgumentException("terdakwaKode harus berupa kode anonim (mis. TD-001), bukan nama: '" + kode + "'");
        double berat = parseDouble(f[6], "beratBarangBuktiGram");
        int tuntutan = (int) parseLong(f[9], "tuntutanBulan");
        int vonis = (int) parseLong(f[10], "vonisBulan");
        long denda = parseLong(f[11], "dendaRupiah");
        return new Putusan(caseId, nomor, pengadilan, tgl, kode, f[5].trim(), berat,
                f[7].trim(), f[8].trim(), tuntutan, vonis, denda, f[12].trim(),
                f[13].trim(), f[14].trim(), f[15].trim());
    }

    private static String required(String s, String name) {
        if (s == null || s.trim().isEmpty()) throw new IllegalArgumentException(name + " wajib diisi");
        return s.trim();
    }

    private static double parseDouble(String s, String name) {
        if (s.trim().isEmpty()) return 0;
        try {
            double d = Double.parseDouble(s.trim());
            if (d < 0 || Double.isNaN(d) || Double.isInfinite(d)) throw new NumberFormatException();
            return d;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " harus angka >= 0: '" + s + "'");
        }
    }

    private static long parseLong(String s, String name) {
        if (s.trim().isEmpty()) return 0;
        try {
            long v = Long.parseLong(s.trim());
            if (v < 0) throw new NumberFormatException();
            return v;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " harus bilangan bulat >= 0: '" + s + "'");
        }
    }

    public String[] toFields() {
        return new String[] {
            caseId, nomorPerkara, pengadilan, tanggalPutusan == null ? "" : tanggalPutusan.toString(),
            terdakwaKode, jenisNarkotika, String.valueOf(beratBarangBuktiGram), pasalDakwaan, pasalTerbukti,
            String.valueOf(tuntutanBulan), String.valueOf(vonisBulan), String.valueOf(dendaRupiah),
            barangBukti, keadaanMemberatkan, keadaanMeringankan, sourcePages
        };
    }

    public String ringkas() {
        return String.format("%-6s | %-22s | %-18s | %-10s | %-14s | %8.2f g | vonis %3d bln | denda Rp%,d",
                caseId, nomorPerkara, pengadilan, tanggalPutusan == null ? "-" : tanggalPutusan.toString(),
                jenisNarkotika, beratBarangBuktiGram, vonisBulan, dendaRupiah);
    }

    @Override public String toString() { return caseId + "/" + nomorPerkara; }

    public String getCaseId() { return caseId; }
    public String getNomorPerkara() { return nomorPerkara; }
    public String getPengadilan() { return pengadilan; }
    public LocalDate getTanggalPutusan() { return tanggalPutusan; }
    public String getTerdakwaKode() { return terdakwaKode; }
    public String getJenisNarkotika() { return jenisNarkotika; }
    public double getBeratBarangBuktiGram() { return beratBarangBuktiGram; }
    public String getPasalDakwaan() { return pasalDakwaan; }
    public String getPasalTerbukti() { return pasalTerbukti; }
    public int getTuntutanBulan() { return tuntutanBulan; }
    public int getVonisBulan() { return vonisBulan; }
    public long getDendaRupiah() { return dendaRupiah; }
    public String getBarangBukti() { return barangBukti; }
    public String getKeadaanMemberatkan() { return keadaanMemberatkan; }
    public String getKeadaanMeringankan() { return keadaanMeringankan; }
    public String getSourcePages() { return sourcePages; }
}
