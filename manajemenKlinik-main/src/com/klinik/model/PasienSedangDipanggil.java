package com.klinik.model;

public final class PasienSedangDipanggil {
    private static final PasienSedangDipanggil INSTANCE = new PasienSedangDipanggil();

    private volatile String idPasien;
    private volatile String nama;
    private volatile String noAntrean;

    private PasienSedangDipanggil() {
    }

    public static PasienSedangDipanggil getInstance() {
        return INSTANCE;
    }

    public void set(Pasien pasien, String noAntrean) {
        if (pasien == null) {
            clear();
            return;
        }
        this.idPasien = pasien.getId();
        this.nama = pasien.getNama();
        this.noAntrean = noAntrean;
    }

    public void clear() {
        idPasien = null;
        nama = null;
        noAntrean = null;
    }

    public String getIdPasien() {
        return idPasien;
    }

    public String getNama() {
        return nama;
    }

    public String getNoAntrean() {
        return noAntrean;
    }

    public boolean isAda() {
        return idPasien != null && !idPasien.trim().isEmpty();
    }
}
