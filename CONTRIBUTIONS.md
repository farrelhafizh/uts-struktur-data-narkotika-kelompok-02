
# Kontribusi Anggota – Kelompok 02

Dokumen ini mencatat kontribusi anggota dalam pengembangan project UTS Struktur Data mengenai Putusan Perkara Narkotika.

Daftar kontribusi disusun berdasarkan riwayat Issue dan Pull Request pada repository GitHub. Daftar PR mencakup perubahan yang telah di-merge ke branch `main`.

Repository: [uts-struktur-data-narkotika-kelompok-02](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02)

## Ringkasan

| Kontribusi | Muhammad Aqilah | Mohammad Farrel |
|---|---:|---:|
| Pull Request dibuat dan di-merge | 15 | 13 |
| Pull Request Approved | 15 | 13 |
| Issue dibuat | 2 | 2 |
| Ekstraksi data (PR) | PF026–PF050 | PF001–PF025 |
| Dokumentasi | Struktur data dan README | Aturan ekstraksi dan ADR |
| Implementasi kode | Model, agregasi, dan pengujian | Fondasi project dan dokumentasi teknis |

**Status Issue:** 2 Closed dan 2 Open.

---

## Muhammad Aqilah Sardi Syaputra (202510370110004) – @aqilahputra

### Ekstraksi Data

- Kontribusi dataset PF026–PF050 berdasarkan PR #6 (PN Kisaran 2024).

### Issue

| Issue | Judul | Status |
|---|---|---|
| [#1](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/issues/1) | Ekstraksi data PF001–PF025 | Closed |
| [#3](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/issues/3) | Dokumentasi aturan data di README | Open |

### Pull Request yang dibuat

| PR | Isi |
|---|---|
| [#6](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/6) | Data PF026–PF050 (PN Kisaran 2024) |
| [#8](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/8) | feat: add Aggregate class for case statistics |
| [#9](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/9) | feat: add AuditEntry class for operation history |
| [#10](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/10) | feature/aggregate-class |
| [#11](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/11) | feature/aggregate-class |
| [#13](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/13) | feature/aggregate-class |
| [#15](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/15) | feature/aggregate-class |
| [#16](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/16) | feature/aggregate-class |
| [#17](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/17) | feature/aggregate-class |
| [#23](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/23) | docs: document data structures and algorithm decisions |
| [#24](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/24) | feature/aggregate-class |
| [#25](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/25) | Delete docs/trace/analisis-kompleksitas.md |
| [#26](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/26) | Delete docs/trace/skenario-uji.md |
| [#27](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/27) | feature/aggregate-class |
| [#32](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/32) | docs(readme): cara build/test/run, struktur proyek, fitur, dan tautan dokumentasi |

### Implementasi Kode

| File | Kontribusi |
|---|---|
| `Aggregate.java` | Menyimpan hasil agregasi perkara, rata-rata vonis, total denda, dan distribusi narkotika. |
| `AuditEntry.java` | Mencatat riwayat aktivitas pengguna beserta waktu dan detail operasi. |
| `Counter.java` | Menghitung frekuensi kemunculan data berdasarkan pasangan kunci dan jumlah. |
| `Putusan.java` | Menyimpan data perkara, melakukan validasi, parsing CSV, dan menyediakan konversi data. |

### Unit Testing

| File | Kontribusi |
|---|---|
| `ArrayStackTest.java` | Menguji operasi LIFO, resize otomatis, pencarian, penghapusan, dan clear. |
| `PutusanServiceTest.java` | Menguji pencarian, validasi, tambah-hapus, filter, agregasi, pengurutan, audit, dan impor CSV. |
| `SinglyLinkedListTest.java` | Menguji penambahan, penyisipan, penghapusan, pencarian, filter, dan validitas linked list. |

---

## Mohammad Farrel Hafizh Al Bintang (202510370110012) – @farrelhafizh

### Ekstraksi Data

- Kontribusi dataset PF001–PF025 berdasarkan PR #5 (PN Kisaran 2022).

### Issue

| Issue | Judul | Status |
|---|---|---|
| [#2](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/issues/2) | Ekstraksi data PF026–PF050 | Closed |
| [#4](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/issues/4) | Setup Maven dan kelas Putusan | Open |

### Pull Request yang dibuat

| PR | Isi |
|---|---|
| [#5](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/5) | Data PF001–PF025 (PN Kisaran 2022) |
| [#7](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/7) | docs: tambah aturan ekstraksi data di README |
| [#12](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/12) | Delete src/main/java/uts/app directory |
| [#14](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/14) | Delete src/main/java/uts/ds directory |
| [#18](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/18) | docs: add ADR-002 for search and sorting algorithm decisions |
| [#19](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/19) | docs: aturan data |
| [#20](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/20) | Delete docs/adr/ADR-001-pemilihan-struktur-data.md |
| [#21](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/21) | Delete docs/adr/ADR-002-pemilihan-struktur-data.md |
| [#22](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/22) | Delete docs/adr/ADR-001-pemilihan-struktur.md |
| [#28](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/28) | strukturdata |
| [#29](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/29) | Delete src/strukturdata |
| [#30](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/30) | feat: implement aggregate class and data processing |
| [#31](https://github.com/farrelhafizh/uts-struktur-data-narkotika-kelompok-02/pull/31) | feat: implement aggregate class and data processing |

### Dokumentasi dan Pengelolaan Project

- Menyiapkan fondasi project menggunakan Maven.
- Mengembangkan dokumentasi aturan ekstraksi dan pengelolaan data.
- Mengelola dokumentasi Architecture Decision Record (ADR).
- Berkontribusi dalam penataan struktur direktori project.
- Mengembangkan dan merapikan dokumentasi teknis project.

---

## Review dan Approval

Seluruh PR #5–#32 pada daftar tertutup GitHub ditampilkan dengan status Approved.

Identitas reviewer dan pembagian jumlah approval per anggota perlu dicatat berdasarkan detail aktivitas review masing-masing PR.

---

## Catatan Riwayat

- Branch `main` menggunakan proteksi sehingga perubahan harus diajukan melalui Pull Request.
- Terdapat 28 Pull Request yang telah di-merge, yaitu PR #5 sampai #32.
- Sebanyak 15 PR dibuat oleh @aqilahputra dan 13 PR dibuat oleh @farrelhafizh.
- Seluruh 28 PR yang ditampilkan berstatus Approved.
- Terdapat 4 Issue, dengan 2 Issue Closed (#1 dan #2) serta 2 Issue Open (#3 dan #4).
- Beberapa Pull Request menggunakan branch `feature/aggregate-class` untuk mengajukan perubahan kode.
- Daftar kontribusi implementasi kode dan pengujian didasarkan pada pekerjaan yang telah dilakukan dan riwayat repository.
- Jumlah kontribusi dapat diperbarui mengikuti perkembangan repository.
