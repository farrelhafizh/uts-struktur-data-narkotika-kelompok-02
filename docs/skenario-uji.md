
# Skenario Uji Wajib

Dokumen ini memetakan 7 skenario uji wajib ke test otomatis (JUnit 5) yang membuktikannya, beserta langkah demo manual melalui menu konsol.

Jalankan semua test:

```bash
mvn clean test
```

Jalankan satu kelas test saja, misalnya:

```bash
mvn test -Dtest=TambahHapusTest
```

Total pengujian berdasarkan dokumentasi proyek sebelumnya: **89 test, 0 gagal**. Jumlah tersebut perlu diverifikasi kembali dengan menjalankan `mvn clean test` setelah penyesuaian dataset dan implementasi.

## Ringkasan

| No | Skenario | Test yang membuktikan | Hasil yang diharapkan |
|---|---|---|---|
| 1 | Muat 50 record | `ArsipPutusanTest#impor50Record`, `PutusanCsvParserTest#fileAsliBerisi50Record` | 50 baris CSV terbaca. Terdapat 25 record lengkap dan 25 record belum lengkap. Jumlah diterima dan ditolak mengikuti aturan validasi parser. |
| 2 | Cari: ada, tidak ada, batas | `PencarianPutusanTest#caseIdAda`, `#caseIdTidakAda`, `#posisiBatas`; `ArrayDinamisTest#searchDitemukan`, `#searchTidakAda` | PF012 → indeks 11, 12 langkah; PF099 → -1, 50 langkah; PF001 → indeks 0, 1 langkah; PF050 → indeks 49, 50 langkah. |
| 3 | Hapus awal, tengah, akhir + pergeseran | `TambahHapusTest#hapusPertamaTengahTerakhir`; `ArrayDinamisTest#hapusPertama`, `#hapusTengah`, `#hapusTerakhir` | Hapus PF001 → PF002 pindah ke indeks 0; hapus PF026 → PF027 pindah ke indeks 24; hapus PF050 → PF049 menjadi elemen terakhir; size 47. |
| 4 | Tambah kembali 3 record, invariant dan urutan | `TambahHapusTest#tambahKembaliTiga`; `ArrayDinamisTest#sisipKembali` | Setelah PF001, PF026, PF050 disisipkan kembali: size 50, urutan PF001..PF050, `validateInvariant()` lolos. |
| 5 | Filter gabungan vs provenance | `FilterPutusanTest#gabungan`; `MenuKonsolTest#filterGabungan` | Sabu + pasal terbukti diawali Pasal 112 ayat (1) + vonis 48–60 bulan + berat 0–2 gram → PF002, PF004, PF007, PF018, PF020, PF024, PF025. |
| 6 | Cari di Stack tidak mengubah urutan | `StackRiwayatTest#searchPreserveDitemukan`, `#searchPreserveTidakAda`; `AuditRiwayatTest#cariTidakMengubahUrutan` | `gambarIsi()` sebelum dan sesudah pencarian sama; size tetap. |
| 7 | Record tidak valid → pesan error, struktur konsisten | `TambahHapusTest#tambahDariCsvTidakValid`; `PutusanCsvParserTest#fileDenganBarisRusak` dan test validasi lainnya; `MenuKonsolTest#inputTidakValid` | Pesan menyebut kolom yang salah; data yang sudah ada tidak berubah; `validateInvariant()` lolos. |

**Catatan:** Skenario 2–4 menggunakan asumsi bahwa seluruh 50 record berhasil dimuat. Jika parser menolak record yang belum lengkap, indeks, jumlah data, dan hasil pengujian harus disesuaikan.

## Rincian per Skenario

### Skenario 1: Muat 50 Record

- **Data:** `data/putusan-50.csv` (header + 50 baris).
- **Identitas:** PF001 sampai PF050.
- **Bukti:** `impor()` memanggil `PutusanCsvParser.bacaFile()`, lalu setiap baris valid dimasukkan dengan `ArrayDinamis.add()`.
- **Kondisi CSV:** Terdapat 50 baris dengan kode unik, tetapi hanya 25 record yang memiliki seluruh atribut terisi.
- **Data tambahan:** PF026–PF034 memiliki beberapa atribut dasar yang terisi, sedangkan PF035–PF050 memiliki lebih banyak kolom kosong.
- **Kapasitas:** Jika seluruh 50 baris berhasil dimasukkan, kapasitas berubah dari 10 → 20 → 40 → 80 (resize ×2 sebanyak 3 kali).
- **Demo menu:** Program menampilkan jumlah data diterima dan ditolak saat mulai, kemudian menu **11** menampilkan size dan kapasitas.

**Catatan validasi:**

- Jika parser menerima record parsial, jumlah data dapat mencapai 50.
- Jika parser mensyaratkan seluruh atribut wajib terisi, hanya 25 record lengkap yang dapat diterima.
- Jika hanya 25 record diterima, kapasitas akhir dengan kapasitas awal 10 dan resize ×2 adalah 40, bukan 80.
- Jumlah diterima dan ditolak harus mengikuti implementasi parser yang sebenarnya.

### Skenario 2: Pencarian

- **Algoritma:** Linear search pada `ArrayDinamis`, dengan langkah perbandingan dihitung.
- **Kasus:** Ada (PF012), tidak ada (PF099), dan batas (PF001 sebagai elemen pertama, PF050 sebagai elemen terakhir).

| Kasus | Kunci | Indeks | Langkah |
|---|---|---:|---:|
| Terbaik | PF001 | 0 | 1 |
| Rata-rata (contoh) | PF012 | 11 | 12 |
| Terburuk (ada) | PF050 | 49 | 50 |
| Terburuk (tidak ada) | PF099 | -1 | 50 |

Rata-rata pencarian untuk kunci yang ada = (n + 1) / 2 = 25,5 langkah, jika seluruh 50 record dimuat.

- **Demo menu:** **5** → **1** → `PF012`, lalu ulangi dengan `PF099`.

Catatan: Pencarian berdasarkan `caseId` tetap dapat dilakukan meskipun atribut lain pada record belum lengkap, selama parser menerima record tersebut.

### Skenario 3: Hapus dengan Pergeseran

- **Bukti pergeseran:** Setelah `remove(0)`, elemen yang semula di indeks 1 berada di indeks 0.
- Slot terakhir di-set `null` supaya tidak ada referensi tertinggal.

| Tahap | Operasi | Hasil |
|---|---|---|
| 1 | Hapus PF001 | PF002 pindah ke indeks 0 |
| 2 | Hapus PF026 | PF027 pindah ke indeks 24 |
| 3 | Hapus PF050 | PF049 menjadi elemen terakhir |
| Akhir | Pemeriksaan size | 47 |

- **Trace manual:** Lihat `docs/trace/trace-skenario-3.md`.
- **Demo menu:** **4** → `PF001`, **4** → `PF026`, **4** → `PF050`, lalu **2** untuk melihat urutan.

Catatan: Urutan dan indeks di atas berlaku apabila seluruh 50 record berhasil dimuat sebelum penghapusan.

### Skenario 4: Tambah Kembali

- **Bukti:** `insert(0, PF001)`, `insert(25, PF026)`, dan `add(PF050)` mengembalikan urutan semula PF001..PF050.
- **Kondisi awal:** Setelah ketiga penghapusan pada skenario 3, terdapat 47 record.
- **Kondisi akhir:** Setelah ketiga record ditambahkan kembali, jumlah menjadi 50 dan urutan arsip kembali seperti semula.
- **Catatan:** Menu **3** menambah di akhir (`add`). Penyisipan di posisi tertentu (`insert`) dibuktikan melalui test.

### Skenario 5: Filter Gabungan vs Provenance

**Kriteria filter:**

1. Jenis narkotika: `sabu`.
2. Pasal terbukti diawali `Pasal 112 ayat (1)`.
3. Lama vonis: 48–60 bulan.
4. Berat barang bukti: 0–2 gram.

**Hasil yang diharapkan:**

| Case ID | Jenis | Vonis | Berat barang bukti |
|---|---|---:|---:|
| PF002 | Sabu | 60 bulan | 1,0695 gram |
| PF004 | Sabu | 48 bulan | 1,2900 gram |
| PF007 | Sabu | 48 bulan | 0,0800 gram |
| PF018 | Sabu | 54 bulan | 0,4400 gram |
| PF020 | Sabu | 48 bulan | 1,1200 gram |
| PF024 | Sabu | 48 bulan | 1,7200 gram |
| PF025 | Sabu | 54 bulan | 0,0800 gram |

**Total hasil: 7 putusan.**

- **Pemeriksaan manual:** Cocokkan setiap putusan dengan kolom `sourcePages` dan dokumen sumber PDF melalui `docs/data-provenance.csv`.
- **Demo menu:** **6** → `sabu` → `112(1)` → `0` → `2` → `48` → `60`.

Catatan:

- Hasil di atas dihitung berdasarkan 25 record lengkap dalam CSV.
- Filter tidak boleh menganggap kolom kosong sebagai nilai nol.
- Pemeriksaan provenance harus menggunakan halaman sumber yang benar-benar tercatat pada CSV.

### Skenario 6: Pencarian di Stack

- **Algoritma:** `searchPreserve` memindahkan elemen ke stack sementara sampai elemen ditemukan, lalu memindahkannya kembali (`restore`).
- **Bukti:** `gambarIsi()` sebelum dan sesudah pencarian identik, termasuk saat elemen tidak ditemukan (seluruh stack dipindahkan lalu dikembalikan).
- **Trace manual:** Lihat `docs/trace/trace-skenario-6.md`.
- **Demo menu:** Jalankan beberapa operasi, lalu **9** → **1** (lihat riwayat), **9** → **2** → `CARI`, lalu **9** → **1** lagi. Urutan riwayat tetap sama.

### Skenario 7: Data Tidak Valid

- **Contoh:** Baris dengan tanggal `20-12-2024` (bukan format `YYYY-MM-DD`) ditolak dengan pesan yang menyebut `tanggalPutusan`.
- **Contoh tambahan:** Record dengan `caseId` kosong atau nilai numerik yang tidak valid harus ditolak sesuai aturan validasi.
- **Prinsip:** Validasi dilakukan oleh parser sebelum `add()`, sehingga struktur tidak pernah berisi data setengah jadi.
- **Demo menu:** **3** → tempel `PF051,rusak`, lalu **6** dengan berat minimum `x`, kemudian **11** untuk menunjukkan invariant tetap OK.

## Catatan Konsistensi Dataset

File CSV yang digunakan memiliki struktur sebagai berikut:

| Atribut | Jumlah terisi |
|---|---:|
| `caseId` | 50 |
| `nomorPerkara` | 34 |
| `pengadilan` | 50 |
| `tanggalPutusan` | 50 |
| `terdakwaKode` | 25 |
| `jenisNarkotika` | 25 |
| `beratBarangBuktiGram` | 25 |
| `pasalDakwaan` | 25 |
| `pasalTerbukti` | 25 |
| `tuntutanBulan` | 25 |
| `vonisBulan` | 25 |
| `dendaRupiah` | 25 |
| `barangBukti` | 25 |
| `keadaanMemberatkan` | 25 |
| `keadaanMeringankan` | 25 |
| `sourcePages` | 25 |

Ketentuan penggunaan data:

1. Setiap putusan menggunakan kode unik dengan format `PFxxx`, yaitu `PF001`–`PF050`.
2. Kode unik harus konsisten antara CSV, objek `Putusan`, test, dan tampilan menu.
3. Kolom yang kosong tidak boleh otomatis dianggap bernilai 0.
4. Pencarian berdasarkan `caseId` dapat menggunakan seluruh kode unik yang berhasil dimuat.
5. Filter dan pengurutan hanya memproses atribut yang telah memiliki nilai valid.
6. Pengujian harus menggunakan dataset yang sama dengan yang digunakan aplikasi.
7. Seluruh referensi kode unik dalam dokumentasi, CSV, implementasi Java, dan unit test harus menggunakan format `PFxxx`.

## Kesimpulan

Ketujuh skenario uji wajib digunakan untuk memastikan struktur data dan fitur aplikasi berjalan sesuai kebutuhan.

Pengujian mencakup proses impor, pencarian, penghapusan, penyisipan kembali, filter gabungan, pencarian Stack, dan validasi data tidak valid.

Dataset terbaru menyediakan 50 kode unik, dengan 25 record yang memiliki seluruh atribut lengkap. Oleh karena itu, setiap pengujian harus membedakan jumlah baris CSV, jumlah record yang berhasil diimpor, dan jumlah record yang memiliki atribut lengkap.

Seluruh hasil pengujian otomatis harus diverifikasi melalui `mvn clean test` sebelum dinyatakan lulus dan didokumentasikan sebagai hasil akhir proyek.
