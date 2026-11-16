package com.klinik.model;

public class Dokter extends Orang {
    private String spesialisasi;

    public Dokter(String idDokter, String nama, String spesialisasi) {
        super(idDokter, nama);
        this.spesialisasi = spesialisasi;
    }

    public String getIdDokter() {
        return getId();
    }

    public void setIdDokter(String idDokter) {
        setId(idDokter);
    }

    public String getSpesialisasi() {
        return spesialisasi;
    }

    public void setSpesialisasi(String spesialisasi) {
        this.spesialisasi = spesialisasi;
    }

    @Override
    public String getInfoRingkas() {
        return String.format("[%s] dr. %s - Spesialis %s", getId(), getNama(), spesialisasi);
    }
}
