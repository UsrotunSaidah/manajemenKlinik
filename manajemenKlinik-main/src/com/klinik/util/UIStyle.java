package com.klinik.util;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class UIStyle {
    public static final Color PRIMARY_COLOR = new Color(0x15, 0x65, 0xC0);       // #1565C0
    public static final Color PRIMARY_HOVER = new Color(0x0D, 0x47, 0xA1);       // #0D47A1
    public static final Color SECONDARY_COLOR = new Color(0xE3, 0xF2, 0xFD);     // #E3F2FD
    public static final Color BACKGROUND_COLOR = new Color(0xF5, 0xF7, 0xFA);    // #F5F7FA
    public static final Color CARD_COLOR = Color.WHITE;
    public static final Color TEXT_PRIMARY = new Color(0x1E, 0x29, 0x3B);        // #1E293B
    public static final Color TEXT_SECONDARY = new Color(0x64, 0x74, 0x8B);      // #64748B
    public static final Color BORDER_COLOR = new Color(0xCB, 0xD5, 0xE1);        // #CBD5E1
    public static final Color DANGER_COLOR = new Color(0xDC, 0x26, 0x26);        // #DC2626
    public static final Color DANGER_HOVER = new Color(0xB9, 0x1C, 0x1C);        // #B91C1C
    public static final Color SUCCESS_COLOR = new Color(0x16, 0xA3, 0x4A);       // #16A34A

    public static final String FONT_FAMILY = "Segoe UI";
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font(FONT_FAMILY, Font.BOLD, 16);
    public static final Font FONT_SECTION = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_STAT_NUMBER = new Font(FONT_FAMILY, Font.BOLD, 28);

    public static final Border CARD_BORDER = new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(16, 20, 16, 20)
    );

    public static final Border INPUT_BORDER = new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(7, 10, 7, 10)
    );

    public static JButton createPrimaryButton(String text) {
        return createRoundedButton(text, PRIMARY_COLOR, PRIMARY_HOVER, Color.WHITE);
    }

    public static JButton createSecondaryButton(String text) {
        return createRoundedButton(text, SECONDARY_COLOR, new Color(0xBB, 0xDE, 0xFB), PRIMARY_COLOR);
    }

    public static JButton createDangerButton(String text) {
        return createRoundedButton(text, DANGER_COLOR, DANGER_HOVER, Color.WHITE);
    }

    private static JButton createRoundedButton(String text, Color bg, Color hoverBg, Color fg) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(FONT_BODY_BOLD);
        button.setForeground(fg);
        button.setBackground(bg);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(9, 18, 9, 18));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) button.setBackground(hoverBg);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) button.setBackground(bg);
            }
        });
        return button;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_COLOR);
        panel.setBorder(CARD_BORDER);
        return panel;
    }

    public static void styleTextField(JTextField field) {
        field.setFont(FONT_BODY);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(Color.WHITE);
        field.setBorder(INPUT_BORDER);
    }

    public static void styleTextArea(JTextArea area) {
        area.setFont(FONT_BODY);
        area.setForeground(TEXT_PRIMARY);
        area.setBackground(Color.WHITE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(6, 8, 6, 8));
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_PRIMARY);
        table.setRowHeight(32);
        table.setGridColor(BORDER_COLOR);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(SECONDARY_COLOR);
        table.setSelectionForeground(PRIMARY_COLOR);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BODY_BOLD);
        header.setBackground(SECONDARY_COLOR);
        header.setForeground(PRIMARY_COLOR);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        header.setBorder(new LineBorder(BORDER_COLOR, 1));

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBorder(new EmptyBorder(0, 10, 0, 10));
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
    }
}
