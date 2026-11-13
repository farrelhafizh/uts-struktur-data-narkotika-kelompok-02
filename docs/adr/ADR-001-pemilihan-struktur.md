# ADR-001: Pemilihan Struktur Data Utama dan Sekunder

- **Status:** Diterima
- **Tanggal:** [2026-11-11]
- **Penulis:** [Muhammad Aqilah Sardi Syaputra]
- **Issue:** [#1]

## Konteks

Kebutuhan aplikasi yang memengaruhi pilihan struktur data:

- **Tampil, filter, agregasi, ekspor:** membaca seluruh 50 elemen secara berurutan setiap kali laporan diminta.
- **Pencarian** berdasarkan caseId atau nomor perkara, dengan pelaporan jumlah langkah.
- **Tambah, sisip, dan hapus:** dilakukan pengguna di posisi awal, tengah, dan akhir (skenario uji 3 dan 4); pada skenario 3 perubahan pointer atau pergeseran elemen harus bisa ditunjukkan.
- **Pengurutan berdasarkan vonis:** menghasilkan tampilan terurut tanpa merusak urutan dan invariant arsip asli.
- **Riwayat operasi:** setiap aksi pengguna dicatat, dan yang paling sering dilihat adalah aksi terbaru.

## Pilihan yang dipertimbangkan

| Peran | Pilihan |
|---|---|
| Struktur utama | Array dinamis, Singly linked list |
| Struktur sekunder | Stack, Queue |

### Perbandingan kompleksitas (struktur utama)

| Operasi | Array dinamis | Singly linked list (dengan `tail`) |
|---|---|---|
| get(i) / set(i) | O(1) | O(n) |
| add di akhir | O(1) amortized (O(n) saat resize) | O(1) |
| add di awal | O(n) (menggeser) | O(1) |
| insert(i) / remove(i) tengah | O(n) menggeser elemen | O(n) mencari node, O(1) mengubah pointer |
| remove elemen terakhir | O(1) | O(n) (perlu node sebelum `tail`) |
| search (linear) | O(n) | O(n) |
| Tampil semua berurutan | O(n) | O(n) |
| Memori tambahan | Slot kosong sisa kapasitas | Satu pointer `next` per node |

### Perbandingan struktur sekunder

| Operasi | Stack (array) | Queue (circular array) |
|---|---|---|
| push / enqueue | O(1) amortized | O(1) amortized |
| pop / dequeue, peek | O(1) | O(1) |
| searchPreserve / rotateSearch | O(n) | O(n) |

## Keputusan

- **Struktur utama:** Singly linked list generik (`SinglyLinkedList<T>`) dengan pointer `head` dan `tail`.
- **Struktur sekunder:** Stack berbasis array generik (`ArrayStack<T>`) untuk riwayat audit.

## Alasan

1. **Tambah di akhir dan awal O(1).** Dengan pointer `tail`, `addLast` tidak menelusuri list, dan `addFirst` cukup mengubah `head`. Arsip tidak memerlukan resize atau penyalinan array.
2. **Hapus dan sisip tanpa menggeser elemen.** Setelah node sebelumnya ditemukan, penghapusan dan penyisipan hanya mengubah satu pointer `next`. Ini cocok dengan skenario uji 3 yang meminta perubahan pointer ditunjukkan.
3. **Penelusuran berurutan sama murahnya.** Fitur tampil, filter, agregasi, dan ekspor hanya menelusuri dari `head` ke `null` (O(n)), tidak memerlukan akses per indeks.
4. **Biaya akses per indeks dapat diterima.** `get(i)` O(n) lebih lambat daripada array, tetapi dengan 50 data paling banyak 49 langkah. Pengurutan (ADR-002) disusun dengan menelusuri node langsung, bukan memanggil `get(i)` berulang.
5. **Stack sesuai pola riwayat.** Riwayat paling sering dilihat dari yang terbaru, yaitu pola LIFO. Queue (FIFO) lebih cocok untuk antrean pemeriksaan, yang bukan kebutuhan utama aplikasi ini.
6. **Struktur sekunder berbasis array.** Stack memakai `Object[]` dengan resize x2, sehingga kelompok mempraktikkan dua teknik berbeda: pointer (utama) dan array (sekunder).

## Konsekuensi

### Yang menguntungkan

- Penambahan di awal/akhir dan penghapusan di awal O(1); tidak ada pergeseran elemen maupun kapasitas yang perlu dikelola.
- Ukuran arsip tumbuh sesuai kebutuhan.

### Yang harus diwaspadai dan cara menanganinya

- **Kesalahan pointer** (`head`/`tail` tidak terbarui, node terputus) adalah sumber bug utama linked list. Penanganan: `validateInvariant()` dipanggil di test dan di `Main` setelah setiap operasi menu.
- **Invariant `SinglyLinkedList`:**
    - `size >= 0`; list kosong <=> `head == null`, `tail == null`, `size == 0`.
    - Jumlah node yang ditelusuri dari `head` sama dengan `size` (tidak ada siklus).
    - `tail` menunjuk node terakhir dan `tail.next == null`.
- **Skenario uji 3 (hapus pertama, tengah, terakhir)** ditunjukkan lewat perubahan pointer:
    - Pertama: `head = head.next`; bila list menjadi kosong, `tail = null`.
    - Tengah: `prev.next = removed.next`.
    - Terakhir: `prev.next = null` dan `tail = prev` (perlu menelusuri sampai node sebelum `tail`, O(n)).
- **Batas indeks:** `get`, `insert`, dan `remove` di luar rentang melempar `IndexOutOfBoundsException` tanpa mengubah isi struktur.
- **Pengurutan berdasarkan vonis** dilakukan ke list baru (insertion sort stabil), sehingga urutan dan invariant arsip asli tidak berubah.
- **Pencarian** linear menyimpan jumlah langkah terakhir (`getLastSearchSteps()`) untuk dilaporkan ke pengguna.

## Tambahan: `ArrayStack` dan kontrak `StrukturData`

`ArrayStack<T>` tetap mengimplementasikan `StrukturData<T>` agar seragam dengan struktur utama, tetapi beberapa operasi kontrak dipetakan secara khusus supaya sifat LIFO tidak rusak.

| Operasi `StrukturData<T>` | Padanan di `ArrayStack<T>` | Kompleksitas | Keterangan |
|---|---|---|---|
| `add(T value)` | `push(value)` | O(1) amortized | Selalu di puncak; resize x2 saat penuh (kapasitas awal 8) |
| `remove(int position)` | `pop()` | O(1) | **Penyimpangan:** hanya `position == 0`; selain itu `UnsupportedOperationException`, agar riwayat di tengah tidak bisa dihapus |
| `get(int position)` | `peek()` / `get(depth)` | O(1) | Posisi dihitung dari puncak (0 = terbaru); hanya baca |
| `contains(T value)` | `searchPreserve(...) >= 0` | O(n) waktu, O(n) ruang | Pop ke stack bantu lalu `restore`; urutan akhir tetap |
| `size()`, `isEmpty()` | sama | O(1) | |
| `clear()` | `clear()` | O(n) | Tersedia di struktur, tetapi tidak dipakai oleh menu aplikasi |

**Mengapa `get(depth)` tidak melanggar sifat Stack:** method ini membaca `data[size - 1 - depth]` tanpa mengubah `size`. Method ini dipakai menu Audit untuk menampilkan riwayat dari yang terbaru. Semua perubahan isi tetap lewat `push` dan `pop` di puncak.

**Invariant `ArrayStack`:** `0 <= size <= data.length`, dan semua slot di atas puncak (`size .. data.length - 1`) bernilai `null`. `pop`/`peek` pada stack kosong melempar `IllegalStateException`.

**Pengujian:** `SinglyLinkedListTest`, `ArrayStackTest`, dan `PutusanServiceTest`. [ISI jumlah test sesuai hasil `mvn test`.]