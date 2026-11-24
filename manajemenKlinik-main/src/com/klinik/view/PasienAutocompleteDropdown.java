package com.klinik.view;

import com.klinik.model.Pasien;
import com.klinik.model.PasienSedangDipanggil;
import com.klinik.repository.PasienRepository;
import com.klinik.util.UIStyle;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class PasienAutocompleteDropdown {
    private final JTextField field;
    private final PasienRepository repository;
    private final Consumer<Pasien> onSelect;
    private final JWindow popup;
    private final JPanel content;
    private final Timer debounceTimer;
    private final List<Pasien> visiblePatients = new ArrayList<>();
    private final List<String> visibleBadges = new ArrayList<>();
    private boolean noResultShown;
    private int selectedIndex = -1;
    private AWTEventListener outsideListener;

    public PasienAutocompleteDropdown(JTextField field, PasienRepository repository, Consumer<Pasien> onSelect) {
        this.field = field;
        this.repository = repository;
        this.onSelect = onSelect;

        Window owner = SwingUtilities.getWindowAncestor(field);
        popup = owner == null ? new JWindow() : new JWindow(owner);
        popup.setFocusableWindowState(false);

        content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 41, 59, 28));
                g2.fillRoundRect(2, 3, getWidth() - 4, getHeight() - 3, 8, 8);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 3, 8, 8);
                g2.dispose();
            }
        };
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xBB, 0xDE, 0xFB), 1, true),
                BorderFactory.createEmptyBorder(6, 8, 8, 8)));
        popup.setContentPane(content);

        debounceTimer = new Timer(250, e -> rebuildForQuery(field.getText()));
        debounceTimer.setRepeats(false);

        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { scheduleRefresh(); }
            @Override public void removeUpdate(DocumentEvent e) { scheduleRefresh(); }
            @Override public void changedUpdate(DocumentEvent e) { scheduleRefresh(); }
        });
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().trim().isEmpty()) showEmptyRecommendations();
            }

            @Override
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                    if (focusOwner != field && (focusOwner == null || !SwingUtilities.isDescendingFrom(focusOwner, popup.getContentPane()))) {
                        close();
                    }
                });
            }
        });
        field.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (field.getText().trim().isEmpty()) showEmptyRecommendations();
            }
        });
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (!popup.isVisible()) rebuildForQuery(field.getText());
                    moveSelection(1);
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    moveSelection(-1);
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (popup.isVisible() && selectedIndex >= 0) {
                        selectIndex(selectedIndex);
                        e.consume();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    close();
                    e.consume();
                }
            }
        });

        outsideListener = event -> {
            if (event instanceof MouseEvent && ((MouseEvent) event).getID() == MouseEvent.MOUSE_PRESSED) {
                Component source = (Component) ((MouseEvent) event).getSource();
                if (source != field
                        && !SwingUtilities.isDescendingFrom(source, field)
                        && !SwingUtilities.isDescendingFrom(source, popup.getContentPane())) {
                    close();
                }
            }
        };
        Toolkit.getDefaultToolkit().addAWTEventListener(outsideListener, AWTEvent.MOUSE_EVENT_MASK);
    }

    private void scheduleRefresh() {
        debounceTimer.restart();
        if (field.getText().trim().isEmpty()) {
            SwingUtilities.invokeLater(this::showEmptyRecommendations);
        } else if (popup.isVisible()) {
            rebuildForQuery(field.getText());
        }
    }

    public void showForPanel() {
        if (field.getText().trim().isEmpty()) showEmptyRecommendations();
    }

    public void showEmptyRecommendations() {
        if (field.getText().trim().isEmpty() && PasienSedangDipanggil.getInstance().isAda()) {
            rebuildForQuery("");
        } else {
            close();
        }
    }

    private void rebuildForQuery(String query) {
        if (!popup.isVisible() && query != null && !query.trim().isEmpty()) {
            // Hasil pencarian tampil saat panel sudah terbuka atau melalui keyboard/ketikan.
        }

        visiblePatients.clear();
        visibleBadges.clear();
        noResultShown = false;
        content.removeAll();

        PasienSedangDipanggil sedang = PasienSedangDipanggil.getInstance();
        if (sedang.isAda()) {
            Pasien pasien = findPasienById(sedang.getIdPasien());
            if (pasien != null) {
                addItem(pasien, sedang.getNoAntrean());
            }
        }

        String keyword = query == null ? "" : query.trim();
        if (!keyword.isEmpty()) {
            addSectionLabel("Hasil Pencarian");
            List<Pasien> hasil = repository.searchByNamaOrNik(keyword);
            int batas = Math.min(5, hasil.size());
            for (int i = 0; i < batas; i++) {
                Pasien pasien = hasil.get(i);
                if (!containsPatientId(pasien.getId())) addItem(pasien, null);
            }
            if (batas == 0) addNoResult();
        }

        if (visiblePatients.isEmpty() && !noResultShown) {
            close();
            return;
        }

        selectedIndex = -1;
        content.revalidate();
        content.repaint();
        popup.pack();
        popup.setSize(Math.max(field.getWidth(), 1), Math.min(360, content.getPreferredSize().height));
        Point at = field.getLocationOnScreen();
        popup.setLocation(at.x, at.y + field.getHeight() + 2);
        popup.setVisible(true);
        content.repaint();
    }

    private boolean containsPatientId(String id) {
        for (Pasien pasien : visiblePatients) {
            if (pasien.getId().equalsIgnoreCase(id)) return true;
        }
        return false;
    }

    private Pasien findPasienById(String id) {
        for (Pasien pasien : repository.findAll()) {
            if (pasien.getId().equalsIgnoreCase(id)) return pasien;
        }
        return null;
    }

    private void addSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIStyle.FONT_SMALL);
        label.setForeground(UIStyle.TEXT_SECONDARY);
        label.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(label);
    }

    private void addNoResult() {
        noResultShown = true;
        JLabel label = new JLabel("Pasien tidak ditemukan");
        label.setFont(UIStyle.FONT_BODY);
        label.setForeground(UIStyle.TEXT_SECONDARY);
        label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        label.setPreferredSize(new Dimension(field.getWidth(), 44));
        content.add(label);
    }

    private void addItem(Pasien pasien, String badge) {
        int index = visiblePatients.size();
        visiblePatients.add(pasien);
        visibleBadges.add(badge);

        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(true);
        row.setBackground(Color.WHITE);
        row.setCursor(new Cursor(Cursor.HAND_CURSOR));
        row.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        row.setPreferredSize(new Dimension(field.getWidth(), 44));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        if (badge != null) {
            JLabel badgeLabel = new JLabel(badge);
            badgeLabel.setFont(UIStyle.FONT_BODY_BOLD);
            badgeLabel.setForeground(UIStyle.PRIMARY_COLOR);
            badgeLabel.setOpaque(true);
            badgeLabel.setBackground(UIStyle.SECONDARY_COLOR);
            badgeLabel.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            left.add(badgeLabel);
        }

        JLabel name = new JLabel(pasien.getNama());
        name.setFont(UIStyle.FONT_BODY_BOLD);
        name.setForeground(UIStyle.TEXT_PRIMARY);
        left.add(name);

        JLabel id = new JLabel(pasien.getId());
        id.setFont(UIStyle.FONT_SMALL);
        id.setForeground(UIStyle.TEXT_SECONDARY);

        row.add(left, BorderLayout.CENTER);
        row.add(id, BorderLayout.EAST);

        row.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { setSelected(index); }
            @Override public void mouseExited(MouseEvent e) {
                if (selectedIndex == index) setRowBackground(row, false);
            }
            @Override public void mouseClicked(MouseEvent e) { selectIndex(index); }
        });
        content.add(row);
    }

    private void moveSelection(int delta) {
        if (visiblePatients.isEmpty()) return;
        if (selectedIndex < 0) selectedIndex = delta > 0 ? 0 : visiblePatients.size() - 1;
        else selectedIndex = (selectedIndex + delta + visiblePatients.size()) % visiblePatients.size();
        updateRowSelection();
    }

    private void setSelected(int index) {
        selectedIndex = index;
        updateRowSelection();
    }

    private void updateRowSelection() {
        Component[] components = content.getComponents();
        int rowIndex = 0;
        for (Component component : components) {
            if (component instanceof JPanel) {
                setRowBackground((JPanel) component, rowIndex == selectedIndex);
                rowIndex++;
            }
        }
        content.repaint();
    }

    private void setRowBackground(JPanel row, boolean selected) {
        row.setBackground(selected ? UIStyle.SECONDARY_COLOR : Color.WHITE);
    }

    private void selectIndex(int index) {
        if (index < 0 || index >= visiblePatients.size()) return;
        Pasien pasien = visiblePatients.get(index);
        field.setText(pasien.getNama());
        close();
        onSelect.accept(pasien);
    }

    public void close() {
        debounceTimer.stop();
        if (popup.isVisible()) popup.setVisible(false);
    }

    public void dispose() {
        close();
        if (outsideListener != null) {
            Toolkit.getDefaultToolkit().removeAWTEventListener(outsideListener);
        }
    }
}
