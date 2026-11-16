package com.klinik.model;

public class RekamMedis implements DapatDicetak {
    private String idRekamMedis;
    private String idPasien;
    private String tanggalPemeriksaan;
    private String diagnosis;
    private String tindakan;
    private String catatan;

    public RekamMedis(String idRekamMedis, String idPasien, String tanggalPemeriksaan,
                      String diagnosis, String tindakan, String catatan) {
        this.idRekamMedis = idRekamMedis;
        this.idPasien = idPasien;
        this.tanggalPemeriksaan = tanggalPemeriksaan;
        this.diagnosis = diagnosis;
        this.tindakan = tindakan;
        this.catatan = catatan;
    }

    public String getIdRekamMedis() {
        return idRekamMedis;
    }

    public void setIdRekamMedis(String idRekamMedis) {
        this.idRekamMedis = idRekamMedis;
    }

    public String getIdPasien() {
        return idPasien;
    }

    public void setIdPasien(String idPasien) {
        this.idPasien = idPasien;
    }

    public String getTanggalPemeriksaan() {
        return tanggalPemeriksaan;
    }

    public void setTanggalPemeriksaan(String tanggalPemeriksaan) {
        this.tanggalPemeriksaan = tanggalPemeriksaan;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTindakan() {
        return tindakan;
    }

    public void setTindakan(String tindakan) {
        this.tindakan = tindakan;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    @Override
    public String formatCetak() {
        return "=== CATATAN REKAM MEDIS ===\n"
             + "ID RM     : " + idRekamMedis + "\n"
             + "ID Pasien : " + idPasien + "\n"
             + "Tanggal   : " + tanggalPemeriksaan + "\n"
             + "Diagnosis : " + diagnosis + "\n"
             + "Tindakan  : " + tindakan + "\n"
             + "Catatan   : " + catatan + "\n";
    }
}
