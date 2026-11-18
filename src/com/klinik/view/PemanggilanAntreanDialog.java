package com.klinik.view;

import com.klinik.model.Antrean;
import com.klinik.util.AntreanWorker;
import com.klinik.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PemanggilanAntreanDialog extends JDialog {

    private final AntreanWorker worker;

    public PemanggilanAntreanDialog(Window parent, Antrean antrean) {
        super(parent, "Pemanggilan Antrean", ModalityType.APPLICATION_MODAL);

        JPanel isi = new JPanel(new GridBagLayout());
        isi.setBackground(UIStyle.CARD_COLOR);
        isi.setBorder(new EmptyBorder(26, 34, 26, 34));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblJudul = new JLabel("Nomor Antrean Berikutnya", SwingConstants.CENTER);
        lblJudul.setFont(UIStyle.FONT_SUBTITLE);
        lblJudul.setForeground(UIStyle.PRIMARY_COLOR);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 14, 0);
        isi.add(lblJudul, gbc);

        JLabel lblNomor = new JLabel(antrean.getNomor(), SwingConstants.CENTER);
        lblNomor.setFont(UIStyle.FONT_STAT_NUMBER.deriveFont(Font.BOLD, 52f));
        lblNomor.setForeground(UIStyle.PRIMARY_COLOR);
        lblNomor.setOpaque(true);
        lblNomor.setBackground(UIStyle.SECONDARY_COLOR);
        lblNomor.setBorder(new EmptyBorder(20, 40, 20, 40));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 12, 0);
        isi.add(lblNomor, gbc);

        JLabel lblNama = new JLabel(antrean.getPasien().getNama(), SwingConstants.CENTER);
        lblNama.setFont(UIStyle.FONT_BODY_BOLD);
        lblNama.setForeground(UIStyle.TEXT_PRIMARY);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        isi.add(lblNama, gbc);

        JButton btnOk = UIStyle.createPrimaryButton("OK");
        btnOk.addActionListener(e -> tutup());
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        isi.add(btnOk, gbc);

        setContentPane(isi);
        getRootPane().setDefaultButton(btnOk);
        setMinimumSize(new Dimension(380, 0));
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);

        // jendela ditutup lewat tombol X juga harus menghentikan thread
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { tutup(); }
        });

        worker = new AntreanWorker(lblNomor, 3);
        worker.execute();
    }

    private void tutup() {
        worker.cancel(true);
        dispose();
    }
}