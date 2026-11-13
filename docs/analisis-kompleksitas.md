
# Analisis Kompleksitas

Keterangan:
- `n` = jumlah entri arsip berdasarkan kode unik `caseId` (50).
- `v` = jumlah putusan dengan nilai `vonisBulan` terisi (25).
- `h` = jumlah entri riwayat di Stack.
- `k` = jumlah jenis narkotika berbeda.
- Data CSV memiliki 50 kode unik, tetapi hanya 25 putusan yang memiliki data lengkap. Sebanyak 25 entri lainnya belum memiliki nilai vonis dan beberapa atribut lainnya.

## 1. ArrayDinamis<T> (Struktur Utama)

| Operasi | Waktu | Ruang tambahan | Alasan |
|---|---|---|---|
| `add(x)` | O(1) amortized, O(n) saat resize | O(n) saat resize | Menulis di `data[size]`; saat penuh, array baru 2× kapasitas dan elemen disalin |
| `get(i)` / `set(i, x)` | O(1) | O(1) | Akses langsung ke indeks array |
| `insert(i, x)` | O(n) | O(1) | Elemen `i..size-1` digeser satu ke kanan |
| `remove(i)` | O(n) | O(1) | Elemen `i+1..size-1` digeser satu ke kiri; hapus akhir O(1) |
| `search(x)` / `cari(p)` / `contains(x)` | O(n) | O(1) | Linear search, data tidak terurut berdasarkan kunci |
| `size()` / `isEmpty()` | O(1) | O(1) | Membaca field `size` |
| `clear()` | O(kapasitas) | O(1) | Semua slot di-set null |
| `salin()` | O(n) | O(n) | Menyalin isi ke ArrayDinamis baru |
| `validateInvariant()` | O(kapasitas) | O(1) | Memeriksa slot aktif dan slot kosong |

### Bukti Amortized pada `add`

Kapasitas awal 10, resize ×2. Saat memuat 50 putusan:

| Resize ke- | Terjadi saat add ke- | Kapasitas | Elemen disalin |
|---|---|---|---|
| 1 | 11 | 10 → 20 | 10 |
| 2 | 21 | 20 → 40 | 20 |
| 3 | 41 | 40 → 80 | 40 |

Total kerja = 50 penulisan + 70 salinan = **120 ≤ 3n**, sehingga rata-rata per `add` konstan: **O(1) amortized**.

Bukti: menu **11** menampilkan `size=50, kapasitas=80`.

### Pergeseran pada `remove` (Skenario 3)

Jumlah pergeseran = `size − 1 − i`.

| Posisi | Contoh (n = 50) | Pergeseran |
|---|---|---|
| Awal (i = 0) | Hapus PF001 | 49 (terburuk) |
| Tengah (i = 24) | Hapus PF026 setelah PF001 dihapus | 24 |
| Akhir (i = size − 1) | Hapus PF050 | 0 (terbaik) |

Trace langkah demi langkah: `docs/trace/trace-skenario-3.md`.

## 2. StackRiwayat<T> (Struktur Sekunder)

| Operasi | Waktu | Ruang tambahan | Alasan |
|---|---|---|---|
| `push(x)` | O(1) amortized | O(h) saat resize | Sama seperti `add` |
| `pop()` / `peek()` | O(1) | O(1) | Hanya mengubah atau membaca `top` |
| `lihatDariAtas(m)` | O(1) | O(1) | Akses `data[top − m]` |
| `searchPreserve(p)` | O(h) | O(h) | Maksimal h pop ke stack sementara + h push saat restore |
| `restore(s)` | O(k) | O(1) | k elemen dipindah kembali |

`searchPreserve` terburuk (elemen tidak ada): 2h pop + 2h push = 4h operasi, tetap **O(h)**.

Trace: `docs/trace/trace-skenario-6.md`.

## 3. Fitur Aplikasi

| Fitur | Kelas / method | Waktu | Catatan |
|---|---|---|---|
| Impor | `ArsipPutusan.impor` | O(n) | Parse tiap baris + `add` O(1) amortized |
| Tampil | `ArsipPutusan.tampil` | O(n) | `get(i)` untuk i = 0..n−1 |
| Tambah | `ArsipPutusan.tambah` | O(n) | `contains` untuk menolak duplikat O(n) + `add` O(1) |
| Tambah di posisi | `ArsipPutusan.tambahDi` | O(n) | `contains` + `insert` |
| Hapus by caseId | `ArsipPutusan.hapus` | O(n) | `cari` O(n) + `remove` O(n) |
| Cari caseId / nomor perkara | `PencarianPutusan` | O(n) | Linear search |
| Filter (tunggal/gabungan) | `FilterPutusan.filter` | O(n) | Setiap putusan diperiksa sekali; hasil di ArrayDinamis baru, ruang O(n) |
| Agregasi | `AgregasiPutusan` | O(n·k) | Distribusi jenis: untuk tiap putusan, cari jenisnya di ArrayDinamis berisi k jenis |
| Urut vonis | `PengurutanPutusan` | O(v²) terburuk, O(v) terbaik | Insertion sort pada salinan; ruang O(v) |
| Audit tampil | `AuditRiwayat.tampilkan(m)` | O(m) | `lihatDariAtas` O(1) per entri |
| Audit cari | `AuditRiwayat.cari` | O(h) | `searchPreserve` |
| Ekspor CSV/TXT | `EksporPutusan` | O(n) | Satu baris per putusan |

Catatan:
- Pengurutan hanya memproses putusan yang mempunyai nilai `vonisBulan`.
- Pada CSV saat ini, terdapat 25 nilai vonis yang terisi (`v = 25`).
- Nilai vonis yang kosong tidak boleh diperlakukan sebagai nilai 0.
- Jika data yang belum lengkap tidak dimasukkan ke arsip, nilai n pada fitur aplikasi perlu mengikuti jumlah data yang benar-benar berhasil diimpor.
- Untuk data jenis narkotika yang terisi saat ini, seluruh 25 putusan memiliki jenis narkotika Sabu (`k = 1`).

## 4. Data Empiris

### 4.1 Pencarian Linear

Pengujian menggunakan 50 kode unik `PF001` sampai `PF050`, dengan asumsi seluruh entri berkode tersebut dimuat ke dalam ArrayDinamis.

| Kasus | Kunci | Indeks | Langkah | Test |
|---|---|---|---|---|
| Terbaik | PF001 | 0 | 1 | `PencarianPutusanTest#posisiBatas` |
| Rata-rata (contoh) | PF012 | 11 | 12 | `PencarianPutusanTest#caseIdAda` |
| Terburuk (ada) | PF050 | 49 | 50 | `PencarianPutusanTest#posisiBatas` |
| Terburuk (tidak ada) | PF099 | -1 | 50 | `PencarianPutusanTest#caseIdTidakAda` |

Rata-rata untuk kunci yang ada = (n + 1) / 2 = 25,5 langkah, jadi **O(n)**.

Catatan: Pencarian berdasarkan `nomorPerkara` hanya dapat menemukan entri yang memiliki nomor perkara terisi. Pada CSV saat ini terdapat 34 nomor perkara yang terisi.

### 4.2 Insertion Sort Berdasarkan Vonis

Pengujian menggunakan 25 nilai `vonisBulan` yang tersedia pada CSV.

| Kasus | Perbandingan | Pergeseran |
|---|---:|---:|
| Data asli, urut menurun | 124 | 103 |
| Data asli, urut menaik | 160 | 138 |
| Data sudah terurut (terbaik) | 24 (= v − 1) | 0 |
| Batas teoretis terburuk | 300 (= v(v−1)/2) | 300 |

Data asli memiliki beberapa nilai vonis yang sama, terutama 48 dan 60 bulan. Insertion sort dipilih karena **stabil**, sehingga putusan dengan vonis sama tetap mengikuti urutan arsip.

Pengurutan dilakukan pada salinan agar urutan arsip asli tidak berubah.

### 4.3 Perbandingan Algoritma Pengurutan

Berdasarkan 25 nilai vonis pada CSV:

| Algoritma | Perbandingan (menurun) | Perpindahan (menurun) | Perbandingan (menaik) | Perpindahan (menaik) | Stabil? | Ruang tambahan |
|---|---:|---:|---:|---:|---|---|
| **Insertion Sort (dipakai)** | **124** | **103 pergeseran** | **160** | **138 pergeseran** | Ya | O(1) |
| Bubble Sort (berhenti dini) | 264 | 103 tukar | 294 | 138 tukar | Ya | O(1) |
| Selection Sort | 300 | 22 tukar | 300 | 24 tukar | Tidak | O(1) |
| Merge Sort | O(v log v) | - | O(v log v) | - | Ya | O(v) |

Insertion sort membutuhkan lebih sedikit perbandingan dibandingkan Bubble Sort pada data yang sama. Merge sort memiliki kompleksitas asimtotik lebih baik, tetapi membutuhkan ruang tambahan O(v) dan implementasi lebih kompleks.

### 4.4 Penanganan Nilai Khusus Vonis

Nilai `999` digunakan untuk merepresentasikan vonis seumur hidup.

Nilai tersebut tetap harus diikutsertakan dalam pengurutan dan tidak boleh dianggap sebagai data kosong.

Pada 25 nilai vonis yang tersedia di CSV saat ini, belum ditemukan nilai `999`. Penanganannya tetap disiapkan dalam implementasi untuk data berikutnya.

## 5. Mengapa Kompleksitas Ini Dapat Diterima

- n = 50, sehingga operasi O(n²) terburuk untuk insertion sort pada seluruh 50 data secara teoretis adalah 1.225 perbandingan. Namun, pengujian aktual saat ini hanya menggunakan 25 nilai vonis, dengan batas teoretis 300 perbandingan.
- Operasi yang paling sering dipakai (tampil, get, add) bernilai O(1) atau O(n).
- Pencarian linear masih memadai untuk 50 entri arsip.
- Kelemahan array dinamis adalah hapus/sisip di awal yang O(n). Untuk arsip putusan, penghapusan jauh lebih jarang daripada pembacaan, sehingga kelemahan ini dapat diterima (lihat ADR-001).
- Insertion sort tetap sesuai untuk jumlah data yang relatif kecil dan mudah ditelusuri melalui trace maupun unit test.
- Jika jumlah data meningkat menjadi ribuan, merge sort dapat dipertimbangkan untuk menggantikan insertion sort.

## 6. Kesimpulan

Berdasarkan struktur data dan CSV yang digunakan, ArrayDinamis dengan linear search, insertion sort, serta Stack dengan `searchPreserve` masih sesuai untuk kebutuhan aplikasi.

Jumlah arsip berdasarkan kode unik adalah 50 entri, sementara pengujian pengurutan menggunakan 25 nilai vonis yang sudah tersedia.

Seluruh algoritma dipilih dengan mempertimbangkan kesederhanaan implementasi, kemudahan pengujian, stabilitas pengurutan, dan kesesuaian terhadap batasan tugas Struktur Data.
