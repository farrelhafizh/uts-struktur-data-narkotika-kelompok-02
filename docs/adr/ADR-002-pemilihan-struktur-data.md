# ADR-002: Pemilihan Algoritma Pencarian dan Pengurutan

- **Status:** Diterima
- **Tanggal:** 13 November 2026
- **Pengambil keputusan:** Mohammad Farrel Hafizh Al Bintang, Muhammad Aqilah Sardi Syaputra
- **Terkait:** ADR-001 (Array Dinamis + Stack), `docs/analisis-kompleksitas.md`

## Konteks

Struktur utama adalah `ArrayDinamis<Putusan>` yang menyimpan data putusan narkotika. Setiap putusan memiliki kode unik (`caseId`) dengan format `PFxxx`, misalnya `PF001`, `PF002`, hingga `PF050`.

Berdasarkan file CSV yang digunakan (`farrel putra strukdat - data(2).csv`), terdapat 50 baris data dengan 25 baris terisi lengkap dan 25 baris lainnya belum memiliki data lengkap.

Aplikasi membutuhkan:

1. Pencarian putusan berdasarkan `caseId` dan `nomorPerkara` (skenario 2).
2. Pengurutan berdasarkan lama vonis (`vonisBulan`), dengan kode khusus 999 (seumur hidup) tetap dapat diikutsertakan dalam pengurutan.
3. Pencarian pada riwayat operasi (Stack) tanpa mengubah urutannya (skenario 6).

Batasan dari panduan: tidak boleh memakai koleksi `java.util` maupun `Collections.sort`/`Arrays.sort` untuk struktur inti. Oleh karena itu, algoritma pencarian dan pengurutan harus diimplementasikan sendiri.

## Keputusan 1: Pencarian di ArrayDinamis memakai Linear Search

### Opsi yang dipertimbangkan

| Opsi | Kompleksitas | Kelebihan | Kekurangan |
|---|---|---|---|
| **Linear Search** | O(n) | Sederhana; berlaku untuk berbagai kunci seperti `caseId`, `nomorPerkara`, dan kriteria bebas melalui `Predicate` | Membutuhkan hingga 50 perbandingan pada kapasitas data saat ini |
| Binary Search | O(log n) | Lebih cepat untuk data yang sudah terurut | Arsip harus selalu terurut berdasarkan kunci pencarian; penambahan data memerlukan penyisipan pada posisi terurut |
| Hash Table buatan sendiri | O(1) rata-rata | Pencarian sangat cepat | Memerlukan struktur tambahan, penanganan collision, dan resize |

### Alasan

- Untuk 50 baris data, linear search membutuhkan maksimal 50 perbandingan pada kasus terburuk.
- Satu method `cari(Predicate<T>)` dapat digunakan kembali untuk pencarian berdasarkan `caseId`, `nomorPerkara`, penghapusan, dan filter.
- Kode unik `PFxxx` memudahkan identifikasi setiap putusan dan menghindari ketergantungan pada posisi indeks.
- Urutan arsip tetap mengikuti urutan impor CSV, sesuai kebutuhan skenario 3 dan 4.
- Algoritma ini tetap dapat digunakan meskipun data belum terurut.

## Keputusan 2: Pengurutan Vonis Memakai Insertion Sort pada Salinan

### Data pembanding

Pengujian menggunakan 25 nilai `vonisBulan` yang tersedia pada CSV.

| Algoritma | Perbandingan (menurun) | Perpindahan (menurun) | Perbandingan (menaik) | Perpindahan (menaik) | Stabil? | Ruang tambahan |
|---|---:|---:|---:|---:|---|---|
| **Insertion Sort (dipakai)** | **124** | **103 pergeseran** | **160** | **138 pergeseran** | Ya | O(1) |
| Bubble Sort (berhenti dini) | 264 | 103 pertukaran | 294 | 138 pertukaran | Ya | O(1) |
| Selection Sort | 300 | 22 pertukaran | 300 | 24 pertukaran | Tidak | O(1) |
| Merge Sort | O(n log n) | - | O(n log n) | - | Ya | O(n) |

Angka insertion sort diperoleh dari simulasi terhadap 25 nilai vonis yang tersedia. Angka algoritma lain merupakan hasil perhitungan simulasi dengan data yang sama.

### Alasan

- **Stabil:** Putusan dengan nilai vonis yang sama tetap mempertahankan urutan relatif sesuai urutan arsip. Contohnya, putusan dengan vonis 48 bulan tidak saling bertukar urutan.
- **Adaptif:** Data yang sudah terurut hanya membutuhkan n - 1 perbandingan, yaitu 24 perbandingan untuk 25 data, tanpa pergeseran.
- **Lebih efisien daripada Bubble Sort:** Insertion sort membutuhkan lebih sedikit perbandingan, meskipun jumlah perpindahannya sama.
- **Lebih sederhana daripada Merge Sort:** Merge sort memang memiliki kompleksitas O(n log n), tetapi membutuhkan array tambahan O(n) dan implementasi rekursif yang lebih rumit.
- Pengurutan dilakukan pada salinan (`ArrayDinamis.salin()`), sehingga urutan arsip asli tidak berubah.

### Penanganan Kode Khusus Vonis

Nilai `999` digunakan untuk merepresentasikan vonis seumur hidup. Nilai tersebut harus tetap diikutsertakan dalam pengurutan dan tidak boleh dianggap sebagai data kosong atau diabaikan.

Pada data CSV yang digunakan untuk pengujian saat ini, belum ditemukan nilai vonis 999. Oleh karena itu, penanganan tersebut tetap disiapkan dalam implementasi untuk mengantisipasi data berikutnya.

## Keputusan 3: Pencarian di Stack Memakai searchPreserve

- Stack hanya boleh diakses melalui bagian puncak (top).
- Pencarian dilakukan dengan memindahkan elemen ke stack sementara, kemudian mengembalikannya melalui proses `restore`.
- Kompleksitas waktu adalah O(h), sedangkan ruang tambahan adalah O(h), dengan h merupakan jumlah elemen dalam Stack.
- Urutan akhir Stack dijamin tetap sama karena terjadi dua kali pembalikan.
- Alternatif membaca array internal Stack secara langsung ditolak karena melanggar prinsip LIFO yang telah dipilih pada ADR-001.
- Method `lihatDariAtas(m)` hanya digunakan untuk menampilkan riwayat secara read-only, bukan untuk melakukan pencarian.

## Konsekuensi

### Positif

- Semua algoritma dapat diimplementasikan sendiri tanpa bergantung pada algoritma pengurutan bawaan Java.
- Linear search mendukung pencarian berdasarkan kode unik `PFxxx` dan nomor perkara.
- Insertion sort mempertahankan stabilitas urutan data dengan vonis yang sama.
- Pengurutan tidak mengubah urutan arsip asli karena menggunakan salinan.
- Algoritma mudah ditelusuri (trace) dan diuji melalui unit test.

### Negatif

- Pencarian linear memiliki kompleksitas O(n).
- Insertion sort memiliki kompleksitas O(n²) pada kasus rata-rata dan terburuk.
- Pencarian pada Stack membutuhkan ruang tambahan O(h).

### Rencana Jika Data Membesar

Apabila jumlah putusan meningkat menjadi ribuan atau lebih, beberapa pengembangan dapat dipertimbangkan:

1. Mengganti insertion sort dengan merge sort yang stabil dan memiliki kompleksitas O(n log n).
2. Mempertimbangkan hash table buatan sendiri untuk mempercepat pencarian berdasarkan `caseId`.
3. Mempertimbangkan indeks tambahan untuk pencarian berdasarkan `nomorPerkara`.
4. Mempertahankan struktur arsip utama agar urutan impor tetap dapat dipertahankan.