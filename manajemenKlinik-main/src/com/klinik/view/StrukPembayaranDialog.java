package com.klinik.view;

import com.klinik.util.UIStyle;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class StrukPembayaranDialog extends JDialog {
    public StrukPembayaranDialog(Window owner, int total, int dibayar, String metode) {
        super(owner, "Pratinjau Struk Pembayaran", ModalityType.APPLICATION_MODAL);
        setSize(380, 500);
        setResizable(false);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(18, 24, 18, 24));
        setContentPane(card);

        JPanel header = new JPanel(new GridLayout(3, 1, 0, 2));
        header.setBackground(Color.WHITE);
        header.setBorder(new MatteBorder(0, 0, 1, 0, UIStyle.TEXT_PRIMARY));
        JLabel title = new JLabel("KLINIK SEHAT BERSAMA", SwingConstants.CENTER);
        title.setFont(UIStyle.FONT_SUBTITLE);
        title.setForeground(UIStyle.PRIMARY_COLOR);
        JLabel address = new JLabel("Jl. Sehat No. 1, Surakarta", SwingConstants.CENTER);
        address.setFont(UIStyle.FONT_SMALL);
        address.setForeground(UIStyle.TEXT_PRIMARY);
        JLabel divider = new JLabel("Struk Pembayaran", SwingConstants.CENTER);
        divider.setFont(UIStyle.FONT_SECTION);
        divider.setForeground(UIStyle.TEXT_PRIMARY);
        header.add(title);
        header.add(address);
        header.add(divider);
        card.add(header, BorderLayout.NORTH);

        JPanel detail = new JPanel(new GridLayout(8, 2, 8, 8));
        detail.setBackground(Color.WHITE);
        detail.setBorder(new EmptyBorder(12, 6, 12, 6));
        detail.add(createKeyLabel("No. Transaksi"));
        detail.add(createValueLabel("TRX-20261123-007"));
        detail.add(createKeyLabel("Pasien"));
        detail.add(createValueLabel("Siti Rahma (RM-000123)"));
        detail.add(createKeyLabel("Tanggal"));
        detail.add(createValueLabel(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd MMM yyyy, HH.mm", new Locale("id", "ID")))));
        detail.add(createKeyLabel("Metode"));
        detail.add(createValueLabel(metode));
        detail.add(createKeyLabel("Total Biaya"));
        detail.add(createValueLabel(PemrosesanResepPanel.formatRupiah(total)));
        detail.add(createKeyLabel("Jumlah Dibayar"));
        detail.add(createValueLabel(PemrosesanResepPanel.formatRupiah(dibayar)));
        detail.add(createKeyLabel("Kembalian"));
        detail.add(createValueLabel(PemrosesanResepPanel.formatRupiah(
                Math.max(0, dibayar - total))));
        detail.add(createKeyLabel("Status"));
        detail.add(createValueLabel("Lunas"));
        card.add(detail, BorderLayout.CENTER);

        JLabel thanks = new JLabel("Terima Kasih", SwingConstants.CENTER);
        thanks.setFont(UIStyle.FONT_BODY);
        thanks.setForeground(UIStyle.TEXT_PRIMARY);
        thanks.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, UIStyle.TEXT_PRIMARY),
                new EmptyBorder(10, 0, 8, 0)));
        card.add(thanks, BorderLayout.SOUTH);
    }

    private JLabel createKeyLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIStyle.FONT_BODY);
        label.setForeground(UIStyle.TEXT_PRIMARY);
        return label;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(": " + text);
        label.setFont(UIStyle.FONT_BODY_BOLD);
        label.setForeground(UIStyle.TEXT_PRIMARY);
        return label;
    }
}