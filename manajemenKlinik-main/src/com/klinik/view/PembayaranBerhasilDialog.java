package com.klinik.view;

import com.klinik.model.Pasien;
import com.klinik.model.Transaksi;
import com.klinik.util.UIStyle;
import java.awt.*;
import java.awt.geom.Path2D;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;

public class PembayaranBerhasilDialog extends JDialog {
    public PembayaranBerhasilDialog(
            Window owner,
            Transaksi transaksi,
            Pasien pasien,
            Runnable onFinished) {
        super(owner, "Pembayaran Berhasil", ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setModal(true);
        setBackground(Color.WHITE);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        getAccessibleContext().setAccessibleName("Pembayaran berhasil");
        getAccessibleContext().setAccessibleDescription("Ringkasan pembayaran berhasil.");

        JPanel card = new ShadowCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(28, 28, 28, 28));

        JButton close = new JButton("\u00D7");
        close.setFont(new Font(UIStyle.FONT_FAMILY, Font.PLAIN, 20));
        close.setForeground(new Color(0x0B, 0x4A, 0x9E));
        close.setBackground(Color.WHITE);
        close.setToolTipText("Tutup");
        close.getAccessibleContext().setAccessibleName("Tutup dialog");
        close.setFocusPainted(false);
        close.setBorderPainted(false);
        close.setContentAreaFilled(false);
        close.addActionListener(e -> dispose());
        JPanel closeRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closeRow.setOpaque(false);
        closeRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        closeRow.add(close);
        card.add(closeRow);

        JPanel successIcon = new JPanel(new GridBagLayout());
        successIcon.setOpaque(false);
        successIcon.setPreferredSize(new Dimension(424, 56));
        successIcon.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        successIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        JComponent iconCircle = new CheckIcon();
        successIcon.add(iconCircle);
        card.add(successIcon);
        card.add(Box.createVerticalStrut(10));

        JLabel title = new JLabel("Pembayaran Berhasil", SwingConstants.CENTER);
        title.setFont(new Font(UIStyle.FONT_FAMILY, Font.BOLD, 22));
        title.setForeground(UIStyle.TEXT_PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        card.add(title);
        card.add(Box.createVerticalStrut(10));

        JLabel help = new JLabel(
                "<html><div style='text-align:center; width:380px'>"
                        + "Resep telah ditandai selesai dan data tersimpan.</div></html>",
                SwingConstants.CENTER);
        help.setFont(new Font(UIStyle.FONT_FAMILY, Font.PLAIN, 14));
        help.setForeground(new Color(0x47, 0x55, 0x69));
        help.setAlignmentX(Component.CENTER_ALIGNMENT);
        help.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        card.add(help);
        card.add(Box.createVerticalStrut(16));

        JPanel summary = new RoundedPanel(Color.WHITE, new Color(0xDD, 0xE3, 0xEE), 4);
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setBorder(new EmptyBorder(13, 15, 13, 15));
        summary.setAlignmentX(Component.CENTER_ALIGNMENT);
        summary.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        summary.add(createSummaryRow(
                "No. Transaksi", transaksi.getNoTransaksi(), true));
        summary.add(Box.createVerticalStrut(6));
        summary.add(createSummaryRow(
                "Pasien", pasien.getNama() + " (" + pasien.getId() + ")", true));
        summary.add(Box.createVerticalStrut(6));
        String tanggal = transaksi.getTanggalWaktu().format(
                DateTimeFormatter.ofPattern("dd MMM yyyy, HH.mm", new Locale("id", "ID")));
        summary.add(createSummaryRow("Tanggal", tanggal, true));
        summary.add(Box.createVerticalStrut(6));
        summary.add(createSummaryRow("Metode", transaksi.getMetode(), true));
        summary.add(Box.createVerticalStrut(10));
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(0xDD, 0xE3, 0xEE));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        summary.add(separator);
        summary.add(Box.createVerticalStrut(6));
        summary.add(createSummaryRow(
                "Total Biaya", PemrosesanResepPanel.formatRupiah(transaksi.getTotal()), true));
        summary.add(Box.createVerticalStrut(6));
        summary.add(createSummaryRow(
                "Jumlah Dibayar",
                PemrosesanResepPanel.formatRupiah(transaksi.getJumlahDibayar()),
                false));
        summary.add(Box.createVerticalStrut(8));

        JPanel change = new RoundedPanel(new Color(0xF1, 0xF5, 0xF9), null, 4);
        change.setLayout(new BorderLayout(8, 0));
        change.setBorder(new EmptyBorder(10, 14, 10, 14));
        change.setAlignmentX(Component.CENTER_ALIGNMENT);
        change.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        change.add(createSummaryKey("Kembalian"), BorderLayout.WEST);
        JLabel changeAmount = new JLabel(
                PemrosesanResepPanel.formatRupiah(transaksi.getKembalian()));
        changeAmount.setFont(new Font(UIStyle.FONT_FAMILY, Font.BOLD, 18));
        changeAmount.setForeground(UIStyle.TEXT_PRIMARY);
        change.add(changeAmount, BorderLayout.EAST);
        summary.add(change);
        summary.add(Box.createVerticalStrut(6));

        JPanel statusRow = new JPanel(new BorderLayout(8, 0));
        statusRow.setOpaque(false);
        statusRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        statusRow.add(createSummaryKey("Status"), BorderLayout.WEST);
        JLabel paidBadge = new JLabel("Lunas");
        paidBadge.setFont(new Font(UIStyle.FONT_FAMILY, Font.BOLD, 13));
        paidBadge.setForeground(new Color(0x14, 0x53, 0x2D));
        paidBadge.setOpaque(true);
        paidBadge.setBackground(new Color(0xDC, 0xFC, 0xE7));
        paidBadge.setBorder(new javax.swing.border.CompoundBorder(
                new LineBorder(new Color(0xDC, 0xFC, 0xE7), 1, true),
                new EmptyBorder(4, 10, 4, 10)));
        statusRow.add(paidBadge, BorderLayout.EAST);
        summary.add(statusRow);
        card.add(summary);
        card.add(Box.createVerticalStrut(16));

        JButton finish = createButton("Selesai, Kembali ke Antrean");
        finish.setAlignmentX(Component.CENTER_ALIGNMENT);
        finish.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        finish.addActionListener(e -> {
            dispose();
            onFinished.run();
        });
        card.add(finish);

        getRootPane().setDefaultButton(finish);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "ignoreEscape");
        getRootPane().getActionMap().put("ignoreEscape", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
            }
        });
        Dimension preferred = card.getPreferredSize();
        card.setPreferredSize(new Dimension(480, preferred.height));
        setContentPane(card);
        pack();
        setLocationRelativeTo(owner);
        SwingUtilities.invokeLater(finish::requestFocusInWindow);
    }

    private JPanel createSummaryRow(String key, String value, boolean boldValue) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.setPreferredSize(new Dimension(420, 28));
        row.setMinimumSize(new Dimension(0, 28));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        row.add(createSummaryKey(key), BorderLayout.WEST);
        JLabel valueLabel = new JLabel(value, SwingConstants.RIGHT);
        valueLabel.setFont(new Font(
                UIStyle.FONT_FAMILY, boldValue ? Font.BOLD : Font.PLAIN, 14));
        valueLabel.setForeground(UIStyle.TEXT_PRIMARY);
        row.add(valueLabel, BorderLayout.EAST);
        return row;
    }

    private JLabel createSummaryKey(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(UIStyle.FONT_FAMILY, Font.PLAIN, 14));
        label.setForeground(new Color(0x47, 0x55, 0x69));
        return label;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setFont(new Font(UIStyle.FONT_FAMILY, Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0x0B, 0x5C, 0xBF));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(0, 12, 0, 12));
        button.setPreferredSize(new Dimension(424, 44));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setMinimumSize(new Dimension(120, 44));
        button.getModel().addChangeListener(event -> {
            ButtonModel model = button.getModel();
            if (model.isPressed()) {
                button.setBackground(new Color(0x08, 0x3F, 0x85));
            } else if (model.isRollover()) {
                button.setBackground(new Color(0x0A, 0x4F, 0xA5));
            } else {
                button.setBackground(new Color(0x0B, 0x5C, 0xBF));
            }
        });
        return button;
    }

    private static class CheckIcon extends JComponent {
        private CheckIcon() {
            setPreferredSize(new Dimension(56, 56));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0xDC, 0xFC, 0xE7));
            g2.fillOval(0, 0, 55, 55);
            g2.setColor(new Color(0x14, 0x53, 0x2D));
            g2.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Path2D check = new Path2D.Float();
            check.moveTo(16, 29);
            check.lineTo(24, 37);
            check.lineTo(41, 19);
            g2.draw(check);
            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final Color fill;
        private final Color outline;
        private final int radius;

        private RoundedPanel(Color fill, Color outline, int radius) {
            this.fill = fill;
            this.outline = outline;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            if (outline != null) {
                g2.setColor(outline);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            }
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class ShadowCard extends JPanel {
        private ShadowCard() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 12; i > 0; i--) {
                g2.setColor(new Color(15, 23, 42, Math.max(2, 24 - i)));
                g2.fillRoundRect(6, 4 + i / 2, getWidth() - 13,
                        getHeight() - 13, 12, 12);
            }
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(6, 4, getWidth() - 13, getHeight() - 13, 6, 6);
            g2.setColor(new Color(0xDD, 0xE3, 0xEE));
            g2.drawRoundRect(6, 4, getWidth() - 13, getHeight() - 13, 6, 6);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }
}
