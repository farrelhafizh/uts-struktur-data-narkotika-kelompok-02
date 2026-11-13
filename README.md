# Aplikasi Arsip Putusan Narkotika – PN Kisaran

UTS Struktur Data – Kelompok 02
Universitas Muhammadiyah Malang – Semester Ganjil 2026/2027
Dosen pengampu: Galih W. Wicaksono

## Anggota Kelompok

| Nama                              | NIM             | Akun GitHub   |
|-----------------------------------|-----------------|---------------|
| Muhammad Aqilah Sardi Syaputra    | 202510370110004 | @aqilahputra  |
| Mohammad Farrel Hafizh Al Bintang | 202510370110012 | @farrelhafizh |

Rincian kontribusi (Issue, Pull Request, review): [`CONTRIBUTIONS.md`](CONTRIBUTIONS.md).

## Deskripsi

Aplikasi konsol Java berorientasi objek untuk menyimpan dan mengolah 50 putusan
pengadilan narkotika Pengadilan Negeri Kisaran [isi rentang tahun] menggunakan struktur data
yang dibuat dari nol, generik `<T>`, tanpa koleksi bawaan `java.util`.

Fitur: impor CSV, tampil, tambah, hapus, cari, filter tunggal/gabungan, agregasi,
pengurutan berdasarkan vonis, audit riwayat operasi, dan ekspor hasil ke CSV/TXT.

## Dataset

- 50 putusan pidana narkotika Pengadilan Negeri Kisaran:
  PF001–PF025 (extractor Muhammad Aqilah Sardi Syaputra) dan PF026–PF050 (extractor Mohammad Farrel Hafizh Al Bintang)
- Data terstruktur: [`data/putusan-50.csv`](data/putusan-50.csv) (16 atribut sesuai skema 2.2)
- Sumber per atribut: [`docs/data-provenance.csv`](docs/data-provenance.csv)
  (9 atribut × 50 putusan = 450 baris, diperiksa silang antaranggota)
- PDF sumber 50 putusan (akses publik): [https://drive.google.com/drive/folders/1F56ugek8B3KhDnxQ0Ld_VltUUxa7-dOH?usp=sharing]
  — nama file `putusan_001.pdf` … `putusan_050.pdf` sama dengan kolom `source_file` di provenance
- Nama terdakwa disamarkan dengan kode T001–T050

## Aturan Data

| No | Atribut / Situasi | Aturan | Contoh |
|---:|---|---|---|
| 1 | `caseId` | PF + 3 digit, sama dengan nomor file | `PF001` |
| 2 | `nomorPerkara` | Salin persis dari halaman 1 | `761/Pid.Sus/2022/PN Kis` |
| 3 | `pengadilan` | Selalu sama | `PN Kisaran` |
| 4 | `tanggalPutusan` | Tanggal dibacakan/diucapkan, format tahun-bulan-tanggal | `2022-12-08` |
| 5 | `terdakwaKode` | T + nomor case, nama asli tidak boleh ditulis | `T001` |
| 6 | `jenisNarkotika` | Huruf kecil: sabu / ganja / ekstasi / lainnya; lebih dari satu dipisah garis tegak | `sabu;ganja` |
| 7 | `beratBarangBuktiGram` | Jumlah berat BERSIH semua narkotika (Berita Acara Lab), desimal pakai titik | `6.493` |
| 8 | Berat hanya bruto atau hanya jumlah butir | Isi 0, status provenance = missing | `0` |
| 9 | `pasalDakwaan` | Semua pasal di dakwaan, dipisah garis tegak, format pasal(ayat) | `114(1) jo 132(1)\|112(1) jo 132(1)` |
| 10 | `pasalTerbukti` | Pasal di amar; jika amar hanya menyebut “dakwaan kedua”, lihat pasal dakwaan kedua | `112(1) jo 132(1)` |
| 11 | `tuntutanBulan` | Tuntutan jaksa dalam bulan (tahun × 12 + bulan) | `60` |
| 12 | `vonisBulan` | Pidana penjara di amar dalam bulan; kurungan pengganti denda TIDAK dihitung | `48` |
| 13 | Vonis rehabilitasi | `vonisBulan = 0`, tulis “rehabilitasi” di barangBukti | `0` |
| 14 | `dendaRupiah` | Angka saja tanpa titik/Rp; tidak ada denda = 0 | `800000000` |
| 15 | `barangBukti` | Ringkasan barang bukti utama, maksimal ±10 kata | `12 paket kecil sabu, 1 plastik klip kosong, 1 handphone Nokia hitam` |
| 16 | `keadaanMemberatkan / Meringankan` | Ringkas dengan kata sendiri, maksimal ±15 kata | `Memberatkan: Perbuatan terdakwa bertentangan dengan program pemerintah yang sedang giat memberantas peredaran gelap narkotika. Meringankan: Sopan di persidangan; menyesali dan mengakui perbuatannya; belum pernah dihukum.` |
| 17 | `sourcePages` | Halaman per kelompok atribut, format bagian:halaman dipisah titik koma | `identitas:1;dakwaan:4-6;tuntutan:2;laboratorium:4;pertimbangan:18-19;amar:19-20` |
| 18 | Nilai tidak ditemukan | Angka = 0, teks = dikosongkan; jangan menebak | `0` |
| 19 | Status provenance | extracted = baru diisi; verified = sudah dicek reviewer; corrected = diperbaiki reviewer; missing = tidak ada di putusan | `extracted` |
| 20 | Vonis/tuntutan seumur hidup | `tuntutanBulan / vonisBulan = 999` | `0` |
| 21 | Tuntutan pidana mati | `tuntutanBulan = 9999` | `0` |
| 22 | Agregasi (rata-rata, total) vonis/tuntutan | Nilai 999 dan 9999 dikecualikan dari perhitungan rata-rata; tetap dipakai saat pengurutan | `0` |
| 23 | Vonis seumur hidup / mati tanpa denda | `dendaRupiah = 0` | `0` |
| 24 | Pidana kurungan pengganti denda | Tidak dihitung ke `vonisBulan` | `2 bulan` |
| 25 | Lebih dari satu jenis narkotika | Jenis dipisah garis tegak; berat = jumlah berat bersih semuanya | `0` |
| 26 | Jenis selain sabu/ganja/ekstasi | Tulis “lainnya” (tembakau sintetis, mefedron, dll.) | `0` |
| 27 | Barang bukti hanya sisa pakai (alat hisap, pipa kaca) | `beratBarangBuktiGram = 0`, status provenance = missing | `0` |
| 28 | Putusan menyebut “total keseluruhan berat bersih” | Pakai angka total yang tertulis, walau rinciannya tidak pas dijumlah | `0` |
| 29 | Satu putusan dengan beberapa terdakwa | Tetap 1 record; kode terdakwa satu (T + nomor case); vonis diambil dari amar (biasanya “masing-masing” sama) | `0` |
| 30 | Perkara terpisah dengan barang bukti yang sama | Masing-masing tetap 1 record dengan berat yang sama | `2022-12-08` |
| 31 | Tanggal diucapkan “pada hari itu juga” | `tanggalPutusan = tanggal musyawarah` | `0` |
| 32 | Tanggal diucapkan lebih awal dari tanggal musyawarah (salah ketik di putusan) | Pakai tanggal musyawarah, beri catatan di provenance | `0` |
| 33 | Nomor perkara tahun berbeda dengan tahun putusan | Nomor disalin persis; `tanggalPutusan` tetap tanggal putusan | `Pasal 114(1) jo 132(1); Pasal 112(1) jo 132(1)` |
| 34 | Pasal dengan jo Pasal 132 atau KUHP | Ditulis lengkap setelah pasal utama | `alternatif` |
| 35 | Dakwaan berbentuk primair-subsidair atau alternatif | Semua pasal ditulis berurutan sesuai dakwaan | `0` |
| 36 | Angka di amar tidak jelas/terpotong (“selama (enam) tahun”) | Ambil angka dari ejaan dalam kurung, beri catatan, wajib dicek reviewer | `0` |

### Format dan konversi

| No | Atribut / situasi | Aturan | Contoh |
|---|---|---|---|
| 1 | caseId | PF + 3 digit, sama dengan nomor file | PF001 |
| 2 | nomorPerkara | Salin persis dari halaman 1 | 761/Pid.Sus/2022/PN Kis |
| 3 | pengadilan | Selalu sama | PN Kisaran |
| 4 | tanggalPutusan | Tanggal dibacakan/diucapkan, format tahun-bulan-tanggal (`YYYY-MM-DD`) | 2022-12-08 |
| 5 | terdakwaKode | T + nomor case; nama asli tidak boleh ditulis | T001 |
| 6 | jenisNarkotika | Huruf kecil: sabu / ganja / ekstasi / lainnya; lebih dari satu dipisah garis tegak `\|` | sabu\|ganja |
| 7 | beratBarangBuktiGram | Jumlah berat **bersih** semua narkotika (Berita Acara Lab), desimal pakai titik | 6.493 |
| 8 | Berat hanya bruto atau hanya jumlah butir | Isi 0, status provenance = `missing` | 0 |
| 9 | pasalDakwaan | Semua pasal di dakwaan, dipisah garis tegak, format pasal(ayat) | 114(1) jo 132(1)\|112(1) jo 132(1) |
| 10 | pasalTerbukti | Pasal di amar; jika amar hanya menyebut "dakwaan kedua", lihat pasal dakwaan kedua | 112(1) jo 132(1) |
| 11 | tuntutanBulan | Tuntutan jaksa dalam bulan (tahun × 12 + bulan) | 60 |
| 12 | vonisBulan | Pidana penjara di amar dalam bulan; kurungan pengganti denda **tidak** dihitung | 48 |
| 13 | Vonis rehabilitasi | vonisBulan = 0, tulis "rehabilitasi" di barangBukti | — |
| 14 | dendaRupiah | Angka saja tanpa titik/Rp; tidak ada denda = 0 | 800000000 |
| 15 | barangBukti | Ringkasan barang bukti utama, maksimal ±10 kata | 12 paket kecil sabu, 1 plastik klip kosong, 1 handphone Nokia hitam |
| 16 | keadaanMemberatkan / Meringankan | Ringkas dengan kata sendiri, maksimal ±15 kata | Memberatkan: Perbuatan terdakwa bertentangan dengan program pemerintah yang sedang giat memberantas peredaran gelap narkotika. Meringankan: Sopan di persidangan; menyesali dan mengakui perbuatannya; belum pernah dihukum. |
| 17 | sourcePages | Halaman per kelompok atribut, format `bagian:halaman` dipisah titik koma | identitas:1;dakwaan:4-6;tuntutan:2;laboratorium:4;pertimbangan:18-19;amar:19-20 |
| 18 | Nilai tidak ditemukan | Angka = 0, teks = dikosongkan; jangan menebak | — |

### Kode khusus

| No | Situasi | Aturan | Contoh |
|---|---|---|---|
| 19 | Status provenance | `extracted` = baru diisi; `verified` = sudah dicek reviewer; `corrected` = diperbaiki reviewer; `missing` = tidak ada di putusan | extracted |
| 20 | Vonis/tuntutan seumur hidup | tuntutanBulan / vonisBulan = **999** | — |
| 21 | Tuntutan pidana mati | tuntutanBulan = **9999** | — |
| 22 | Agregasi (rata-rata, total) vonis/tuntutan | Nilai 999 dan 9999 **dikecualikan** dari perhitungan rata-rata; tetap dipakai saat pengurutan | — |
| 23 | Vonis seumur hidup / mati tanpa denda | dendaRupiah = 0 | — |
| 24 | Pidana kurungan pengganti denda | Tidak dihitung ke vonisBulan | 2 bulan (tidak dihitung) |
| 25 | Lebih dari satu jenis narkotika | Jenis dipisah garis tegak; berat = jumlah berat bersih semuanya | sabu\|ganja |
| 26 | Jenis selain sabu/ganja/ekstasi | Tulis "lainnya" (tembakau sintetis, mefedron, dll.) | lainnya |
| 27 | Barang bukti hanya sisa pakai (alat hisap, pipa kaca) | beratBarangBuktiGram = 0, status provenance = `missing` | 0 |

### Kasus khusus

| No | Situasi | Aturan | Contoh |
|---|---|---|---|
| 28 | Putusan menyebut "total keseluruhan berat bersih" | Pakai angka total yang tertulis, walau rinciannya tidak pas dijumlah | — |
| 29 | Satu putusan dengan beberapa terdakwa | Tetap 1 record; kode terdakwa satu (T + nomor case); vonis diambil dari amar (biasanya "masing-masing" sama) | — |
| 30 | Perkara terpisah dengan barang bukti yang sama | Masing-masing tetap 1 record dengan berat yang sama | — |
| 31 | Tanggal diucapkan "pada hari itu juga" | tanggalPutusan = tanggal musyawarah | — |
| 32 | Tanggal diucapkan lebih awal dari tanggal musyawarah (salah ketik di putusan) | Pakai tanggal musyawarah, beri catatan di provenance | — |
| 33 | Nomor perkara tahun berbeda dengan tahun putusan | Nomor disalin persis; tanggalPutusan tetap tanggal putusan | — |
| 34 | Pasal dengan jo Pasal 132 atau KUHP | Ditulis lengkap setelah pasal utama | 114(2) jo 132(1) |
| 35 | Dakwaan berbentuk primair-subsidair atau alternatif | Semua pasal ditulis berurutan sesuai dakwaan | 114(1) jo 132(1)\|112(1) jo 132(1) |
| 36 | Angka di amar tidak jelas/terpotong ("selama (enam) tahun") | Ambil angka dari ejaan dalam kurung, beri catatan, wajib dicek reviewer | enam tahun = 72 |

### Status provenance

| Status | Arti |
|---|---|
| `extracted` | Nilai baru diisi oleh extractor, belum dicek reviewer |
| `verified` | Nilai sudah dicek reviewer dan sesuai dengan PDF |
| `corrected` | Nilai diperbaiki oleh reviewer |
| `missing` | Nilai tidak tersedia di putusan (ditulis 0 / kosong) |

## Struktur Data

| | Struktur utama | Struktur sekunder |
|---|---|---|
| Kelas | `ArrayDinamis<T>` | `StackRiwayat<T>` |
| Dasar | array `T[]` | array `T[]` |
| Kontrak | `implements StrukturData<T>` | operasi Stack: `push`, `pop`, `peek`, `searchPreserve` |
| Kapasitas | awal 10, resize ×2 saat penuh (10 → 20 → 40 → 80) | awal 10, resize ×2 saat penuh |
| Isi | 50 objek `Putusan` | riwayat operasi pengguna (`String`) |
| Dipakai untuk | impor, tampil, tambah, hapus, cari, filter, agregasi, urut, ekspor | fitur audit (tampil dan cari riwayat) |
| Invariant (`validateInvariant()`) | `0 ≤ size ≤ kapasitas`; slot `0..size-1` tidak null; slot `size..kapasitas-1` null | `0 ≤ top ≤ kapasitas`; slot `0..top-1` tidak null; slot `top..kapasitas-1` null |

Alasan pemilihan: [`docs/adr/ADR-001-pemilihan-struktur-data.md`](docs/adr/ADR-001-pemilihan-struktur-data.md).
Alasan algoritma cari dan urut: [`docs/adr/ADR-002-algoritma-cari-dan-urut.md`](docs/adr/ADR-002-algoritma-cari-dan-urut.md).

`java.util` hanya dipakai untuk kebutuhan non-struktur: `java.util.function.Predicate` / `Consumer`
(kriteria pencarian dan tujuan impor). Input/output memakai `java.io` dan `java.nio.file`,
tanggal memakai `java.time.LocalDate`.

## Cara Build, Test, dan Menjalankan

### Kebutuhan
- JDK 17 atau lebih baru
- Maven 3.8+ (atau Maven bawaan IntelliJ IDEA)

### Lewat terminal (dari folder proyek)

```
git clone https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02.git
cd uts-struktur-data-narkotika-kelompok-02
mvn clean test
mvn compile
java -cp target/classes id.ac.umm.kelompok02.aplikasi.Main
```

Hasil yang diharapkan dari `mvn clean test`: **Tests run: 89, Failures: 0, Errors: 0 – BUILD SUCCESS**.

Program memuat `data/putusan-50.csv` secara otomatis saat mulai. Untuk memuat file lain:

```
java -cp target/classes id.ac.umm.kelompok02.aplikasi.Main path/ke/file.csv
```

### Lewat IntelliJ IDEA
1. **File → Open** → pilih folder proyek (IntelliJ mengenali `pom.xml` sebagai proyek Maven).
2. Test: panel **Maven** → `Lifecycle` → `test`.
3. Menjalankan: buka `src/main/java/id/ac/umm/kelompok02/aplikasi/Main.java` → klik ▶ → **Run**.
   *Working directory* harus folder proyek supaya `data/putusan-50.csv` ditemukan.

## Struktur Proyek

```
├── data/
│   └── putusan-50.csv              dataset 50 putusan (16 atribut)
├── docs/
│   ├── adr/                        ADR-001 (struktur data), ADR-002 (algoritma)
│   ├── trace/                      trace manual skenario 3 dan 6, skenario 5 vs provenance
│   ├── demo/                       bahan demo
│   ├── data-provenance.csv         sumber setiap nilai (file, halaman, extractor, reviewer)
│   ├── skenario-uji.md             peta 7 skenario uji ke test
│   └── analisis-kompleksitas.md    Big-O dan data empiris
├── src/main/java/id/ac/umm/kelompok02/
│   ├── struktur/   StrukturData, ArrayDinamis, StackRiwayat
│   ├── model/      Putusan
│   ├── io/         PutusanCsvParser, HasilImpor, DataTidakValidException
│   └── aplikasi/   ArsipPutusan, PencarianPutusan, HasilCari, FilterPutusan, KriteriaFilter,
│                   AgregasiPutusan, JumlahJenis, PengurutanPutusan, AuditRiwayat,
│                   EksporPutusan, MenuKonsol, Main
├── src/test/java/id/ac/umm/kelompok02/   test JUnit 5 (paket sama dengan kode)
├── AI_USAGE.md
├── CONTRIBUTIONS.md
└── pom.xml
```

## Fitur dan Menu

| Menu | Fitur | Struktur / operasi yang dipakai |
|---|---|---|
| 1 | Impor CSV | `PutusanCsvParser` + `ArrayDinamis.add` |
| 2 | Tampil semua | `get(i)` untuk `i = 0..size-1` |
| 3 | Tambah putusan (satu baris CSV) | validasi parser + `contains` (tolak caseId ganda) + `add` |
| 4 | Hapus putusan berdasarkan caseId | `cari` + `remove` (elemen di kanan bergeser ke kiri) |
| 5 | Cari caseId / nomor perkara | linear search, menampilkan jumlah langkah perbandingan |
| 6 | Filter jenis, pasal terbukti, berat, vonis (tunggal atau gabungan) | `KriteriaFilter`, hasil di `ArrayDinamis` baru |
| 7 | Agregasi: jumlah perkara, rata-rata vonis, total denda, distribusi jenis | iterasi O(n), kode 999/9999 dikecualikan dari rata-rata |
| 8 | Urut berdasarkan vonis (menurun / menaik) | insertion sort stabil pada salinan, menampilkan pencacah operasi |
| 9 | Audit: tampilkan / cari riwayat operasi | `StackRiwayat.lihatDariAtas` / `searchPreserve` |
| 10 | Ekspor hasil cari/filter/urut terakhir ke CSV, laporan agregasi ke TXT | ditulis ke folder `ekspor/` |
| 11 | Info struktur data | size, kapasitas, `validateInvariant()` kedua struktur |
| 0 | Keluar | |

Setiap operasi (kecuali melihat audit) dicatat ke `StackRiwayat` dengan format
`yyyy-MM-dd HH:mm:ss | JENIS | detail`, misalnya `2026-11-11 19:00:00 | HAPUS | PF030 dari indeks 29`.

Catatan menu 10: yang diekspor adalah **hasil saat operasi cari/filter/urut dijalankan**.
Jika putusan dihapus setelah filter, hasil filter yang sudah tersimpan tidak ikut berubah.

Contoh sesi singkat (isi baris data disesuaikan dengan `data/putusan-50.csv`):

```
Pilih menu: 5
Cari berdasarkan 1) caseId  2) nomor perkara: 1
Kata kunci: PF012
'PF012' ditemukan di indeks 11 (12 langkah perbandingan)
PF012 | <nomor perkara> | <tanggal> | <jenis> | <berat> g | vonis <x> th <y> bln | denda Rp<jumlah>

Pilih menu: 11
Struktur utama  : ArrayDinamis<Putusan>, size=50, kapasitas=80
Struktur sekunder: StackRiwayat<String>, size=2
validateInvariant() kedua struktur: OK
```

## Dokumentasi

| Dokumen | Isi |
|---|---|
| [`docs/adr/ADR-001-pemilihan-struktur-data.md`](docs/adr/ADR-001-pemilihan-struktur-data.md) | alasan memilih array dinamis + Stack |
| [`docs/adr/ADR-002-algoritma-cari-dan-urut.md`](docs/adr/ADR-002-algoritma-cari-dan-urut.md) | alasan linear search, insertion sort, dan searchPreserve |
| [`docs/analisis-kompleksitas.md`](docs/analisis-kompleksitas.md) | Big-O semua operasi dan data empiris |
| [`docs/skenario-uji.md`](docs/skenario-uji.md) | 7 skenario uji wajib |
| [`docs/trace/`](docs/trace/) | trace manual |
| [`docs/data-provenance.csv`](docs/data-provenance.csv) | sumber setiap nilai data |
| [`AI_USAGE.md`](AI_USAGE.md) | catatan penggunaan AI |
| [`CONTRIBUTIONS.md`](CONTRIBUTIONS.md) | kontribusi setiap anggota |

## Keterbatasan

- Pencarian O(n) dan pengurutan O(n²) memadai untuk 50 data, tetapi tidak untuk ribuan data
  (rencana perbaikan ada di ADR-002).
- Menu tambah hanya menambah di akhir arsip. Penyisipan di posisi tertentu (`tambahDi`)
  tersedia di kode dan dibuktikan oleh test skenario 4.
- Perubahan arsip dan riwayat tidak disimpan permanen. Saat program ditutup,
  arsip kembali ke isi `data/putusan-50.csv` (gunakan menu 10 untuk menyimpan hasil).
