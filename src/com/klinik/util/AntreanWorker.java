package com.klinik.util;

import javax.swing.JLabel;
import javax.swing.SwingWorker;
import java.awt.Color;
import java.awt.Toolkit;
import java.util.List;

/** Multithreading: bunyi beep + nomor berkedip di background tanpa membekukan GUI. */
public class AntreanWorker extends SwingWorker<Void, Boolean> {

    private final JLabel label;
    private final int jumlahKedip;
    private final Color warnaAsal;

    public AntreanWorker(JLabel label, int jumlahKedip) {
        this.label = label;
        this.jumlahKedip = jumlahKedip;
        this.warnaAsal = label.getForeground();
    }

    @Override
    protected Void doInBackground() throws Exception {
        for (int i = 0; i < jumlahKedip && !isCancelled(); i++) {
            Toolkit.getDefaultToolkit().beep();
            publish(true);
            Thread.sleep(500);
            publish(false);
            Thread.sleep(500);
        }
        return null;
    }

    @Override // dijalankan di EDT, aman untuk ubah komponen Swing
    protected void process(List<Boolean> chunks) {
        boolean nyala = chunks.get(chunks.size() - 1);
        label.setForeground(nyala ? new Color(0xD32F2F) : warnaAsal);
    }

    @Override
    protected void done() { label.setForeground(warnaAsal); }
}
