package com.klinik.controller;

import com.klinik.exception.AntreanKosongException;
import com.klinik.model.Antrean;
import com.klinik.model.Pasien;
import com.klinik.model.PasienSedangDipanggil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AntreanController {

    private final Queue<Antrean> menunggu = new LinkedList<>();   // Collections: Queue + Generics
    private final List<Antrean> aktif = new ArrayList<>();        // yang tampil di tabel
    private final List<Runnable> listeners = new ArrayList<>();   // supaya View otomatis refresh
    private Antrean sedangDiperiksa;
    private Antrean antreanTerakhirSelesai;
    private int counter = 0;

    /** Daftarkan View yang ingin diberi tahu saat data antrean berubah. */
    public void tambahListener(Runnable r) { listeners.add(r); }

    private void beritahu() { listeners.forEach(Runnable::run); }

    public Antrean tambahKeAntrean(Pasien pasien) {
        counter++;
        Antrean a = new Antrean(String.format("A-%03d", counter), pasien);
        menunggu.add(a);
        aktif.add(a);
        beritahu();
        return a;
    }

    /** Selesaikan pasien yang sedang diperiksa, lalu panggil pasien berikutnya. */
    public Antrean panggilBerikutnya() throws AntreanKosongException {
        if (menunggu.isEmpty()) throw new AntreanKosongException();

        if (sedangDiperiksa != null) {
            sedangDiperiksa.setStatus(Antrean.Status.SELESAI);
            antreanTerakhirSelesai = sedangDiperiksa;
            aktif.remove(sedangDiperiksa);
        }
        sedangDiperiksa = menunggu.poll();
        sedangDiperiksa.setStatus(Antrean.Status.DALAM_PEMERIKSAAN);
        PasienSedangDipanggil.getInstance().set(sedangDiperiksa.getPasien(), sedangDiperiksa.getNomor());
        beritahu();
        return sedangDiperiksa;
    }

    /** Tandai pasien yang sedang diperiksa sebagai selesai. @return false jika tidak ada yang sedang diperiksa. */
    public boolean selesaikanPemeriksaan() {
        if (sedangDiperiksa == null) return false;
        sedangDiperiksa.setStatus(Antrean.Status.SELESAI);
        antreanTerakhirSelesai = sedangDiperiksa;
        aktif.remove(sedangDiperiksa);
        sedangDiperiksa = null;
        PasienSedangDipanggil.getInstance().clear();
        beritahu();
        return true;
    }

    public String getNomorSaatIni() {
        return sedangDiperiksa == null ? "-" : sedangDiperiksa.getNomor();
    }

    public Antrean getSedangDiperiksa() { return sedangDiperiksa; }

    public Antrean getAntreanTerakhirSelesai() { return antreanTerakhirSelesai; }

    public Pasien getPasienTerakhirSelesai() {
        return antreanTerakhirSelesai == null ? null : antreanTerakhirSelesai.getPasien();
    }

    public int getJumlahMenunggu() { return menunggu.size(); }

    /** Total nomor antrean yang sudah dikeluarkan (untuk kartu statistik dashboard). */
    public int getTotalAntrean() { return counter; }

    public List<Antrean> getDaftarAktif() { return new ArrayList<>(aktif); }

    public void kosongkanAntrean() {
        menunggu.clear();
        aktif.clear();
        sedangDiperiksa = null;
        antreanTerakhirSelesai = null;
        counter = 0;
        PasienSedangDipanggil.getInstance().clear();
        beritahu();
    }
}
