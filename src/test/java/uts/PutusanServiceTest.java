package uts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uts.app.ImportResult;
import uts.app.PutusanService;
import uts.app.SearchResult;
import uts.ds.SinglyLinkedList;
import uts.model.Aggregate;
import uts.model.FilterCriteria;
import uts.model.Putusan;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PutusanServiceTest {
    private PutusanService svc;

    private static String[] baris(String id, String nomor, String jenis, String berat, String pasal, int vonis, long denda) {
        return new String[] {id, nomor, "PN Contoh", "2025-01-15", "TD-" + id.substring(1), jenis, berat,
                "Pasal 112", pasal, "0", String.valueOf(vonis), String.valueOf(denda), "bb", "m", "r", "hal 1"};
    }

    @BeforeEach void setUp() {
        svc = new PutusanService();
        svc.addFromFields(baris("P001", "1/Pid.Sus/2025/PN A", "sabu", "1.5", "Pasal 114", 60, 800_000_000L));
        svc.addFromFields(baris("P002", "2/Pid.Sus/2025/PN B", "ganja", "20", "Pasal 111", 24, 0));
        svc.addFromFields(baris("P003", "3/Pid.Sus/2025/PN C", "sabu;ekstasi", "0.5", "Pasal 112", 36, 800_000_000L));
        svc.addFromFields(baris("P004", "4/Pid.Sus/2025/PN D", "sabu", "10", "Pasal 114", 96, 1_000_000_000L));
    }

    @Test void cariAdaTidakAdaDanBatas() {
        SearchResult a = svc.cari("P001");
        assertTrue(a.found());
        assertEquals(1, a.steps);
        SearchResult b = svc.cari("4/Pid.Sus/2025/PN D");
        assertEquals(3, b.index);
        assertEquals(4, b.steps);
        SearchResult c = svc.cari("XXX");
        assertFalse(c.found());
        assertEquals(4, c.steps);
    }

    @Test void recordTidakValidDitolakStrukturTetap() {
        int sebelum = svc.getArsip().size();
        String[] buruk = baris("P005", "5/x", "sabu", "abc", "Pasal 114", 12, 0);
        assertThrows(IllegalArgumentException.class, () -> svc.addFromFields(buruk));
        String[] nama = baris("P006", "6/x", "sabu", "1", "Pasal 114", 12, 0);
        nama[4] = "Budi Santoso";
        assertThrows(IllegalArgumentException.class, () -> svc.addFromFields(nama));
        String[] dup = baris("P001", "7/x", "sabu", "1", "Pasal 114", 12, 0);
        assertThrows(IllegalArgumentException.class, () -> svc.addFromFields(dup));
        assertEquals(sebelum, svc.getArsip().size());
        svc.getArsip().validateInvariant();
    }

    @Test void hapusDanTambahKembali() {
        Putusan pertama = svc.removeAt(0);
        svc.removeAt(1);
        Putusan terakhir = svc.removeAt(svc.getArsip().size() - 1);
        assertEquals(1, svc.getArsip().size());
        svc.getArsip().validateInvariant();
        svc.addRecord(pertama);
        svc.addRecord(terakhir);
        assertEquals(3, svc.getArsip().size());
        assertEquals("P001", svc.getArsip().get(1).getCaseId());
        svc.getArsip().validateInvariant();
    }

    @Test void filterGabungan() {
        FilterCriteria c = new FilterCriteria();
        c.jenisNarkotika = "sabu";
        c.minVonis = 40;
        SinglyLinkedList<Putusan> h = svc.filter(c);
        assertEquals(2, h.size());           // P001 (60) dan P004 (96)
        assertEquals("P001", h.get(0).getCaseId());
        c.minBerat = 5.0;
        assertEquals(1, svc.filter(c).size()); // hanya P004
        assertEquals(4, svc.getArsip().size());
    }

    @Test void agregasi() {
        Aggregate a = svc.aggregate(svc.getArsip());
        assertEquals(4, a.jumlahPerkara);
        assertEquals(54.0, a.rataRataVonisBulan, 1e-9);
        assertEquals(2_600_000_000L, a.totalDenda);
        assertEquals(3, svc.getArsip() == null ? 0 : a.distribusiJenis.size()); // sabu, ganja, ekstasi
        assertEquals(3, a.distribusiJenis.find(x -> x.getKey().equals("sabu")).getCount());
    }

    @Test void urutanTidakMerusakArsip() {
        SinglyLinkedList<Putusan> naik = svc.urutkanBerdasarkanVonis(true);
        assertEquals("P002", naik.get(0).getCaseId());
        assertEquals("P004", naik.get(3).getCaseId());
        SinglyLinkedList<Putusan> turun = svc.urutkanBerdasarkanVonis(false);
        assertEquals("P004", turun.get(0).getCaseId());
        assertEquals("P001", svc.getArsip().get(0).getCaseId());   // arsip tetap
        naik.validateInvariant();
        svc.getArsip().validateInvariant();
    }

    @Test void auditMerekamDanTidakMengubahStack() {
        svc.cari("P001");
        svc.removeAt(0);
        int n = svc.getAudit().size();
        String t1 = svc.tampilkanAudit();
        String t2 = svc.tampilkanAudit();
        assertEquals(t1, t2);
        assertEquals(n, svc.getAudit().size());
        assertTrue(svc.getAudit().peek().getAksi().equals("HAPUS"));
        svc.getAudit().validateInvariant();
    }

    @Test void imporMenolakRecordBurukTanpaBerhenti() throws Exception {
        Path tmp = Files.createTempFile("uji", ".csv");
        StringBuilder sb = new StringBuilder(String.join(",", Putusan.HEADER)).append('\n');
        sb.append("P010,1/a,PN X,2025-02-01,TD-010,sabu,1.0,P112,P112,12,10,0,bb,m,r,h1\n");
        sb.append("ini,baris,rusak\n");
        sb.append("P011,2/a,PN Y,tanggal-salah,TD-011,sabu,1.0,P112,P112,12,10,0,bb,m,r,h1\n");
        sb.append("P012,3/a,PN Z,,TD-012,ganja,,P111,P111,,8,,\"bb, lebih\",m,r,h1\n");
        Files.writeString(tmp, sb.toString());
        PutusanService s = new PutusanService();
        ImportResult r = s.importCsv(tmp);
        assertEquals(2, r.diterima);
        assertEquals(2, r.ditolak);
        assertEquals(2, s.getArsip().size());
        assertNull(s.getArsip().get(1).getTanggalPutusan());   // kosong -> null, bukan perkiraan
        assertEquals(0.0, s.getArsip().get(1).getBeratBarangBuktiGram());
        s.getArsip().validateInvariant();
        Files.deleteIfExists(tmp);
    }
}
