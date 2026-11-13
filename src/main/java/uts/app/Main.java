
package uts.app;

import uts.ds.SinglyLinkedList;
import uts.model.FilterCriteria;
import uts.model.Putusan;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Aplikasi konsol interaktif.
 * Jalankan: java -cp target/classes uts.app.Main [path-csv]
 */
public class Main {

    public static void main(String[] args) {
        PutusanService svc = new PutusanService();
        String csv = args.length > 0 ? args[0] : "data/putusan-50.csv";
        Scanner in = new Scanner(System.in);

        System.out.println("=== Arsip Putusan Narkotika (Singly Linked List) ===");

        try {
            ImportResult r = svc.importCsv(Paths.get(csv));

            System.out.println("Impor: diterima=" + r.diterima
                    + ", ditolak=" + r.ditolak);

            for (SinglyLinkedList.Node<String> n = r.pesanKesalahan.headNode();
                 n != null;
                 n = n.getNext()) {
                System.out.println("  ! " + n.getValue());
            }

        } catch (IOException e) {
            System.out.println("Gagal membaca " + csv + ": " + e.getMessage());
        }

        boolean jalan = true;

        while (jalan) {
            System.out.println("\n========== MENU UTAMA ==========");
            System.out.println("1. Tampilkan semua putusan");
            System.out.println("2. Tambah putusan");
            System.out.println("3. Hapus putusan");
            System.out.println("4. Cari putusan");
            System.out.println("5. Filter putusan");
            System.out.println("6. Agregasi / statistik");
            System.out.println("7. Urutkan berdasarkan vonis");
            System.out.println("8. Audit");
            System.out.println("9. Ekspor CSV");
            System.out.println("10. Sisipkan putusan");
            System.out.println("11. Trace pointer");
            System.out.println("0. Keluar");
            System.out.println("===============================");
            System.out.print("Pilih menu: ");

            String pilih = in.nextLine().trim();

            try {
                switch (pilih) {

                    case "1": {
                        System.out.print(svc.tampilkan(svc.getArsip()));
                        System.out.println("size=" + svc.getArsip().size());
                        break;
                    }

                    case "2": {
                        System.out.println("Masukkan 1 baris CSV (16 kolom):");
                        svc.addFromFields(CsvUtil.parseLine(in.nextLine()));
                        System.out.println("Ditambahkan. size=" + svc.getArsip().size());
                        break;
                    }

                    case "3": {
                        System.out.print("Posisi: ");
                        int pos = Integer.parseInt(in.nextLine().trim());

                        System.out.println("Dihapus: " + svc.removeAt(pos).ringkas());
                        break;
                    }

                    case "4": {
                        System.out.print("Nomor perkara / caseId: ");
                        SearchResult s = svc.cari(in.nextLine());

                        if (s.found()) {
                            System.out.println("Ditemukan pada indeks " + s.index
                                    + " dalam " + s.steps + " langkah:");
                            System.out.println(s.putusan.ringkas());
                        } else {
                            System.out.println("Tidak ditemukan setelah "
                                    + s.steps + " langkah.");
                        }
                        break;
                    }

                    case "5": {
                        FilterCriteria c = bacaFilter(in);
                        SinglyLinkedList<Putusan> h = svc.filter(c);

                        System.out.print(svc.tampilkan(h));
                        System.out.println("Hasil: " + h.size());
                        break;
                    }

                    case "6": {
                        System.out.print(svc.aggregate(svc.getArsip()).ringkas());
                        break;
                    }

                    case "7": {
                        System.out.print("Menaik? (y/n): ");
                        boolean asc = in.nextLine().trim().equalsIgnoreCase("y");

                        System.out.print(svc.tampilkan(
                                svc.urutkanBerdasarkanVonis(asc)));
                        break;
                    }

                    case "8": {
                        System.out.print(svc.tampilkanAudit());
                        break;
                    }

                    case "9": {
                        System.out.print("Nama file keluaran: ");
                        svc.eksporCsv(
                                svc.getArsip(),
                                Paths.get(in.nextLine().trim())
                        );
                        System.out.println("Tersimpan.");
                        break;
                    }

                    case "10": {
                        System.out.print("Posisi: ");
                        int pos = Integer.parseInt(in.nextLine().trim());

                        System.out.println("Masukkan 1 baris CSV (16 kolom):");
                        svc.insertAt(
                                pos,
                                Putusan.fromFields(
                                        CsvUtil.parseLine(in.nextLine())
                                )
                        );

                        System.out.println("Disisipkan. size="
                                + svc.getArsip().size());
                        break;
                    }

                    case "11": {
                        System.out.println(
                                svc.getArsip().traceString(Putusan::getCaseId)
                        );
                        break;
                    }

                    case "0": {
                        jalan = false;
                        break;
                    }

                    default: {
                        System.out.println("Pilihan tidak dikenal.");
                    }
                }

                svc.getArsip().validateInvariant();

            } catch (IllegalArgumentException
                     | IndexOutOfBoundsException
                     | IOException e) {
                System.out.println("Terjadi kesalahan: " + e.getMessage());
            }
        }

        System.out.println("Selesai.");
    }

    private static FilterCriteria bacaFilter(Scanner in) {
        FilterCriteria c = new FilterCriteria();

        System.out.println("Kosongkan isian untuk melewati kriteria.");

        System.out.print("Jenis narkotika: ");
        String s = in.nextLine().trim();
        if (!s.isEmpty()) {
            c.jenisNarkotika = s;
        }

        System.out.print("Berat min (g): ");
        s = in.nextLine().trim();
        if (!s.isEmpty()) {
            c.minBerat = Double.valueOf(s);
        }

        System.out.print("Berat maks (g): ");
        s = in.nextLine().trim();
        if (!s.isEmpty()) {
            c.maxBerat = Double.valueOf(s);
        }

        System.out.print("Pasal terbukti: ");
        s = in.nextLine().trim();
        if (!s.isEmpty()) {
            c.pasalTerbukti = s;
        }

        System.out.print("Vonis min (bln): ");
        s = in.nextLine().trim();
        if (!s.isEmpty()) {
            c.minVonis = Integer.valueOf(s);
        }

        System.out.print("Vonis maks (bln): ");
        s = in.nextLine().trim();
        if (!s.isEmpty()) {
            c.maxVonis = Integer.valueOf(s);
        }

        return c;
    }
}
