package com.klinik.model;

public class Antrean {

    public enum Status {
        MENUNGGU("Menunggu"),
        DALAM_PEMERIKSAAN("Dalam Pemeriksaan"),
        SELESAI("Selesai");

        private final String label;
        Status(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    private final String nomor;
    private final Pasien pasien;
    private Status status;

    public Antrean(String nomor, Pasien pasien) {
        this.nomor = nomor;
        this.pasien = pasien;
        this.status = Status.MENUNGGU;
    }

    public String getNomor() { return nomor; }
    public Pasien getPasien() { return pasien; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
