package com.klinik.model;

public class Pasien extends Orang {
    private String nik;
    private String tanggalLahir;
    private String jenisKelamin;
    private String alamat;

    public Pasien(String id, String nama, String nik, String tanggalLahir, String jenisKelamin, String alamat) {
        super(id, nama);
        this.nik = nik;
        this.tanggalLahir = tanggalLahir;
        this.jenisKelamin = jenisKelamin;
        this.alamat = alamat;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(String tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    @Override
    public String getInfoRingkas() {
        return String.format("[%s] %s - NIK: %s (%s)", getId(), getNama(), nik, jenisKelamin);
    }

    @Override
    public String toString() {
        return getNama() + " (" + nik + ")";
    }
}
