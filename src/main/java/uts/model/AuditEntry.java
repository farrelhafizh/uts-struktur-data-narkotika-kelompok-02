package uts.model;

import java.time.LocalDateTime;

/** Satu catatan riwayat operasi pengguna. */
public class AuditEntry {
    private final LocalDateTime waktu = LocalDateTime.now();
    private final String aksi;
    private final String detail;

    public AuditEntry(String aksi, String detail) {
        this.aksi = aksi;
        this.detail = detail;
    }

    public String getAksi() { return aksi; }
    public String getDetail() { return detail; }

    @Override
    public String toString() {
        return waktu.withNano(0) + " | " + aksi + " | " + detail;
    }
}
