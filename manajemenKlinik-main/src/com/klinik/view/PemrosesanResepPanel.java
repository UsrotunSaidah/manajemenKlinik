package com.klinik.view;

import com.klinik.main.MainApp;
import com.klinik.model.Antrean;
import com.klinik.model.Pasien;
import com.klinik.model.Transaksi;
import com.klinik.repository.TransaksiRepository;
import com.klinik.util.UIStyle;
import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class PemrosesanResepPanel extends JPanel {
    private final JLabel lblTotal = new JLabel("Rp 35.000");
    private final JLabel lblSubtotal = new JLabel("Rp 30.000");
    private final JLabel lblKembalian = new JLabel("Rp 15.000");
    private final JComboBox<String> cmbMetode =
            new JComboBox<>(new String[]{"Tunai", "Transfer Bank", "QRIS", "BPJS / Asuransi"});
    private final JTextField txtJumlahDibayar = new JTextField("Rp 50.000");
    private JLabel lblPaymentError;
    private JPanel cashPaymentFields;
    private JPanel changeBox;
    private final DefaultTableModel resepModel;
    private final TransaksiRepository transaksiRepository;
    private final String petugas;
    private JTable tableResep;

    public PemrosesanResepPanel() {
        this("Petugas", MainApp.transaksiRepository);
    }

    public PemrosesanResepPanel(String petugas) {
        this(petugas, MainApp.transaksiRepository);
    }

    public PemrosesanResepPanel(String petugas, TransaksiRepository transaksiRepository) {
        this.petugas = petugas == null || petugas.trim().isEmpty() ? "Petugas" : petugas.trim();
        this.transaksiRepository = transaksiRepository;
        resepModel = new DefaultTableModel(
                new Object[]{"Nama Obat", "Dosis", "Jumlah"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 14));
        setBackground(UIStyle.BACKGROUND_COLOR);
        setBorder(new EmptyBorder(18, 24, 18, 24));

        JLabel title = new JLabel("Pemrosesan Resep & Pembayaran");
        title.setFont(UIStyle.FONT_TITLE);
        title.setForeground(UIStyle.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(UIStyle.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.weightx = 0.68;
        gbc.insets = new Insets(0, 0, 0, 8);
        content.add(buildResepCard(), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.32;
        gbc.insets = new Insets(0, 8, 0, 0);
        content.add(buildPembayaranCard(), gbc);
        add(content, BorderLayout.CENTER);
    }

    private JPanel buildResepCard() {
        JPanel card = UIStyle.createCardPanel();
        card.setLayout(new BorderLayout(0, 10));

        JLabel title = new JLabel("Resep Obat");
        title.setFont(UIStyle.FONT_SUBTITLE);
        title.setForeground(UIStyle.PRIMARY_COLOR);
        card.add(title, BorderLayout.NORTH);

        resepModel.setColumnIdentifiers(new Object[]{
                "Nama Obat", "Dosis", "Aturan Pakai", "Jumlah", "Harga",
                "Subtotal", "Stok", "Aksi"
        });
        resepModel.addRow(new Object[]{"Paracetamol", "500 mg", "3 x 1, sesudah makan",
                "10 tablet", "Rp 2.000", "Rp 20.000", "Tersedia", "Edit   Hapus"});
        resepModel.addRow(new Object[]{"Vitamin C", "500 mg", "1 x 1, sesudah makan",
                "10 tablet", "Rp 1.000", "Rp 10.000", "Tersedia", "Edit   Hapus"});
        tableResep = new JTable(resepModel);
        UIStyle.styleTable(tableResep);
        tableResep.setFont(UIStyle.FONT_SMALL);
        tableResep.setRowHeight(40);
        tableResep.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {125, 70, 165, 82, 82, 90, 72, 112};
        for (int i = 0; i < widths.length; i++) {
            tableResep.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        tableResep.getColumnModel().getColumn(7).setCellRenderer(createActionRenderer());
        tableResep.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tableResep.rowAtPoint(e.getPoint());
                int column = tableResep.columnAtPoint(e.getPoint());
                if (row >= 0 && column == 7) {
                    Rectangle cell = tableResep.getCellRect(row, column, true);
                    if (e.getX() - cell.x < cell.width / 2) {
                        editObat(row);
                    } else {
                        hapusObat(row);
                    }
                }
            }
        });
        JScrollPane scroll = new JScrollPane(tableResep);
        scroll.setBorder(new LineBorder(UIStyle.BORDER_COLOR, 1));
        card.add(scroll, BorderLayout.CENTER);

        JButton btnTambah = UIStyle.createSecondaryButton("+ Tambah Obat");
        btnTambah.addActionListener(e -> tambahObat());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        south.setBackground(UIStyle.CARD_COLOR);
        south.add(btnTambah);
        card.add(south, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildPembayaranCard() {
        JPanel card = UIStyle.createCardPanel();
        card.setBorder(new javax.swing.border.CompoundBorder(
                new LineBorder(new Color(0xDD, 0xE3, 0xEE), 1, true),
                new EmptyBorder(24, 24, 24, 24)));
        card.setLayout(new BorderLayout(0, 12));

        JPanel details = new JPanel();
        details.setBackground(UIStyle.CARD_COLOR);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        JLabel heading = new JLabel("Rincian Biaya");
        heading.setFont(UIStyle.FONT_SUBTITLE);
        heading.setForeground(UIStyle.TEXT_PRIMARY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        details.add(heading);
        details.add(Box.createVerticalStrut(16));
        details.add(createBillingRow("Subtotal obat", lblSubtotal));
        details.add(Box.createVerticalStrut(8));
        details.add(createBillingRow("Biaya administrasi", createAmountLabel("Rp 5.000")));
        details.add(Box.createVerticalStrut(8));
        details.add(createBillingRow("Diskon", createAmountLabel("Rp 0")));
        details.add(Box.createVerticalStrut(16));
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(0xDD, 0xE3, 0xEE));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        details.add(separator);
        details.add(Box.createVerticalStrut(16));

        JLabel lblTotalTitle = new JLabel("Total Biaya:");
        lblTotalTitle.setFont(UIStyle.FONT_SUBTITLE);
        lblTotalTitle.setForeground(UIStyle.TEXT_PRIMARY);
        lblTotalTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        details.add(lblTotalTitle);
        details.add(Box.createVerticalStrut(4));
        lblTotal.setFont(UIStyle.FONT_TITLE.deriveFont(Font.BOLD, 32f));
        lblTotal.setForeground(UIStyle.TEXT_PRIMARY);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTotal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        details.add(lblTotal);

        JPanel payment = new JPanel();
        payment.setBackground(UIStyle.CARD_COLOR);
        payment.setLayout(new BoxLayout(payment, BoxLayout.Y_AXIS));
        payment.setAlignmentX(Component.LEFT_ALIGNMENT);
        payment.add(Box.createVerticalStrut(16));
        JLabel methodLabel = createPaymentLabel("Metode Pembayaran:");
        methodLabel.setLabelFor(cmbMetode);
        payment.add(methodLabel);
        payment.add(Box.createVerticalStrut(6));
        cmbMetode.setFont(UIStyle.FONT_BODY);
        cmbMetode.setPrototypeDisplayValue("BPJS / Asuransi");
        cmbMetode.setBorder(new javax.swing.border.CompoundBorder(
                new LineBorder(UIStyle.BORDER_COLOR, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        cmbMetode.setMinimumSize(new Dimension(220, 44));
        cmbMetode.setPreferredSize(new Dimension(300, 44));
        cmbMetode.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        cmbMetode.setAlignmentX(Component.LEFT_ALIGNMENT);
        payment.add(cmbMetode);
        payment.add(Box.createVerticalStrut(16));

        cashPaymentFields = new JPanel();
        cashPaymentFields.setBackground(UIStyle.CARD_COLOR);
        cashPaymentFields.setLayout(new BoxLayout(cashPaymentFields, BoxLayout.Y_AXIS));
        cashPaymentFields.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel paidLabel = createPaymentLabel("Jumlah Dibayar:");
        paidLabel.setLabelFor(txtJumlahDibayar);
        cashPaymentFields.add(paidLabel);
        cashPaymentFields.add(Box.createVerticalStrut(6));
        UIStyle.styleTextField(txtJumlahDibayar);
        txtJumlahDibayar.setFont(new Font(UIStyle.FONT_FAMILY, Font.PLAIN, 15));
        txtJumlahDibayar.setBorder(new javax.swing.border.CompoundBorder(
                new LineBorder(UIStyle.BORDER_COLOR, 1, true),
                new EmptyBorder(10, 12, 10, 12)));
        txtJumlahDibayar.setMinimumSize(new Dimension(220, 44));
        txtJumlahDibayar.setPreferredSize(new Dimension(300, 44));
        txtJumlahDibayar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        txtJumlahDibayar.setAlignmentX(Component.LEFT_ALIGNMENT);
        cashPaymentFields.add(txtJumlahDibayar);
        cashPaymentFields.add(Box.createVerticalStrut(12));

        changeBox = new JPanel(new BorderLayout(8, 0));
        changeBox.setBackground(new Color(0xF1, 0xF5, 0xF9));
        changeBox.setBorder(new EmptyBorder(14, 16, 14, 16));
        changeBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        changeBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel changeTitle = new JLabel("Kembalian");
        changeTitle.setFont(new Font(UIStyle.FONT_FAMILY, Font.PLAIN, 15));
        changeTitle.setForeground(UIStyle.TEXT_SECONDARY);
        lblKembalian.setFont(new Font(UIStyle.FONT_FAMILY, Font.BOLD, 20));
        lblKembalian.setForeground(UIStyle.TEXT_PRIMARY);
        changeBox.add(changeTitle, BorderLayout.WEST);
        changeBox.add(lblKembalian, BorderLayout.EAST);
        cashPaymentFields.add(changeBox);
        payment.add(cashPaymentFields);

        lblPaymentError = new JLabel("Jumlah dibayar kurang dari total");
        lblPaymentError.setFont(new Font(UIStyle.FONT_FAMILY, Font.PLAIN, 13));
        lblPaymentError.setForeground(UIStyle.DANGER_COLOR);
        lblPaymentError.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPaymentError.setVisible(false);
        payment.add(Box.createVerticalStrut(8));
        payment.add(lblPaymentError);

        JPanel center = new JPanel();
        center.setBackground(UIStyle.CARD_COLOR);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setAlignmentX(Component.LEFT_ALIGNMENT);
        details.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(details);
        center.add(Box.createVerticalStrut(6));
        center.add(payment);
        JScrollPane centerScroll = new JScrollPane(center);
        centerScroll.setBorder(null);
        centerScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        centerScroll.getVerticalScrollBar().setUnitIncrement(16);
        card.add(centerScroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(1, 2, 8, 0));
        actions.setBackground(UIStyle.CARD_COLOR);
        JButton btnProses = UIStyle.createPrimaryButton("Proses Pembayaran");
        JButton btnBatal = UIStyle.createSecondaryButton("Batal");
        btnProses.setFont(UIStyle.FONT_SMALL.deriveFont(Font.BOLD));
        btnProses.setBorder(new EmptyBorder(9, 8, 9, 8));
        btnBatal.setFont(UIStyle.FONT_SMALL.deriveFont(Font.BOLD));
        btnBatal.setBorder(new EmptyBorder(9, 8, 9, 8));
        btnProses.addActionListener(e -> prosesPembayaran());
        btnBatal.addActionListener(e -> reset());
        actions.add(btnProses);
        actions.add(btnBatal);
        card.add(actions, BorderLayout.SOUTH);

        txtJumlahDibayar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTotals();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTotals();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTotals();
            }
        });
        cmbMetode.addActionListener(e -> updatePaymentMethodVisibility());
        updatePaymentMethodVisibility();
        return card;
    }

    private JPanel createBillingRow(String title, JLabel amount) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(UIStyle.CARD_COLOR);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = createFieldLabel(title);
        label.setForeground(UIStyle.TEXT_SECONDARY);
        label.setFont(new Font(UIStyle.FONT_FAMILY, Font.PLAIN, 15));
        amount.setFont(new Font(UIStyle.FONT_FAMILY, Font.PLAIN, 15));
        row.add(label, BorderLayout.CENTER);
        row.add(amount, BorderLayout.EAST);
        return row;
    }

    private JLabel createAmountLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.RIGHT);
        label.setFont(UIStyle.FONT_SMALL);
        label.setForeground(UIStyle.TEXT_PRIMARY);
        return label;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIStyle.FONT_SMALL);
        label.setForeground(UIStyle.TEXT_PRIMARY);
        return label;
    }

    private JLabel createPaymentLabel(String text) {
        JLabel label = createFieldLabel(text);
        label.setFont(new Font(UIStyle.FONT_FAMILY, Font.PLAIN, 15));
        return label;
    }

    private TableCellRenderer createActionRenderer() {
        return (table, value, isSelected, hasFocus, row, column) -> {
            JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            actions.setOpaque(true);
            actions.setBackground(isSelected ? UIStyle.SECONDARY_COLOR : Color.WHITE);
            JButton edit = new JButton("Edit");
            edit.setFont(UIStyle.FONT_SMALL);
            edit.setForeground(UIStyle.PRIMARY_COLOR);
            edit.setBackground(UIStyle.SECONDARY_COLOR);
            edit.setBorder(new EmptyBorder(4, 7, 4, 7));
            JButton delete = new JButton("Hapus");
            delete.setFont(UIStyle.FONT_SMALL);
            delete.setForeground(UIStyle.DANGER_COLOR);
            delete.setBackground(new Color(0xFE, 0xE2, 0xE2));
            delete.setBorder(new EmptyBorder(4, 7, 4, 7));
            actions.add(edit);
            actions.add(delete);
            return actions;
        };
    }

    private void tambahObat() {
        tampilkanFormObat(-1);
    }

    private void editObat(int row) {
        tampilkanFormObat(row);
    }

    private void hapusObat(int row) {
        int konfirmasi = JOptionPane.showConfirmDialog(
                this,
                "Hapus " + resepModel.getValueAt(row, 0) + " dari resep?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (konfirmasi == JOptionPane.YES_OPTION) {
            resepModel.removeRow(row);
            updateTotals();
        }
    }

    private void tampilkanFormObat(int row) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, row < 0 ? "Tambah Obat" : "Edit Obat",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        ArrayList<MedicineOption> catalog = new ArrayList<>(Arrays.asList(
                new MedicineOption("OBT-001", "Amoxicillin 500 mg", "500 mg",
                        "Kapsul", "Kapsul", 3000, 120),
                new MedicineOption("OBT-002", "Amoxicillin sirup 125 mg/5 ml", "125 mg/5 ml",
                        "ml", "Sirup", 12000, 14),
                new MedicineOption("OBT-003", "Amlodipine 5 mg", "5 mg",
                        "Tablet", "Tablet", 1500, 85),
                new MedicineOption("OBT-004", "Ambroxol 30 mg", "30 mg",
                        "Tablet", "Tablet", 1000, 60),
                new MedicineOption("OBT-005", "Antasida DOEN", "200 mg",
                        "Tablet", "Tablet kunyah", 500, 200),
                new MedicineOption("OBT-006", "Cetirizine 10 mg", "10 mg",
                        "Tablet", "Tablet", 1000, 75),
                new MedicineOption("OBT-007", "Dexamethasone 0.5 mg", "0.5 mg",
                        "Tablet", "Tablet", 500, 90),
                new MedicineOption("OBT-008", "Ibuprofen 400 mg", "400 mg",
                        "Tablet", "Tablet", 1500, 65),
                new MedicineOption("OBT-009", "Lansoprazole 30 mg", "30 mg",
                        "Kapsul", "Kapsul", 2500, 50),
                new MedicineOption("OBT-010", "Metformin 500 mg", "500 mg",
                        "Tablet", "Tablet", 2000, 100),
                new MedicineOption("OBT-011", "Omeprazole 20 mg", "20 mg",
                        "Kapsul", "Kapsul", 2500, 70),
                new MedicineOption("OBT-012", "Paracetamol 500 mg", "500 mg",
                        "Tablet", "Tablet", 1000, 150),
                new MedicineOption("OBT-013", "Prednisone 5 mg", "5 mg",
                        "Tablet", "Tablet", 1000, 45),
                new MedicineOption("OBT-014", "Ranitidine 150 mg", "150 mg",
                        "Tablet", "Tablet", 1000, 50),
                new MedicineOption("OBT-015", "Salbutamol 2 mg", "2 mg",
                        "Tablet", "Tablet", 750, 60),
                new MedicineOption("OBT-016", "Vitamin C 500 mg", "500 mg",
                        "Tablet", "Tablet", 1000, 120)
        ));
        if (row >= 0 && catalog.stream().noneMatch(option ->
                option.name.equalsIgnoreCase(valueAt(row, 0)))) {
            String quantity = valueAt(row, 3);
            String unit = quantity.replaceFirst("^\\d+\\s*", "");
            catalog.add(0, new MedicineOption(
                    "MANUAL",
                    valueAt(row, 0),
                    valueAt(row, 1),
                    normalizeMedicineUnit(unit),
                    unit.isEmpty() ? "Tablet" : unit,
                    parseRupiah(valueAt(row, 4)),
                    -1
            ));
        }

        String initialName = row < 0 ? "" : valueAt(row, 0);
        MedicineOption initialOption = catalog.stream()
                .filter(option -> option.name.equalsIgnoreCase(initialName))
                .findFirst()
                .orElse(catalog.get(0));
        JTextField search = createSearchField(initialName);
        JLabel searchLabel = createDialogLabel("Cari Obat");
        searchLabel.setLabelFor(search);
        JLabel searchHelp = new JLabel();
        searchHelp.setFont(new Font(UIStyle.FONT_FAMILY, Font.PLAIN, 13));
        searchHelp.setForeground(new Color(0x47, 0x55, 0x69));
        searchHelp.setAlignmentX(Component.LEFT_ALIGNMENT);

        DefaultListModel<MedicineOption> medicineListModel = new DefaultListModel<>();
        JList<MedicineOption> medicineList = new JList<>(medicineListModel);
        medicineList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medicineList.setCellRenderer(createMedicineRenderer());
        medicineList.setFixedCellHeight(36);
        medicineList.setFixedCellWidth(520);
        medicineList.setVisibleRowCount(3);
        medicineList.setBackground(Color.WHITE);
        refreshMedicineList(medicineListModel, catalog, initialName);
        medicineList.setSelectedValue(initialOption, true);
        searchHelp.setText(getMedicineSearchHelp(medicineListModel, search.getText()));

        JScrollPane medicineScroll = new JScrollPane(medicineList);
        medicineScroll.setBorder(new LineBorder(new Color(0xDD, 0xE3, 0xEE), 1, true));
        medicineScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        int medicineListHeight = getMedicineListHeight(medicineListModel);
        medicineScroll.setPreferredSize(new Dimension(560, medicineListHeight));
        medicineScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, medicineListHeight));
        medicineScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField dosis = createDialogField(initialOption.dose);
        JComboBox<String> satuan = new JComboBox<>(new String[]{"Kapsul", "Tablet", "ml"});
        styleDialogCombo(satuan);
        satuan.setSelectedItem(initialOption.unit);
        JTextField harga = createDialogField(formatRupiah(initialOption.price));
        harga.setEditable(false);
        harga.setBackground(new Color(0xF1, 0xF5, 0xF9));
        JTextField jumlahPerPemakaian = createDialogField(
                row < 0 ? "1" : String.valueOf(Math.max(1, parseNumber(valueAt(row, 3)))));
        JTextField lamaPemakaian = createDialogField(row < 0 ? "5" : "1");
        JTextField jumlahTotal = createDialogField("");
        JTextField subtotal = createDialogField("");
        JTextField catatan = createDialogField(row < 0 ? "Habiskan sesuai anjuran" : valueAt(row, 2));
        jumlahTotal.setEditable(false);
        subtotal.setEditable(false);
        jumlahTotal.setBackground(new Color(0xF1, 0xF5, 0xF9));
        subtotal.setBackground(new Color(0xF1, 0xF5, 0xF9));

        JComboBox<String> frekuensi = new JComboBox<>(
                new String[]{"3 x sehari", "2 x sehari", "1 x sehari"});
        frekuensi.setSelectedIndex(row < 0 ? 0 : 2);
        JComboBox<String> waktuMinum = new JComboBox<>(
                new String[]{"Sesudah makan", "Sebelum makan", "Saat makan"});
        waktuMinum.setSelectedIndex(0);
        styleDialogCombo(frekuensi);
        styleDialogCombo(waktuMinum);

        JLabel stokError = new JLabel("Stok tidak mencukupi");
        stokError.setFont(new Font(UIStyle.FONT_FAMILY, Font.PLAIN, 12));
        stokError.setForeground(UIStyle.DANGER_COLOR);
        stokError.setText(" ");
        stokError.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel form = new JPanel(new GridLayout(3, 3, 10, 8));
        form.setBackground(Color.WHITE);
        form.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.setPreferredSize(new Dimension(560, 200));
        form.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        form.add(createDialogFieldPanel("Dosis", dosis));
        form.add(createDialogFieldPanel("Satuan", satuan));
        form.add(createDialogFieldPanel("Harga Satuan", harga));
        form.add(createDialogFieldPanel("Frekuensi per Hari", frekuensi));
        form.add(createDialogFieldPanel("Jumlah per Pemakaian", jumlahPerPemakaian));
        form.add(createDialogFieldPanel("Waktu Minum", waktuMinum));
        form.add(createDialogFieldPanel("Lama Pemakaian (hari)", lamaPemakaian));
        form.add(createDialogFieldPanel("Jumlah Total", jumlahTotal));
        form.add(createDialogFieldPanel("Subtotal", subtotal));

        JPanel searchSection = new JPanel();
        searchSection.setBackground(Color.WHITE);
        searchSection.setLayout(new BoxLayout(searchSection, BoxLayout.Y_AXIS));
        searchSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchSection.add(searchLabel);
        searchSection.add(Box.createVerticalStrut(4));
        searchSection.add(search);
        searchSection.add(Box.createVerticalStrut(4));
        searchSection.add(searchHelp);
        searchSection.add(Box.createVerticalStrut(4));
        searchSection.add(medicineScroll);

        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(searchSection);
        content.add(Box.createVerticalStrut(6));
        content.add(form);
        content.add(stokError);
        content.add(Box.createVerticalStrut(6));
        content.add(createDialogFieldPanel("Catatan (opsional)", catatan));

        MedicineOption[] selectedMedicine = {initialOption};
        Runnable recalculate = () -> {
            int frequency = parseNumber(String.valueOf(frekuensi.getSelectedItem()));
            int perUse = parseNumber(jumlahPerPemakaian.getText());
            int days = parseNumber(lamaPemakaian.getText());
            int quantity = frequency * perUse * days;
            int price = parseRupiah(harga.getText());
            String unit = String.valueOf(satuan.getSelectedItem()).toLowerCase();
            jumlahTotal.setText(quantity + " " + unit);
            subtotal.setText(formatRupiah(quantity * price));
            boolean exceedsStock = selectedMedicine[0] != null
                    && selectedMedicine[0].stock >= 0
                    && quantity > selectedMedicine[0].stock;
            stokError.setText(exceedsStock ? "Stok tidak mencukupi" : " ");
        };
        applyMedicineSelection(initialOption, dosis, satuan, harga);
        if (row >= 0 && initialOption.stock < 0) {
            dosis.setText(valueAt(row, 1));
            harga.setText(valueAt(row, 4));
            String unit = valueAt(row, 3).replaceFirst("^\\d+\\s*", "");
            satuan.setSelectedItem(normalizeMedicineUnit(unit));
        }
        frekuensi.addActionListener(e -> recalculate.run());
        satuan.addActionListener(e -> recalculate.run());
        jumlahPerPemakaian.getDocument().addDocumentListener(createDocumentListener(recalculate));
        lamaPemakaian.getDocument().addDocumentListener(createDocumentListener(recalculate));
        medicineList.addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting() && medicineList.getSelectedValue() != null) {
                MedicineOption option = medicineList.getSelectedValue();
                selectedMedicine[0] = option;
                applyMedicineSelection(option, dosis, satuan, harga);
                if (!search.getText().trim().equalsIgnoreCase(option.name)) {
                    search.setText(option.name);
                }
                recalculate.run();
            }
        });
        search.getDocument().addDocumentListener(createDocumentListener(() -> {
            String query = search.getText().trim();
            if (selectedMedicine[0] != null
                    && !selectedMedicine[0].name.equalsIgnoreCase(query)) {
                selectedMedicine[0] = null;
            }
            refreshMedicineList(medicineListModel, catalog, query);
            searchHelp.setText(getMedicineSearchHelp(medicineListModel, query));
            int listHeight = getMedicineListHeight(medicineListModel);
            medicineScroll.setPreferredSize(new Dimension(560, listHeight));
            medicineScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, listHeight));
            searchSection.revalidate();
            if (selectedMedicine[0] != null
                    && selectedMedicine[0].name.equalsIgnoreCase(query)) {
                medicineList.setSelectedValue(selectedMedicine[0], true);
            }
            recalculate.run();
            if (dialog.isVisible()) {
                fitMedicineDialog(dialog);
            }
        }));
        recalculate.run();

        JPanel body = new JPanel(new BorderLayout(0, 8));
        body.setBackground(Color.WHITE);
        body.setBorder(new javax.swing.border.CompoundBorder(
                new LineBorder(new Color(0xDD, 0xE3, 0xEE), 1, true),
                new EmptyBorder(14, 14, 14, 14)));
        JLabel title = new JLabel(row < 0 ? "Tambah Obat" : "Edit Obat");
        title.setFont(new Font(UIStyle.FONT_FAMILY, Font.BOLD, 22));
        title.setForeground(new Color(0x0B, 0x5C, 0xBF));
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.add(title, BorderLayout.WEST);
        body.add(header, BorderLayout.NORTH);
        body.add(content, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setBackground(Color.WHITE);
        JButton cancel = createDialogButton("Batal", false);
        JButton save = createDialogButton(
                row < 0 ? "Tambahkan ke Resep" : "Simpan Perubahan", true);
        cancel.setPreferredSize(new Dimension(110, 38));
        cancel.setMinimumSize(new Dimension(110, 38));
        save.setPreferredSize(new Dimension(190, 38));
        save.setMinimumSize(new Dimension(190, 38));
        cancel.addActionListener(e -> dialog.dispose());
        save.addActionListener(e -> {
            String namaObat = search.getText().trim();
            MedicineOption selected = selectedMedicine[0];
            int jumlahObat = parseNumber(jumlahTotal.getText());
            int hargaObat = parseNumber(harga.getText());
            int perUse = parsePositiveInteger(jumlahPerPemakaian.getText());
            int days = parsePositiveInteger(lamaPemakaian.getText());
            if (selected == null || (!namaObat.isEmpty()
                    && !selected.name.equalsIgnoreCase(namaObat))
                    || dosis.getText().trim().isEmpty()
                    || perUse <= 0 || days <= 0
                    || jumlahObat <= 0 || hargaObat <= 0) {
                JOptionPane.showMessageDialog(dialog,
                        "Pilih obat dan isi dosis, jumlah pemakaian, serta lama pemakaian dengan benar.",
                        "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (selected.stock >= 0 && jumlahObat > selected.stock) {
                stokError.setText("Stok tidak mencukupi");
                return;
            }

            String aturan = frekuensi.getSelectedItem() + ", "
                    + String.valueOf(waktuMinum.getSelectedItem()).toLowerCase();
            if (!catatan.getText().trim().isEmpty()) {
                aturan += " - " + catatan.getText().trim();
            }
            Object[] data = {
                    namaObat,
                    dosis.getText().trim(),
                    aturan,
                    jumlahTotal.getText(),
                    formatRupiah(hargaObat),
                    subtotal.getText(),
                    selected.stock < 0 ? "Manual"
                            : selected.stock > 0 ? "Tersedia" : "Habis",
                    "Edit   Hapus"
            };
            if (row < 0) {
                resepModel.addRow(data);
            } else {
                for (int i = 0; i < data.length; i++) {
                    resepModel.setValueAt(data[i], row, i);
                }
            }
            updateTotals();
            dialog.dispose();
        });
        buttons.add(cancel);
        buttons.add(save);
        body.add(buttons, BorderLayout.SOUTH);
        dialog.setContentPane(body);
        dialog.setResizable(false);
        fitMedicineDialog(dialog);
        SwingUtilities.invokeLater(search::requestFocusInWindow);
        dialog.setVisible(true);
    }

    private int getMedicineListHeight(DefaultListModel<MedicineOption> model) {
        return Math.max(38, Math.min(110, model.getSize() * 36 + 2));
    }

    private String getMedicineSearchHelp(
            DefaultListModel<MedicineOption> model,
            String query) {
        return query.isEmpty()
                ? model.getSize() + " obat tersedia, urut abjad"
                : model.getSize() + " obat ditemukan";
    }

    private void fitMedicineDialog(JDialog dialog) {
        dialog.pack();
        Rectangle screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();
        int dialogWidth = Math.max(1, Math.min(620, screenBounds.width - 32));
        int dialogHeight = Math.max(1, Math.min(dialog.getHeight(), screenBounds.height - 24));
        dialog.setSize(dialogWidth, dialogHeight);
        dialog.setLocationRelativeTo(this);
    }

    private void refreshMedicineList(
            DefaultListModel<MedicineOption> model,
            ArrayList<MedicineOption> catalog,
            String query) {
        model.clear();
        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        for (MedicineOption option : catalog) {
            if (option.name.toLowerCase(Locale.ROOT).contains(normalizedQuery)
                    || option.code.toLowerCase(Locale.ROOT).contains(normalizedQuery)) {
                model.addElement(option);
            }
        }
    }

    private ListCellRenderer<MedicineOption> createMedicineRenderer() {
        return (list, option, index, isSelected, cellHasFocus) -> {
            JPanel item = new JPanel(new GridBagLayout());
            item.setOpaque(true);
            item.setBackground(isSelected ? new Color(0xE0, 0xEC, 0xFB) : Color.WHITE);
            item.setBorder(new javax.swing.border.CompoundBorder(
                    new MatteBorder(1, 0, 0, 0, new Color(0xEE, 0xF1, 0xF6)),
                    new EmptyBorder(8, 12, 8, 12)));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.gridx = 0;
            gbc.weightx = 1.0;
            JLabel name = new JLabel(option.name);
            name.setFont(new Font(UIStyle.FONT_FAMILY,
                    isSelected ? Font.BOLD : Font.PLAIN, 14));
            name.setForeground(UIStyle.TEXT_PRIMARY);
            item.add(name, gbc);

            gbc.weightx = 0;
            gbc.gridx = 1;
            gbc.insets = new Insets(0, 8, 0, 0);
            JLabel form = createMedicineColumn(option.form, 90, SwingConstants.LEFT);
            item.add(form, gbc);
            gbc.gridx = 2;
            JLabel stock = createMedicineColumn("Stok " + option.stock, 70, SwingConstants.LEFT);
            if (option.stock < 0) {
                stock.setText("Manual");
            }
            item.add(stock, gbc);
            gbc.gridx = 3;
            JLabel price = createMedicineColumn(formatRupiah(option.price), 80, SwingConstants.RIGHT);
            item.add(price, gbc);
            item.setPreferredSize(new Dimension(680, 42));
            return item;
        };
    }

    private JLabel createMedicineColumn(String text, int width, int alignment) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(new Font(UIStyle.FONT_FAMILY, Font.PLAIN, 13));
        label.setForeground(new Color(0x47, 0x55, 0x69));
        label.setPreferredSize(new Dimension(width, 20));
        return label;
    }

    private void applyMedicineSelection(
            MedicineOption option,
            JTextField dosis,
            JComboBox<String> satuan,
            JTextField harga) {
        dosis.setText(option.dose);
        satuan.setSelectedItem(option.unit);
        harga.setText(formatRupiah(option.price));
    }

    private String normalizeMedicineUnit(String unit) {
        if ("Kapsul".equalsIgnoreCase(unit)) {
            return "Kapsul";
        }
        if ("Tablet".equalsIgnoreCase(unit)) {
            return "Tablet";
        }
        if ("ml".equalsIgnoreCase(unit) || "sirup".equalsIgnoreCase(unit)) {
            return "ml";
        }
        return "Tablet";
    }

    private JButton createDialogButton(String text, boolean primary) {
        Color normal = primary ? new Color(0x0B, 0x5C, 0xBF) : new Color(0xE0, 0xEC, 0xFB);
        Color hover = primary ? new Color(0x0A, 0x4F, 0xA5) : new Color(0xD4, 0xE3, 0xF8);
        Color foreground = primary ? Color.WHITE : new Color(0x0B, 0x4A, 0x9E);
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                Color background = getModel().isPressed() ? hover.darker()
                        : getModel().isRollover() ? hover : normal;
                g2.setColor(background);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(graphics);
            }
        };
        button.setFont(new Font(UIStyle.FONT_FAMILY, Font.BOLD, 14));
        button.setForeground(foreground);
        button.setBackground(normal);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setPreferredSize(new Dimension(155, 38));
        button.setMinimumSize(new Dimension(155, 38));
        return button;
    }

    private JTextField createSearchField(String text) {
        JTextField field = new JTextField(text) {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                if (getText().isEmpty()) {
                    Insets insets = getInsets();
                    Graphics2D g2 = (Graphics2D) graphics.create();
                    g2.setColor(new Color(0x64, 0x74, 0x8B));
                    g2.setFont(getFont());
                    FontMetrics metrics = g2.getFontMetrics();
                    g2.drawString("Ketik nama atau kode obat", insets.left,
                            (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent());
                    g2.dispose();
                }
            }
        };
        styleDialogTextField(field);
        return field;
    }

    private void styleDialogCombo(JComboBox<String> combo) {
        combo.setFont(new Font(UIStyle.FONT_FAMILY, Font.PLAIN, 14));
        combo.setForeground(UIStyle.TEXT_PRIMARY);
        combo.setBackground(Color.WHITE);
        combo.setBorder(new javax.swing.border.CompoundBorder(
                new LineBorder(UIStyle.BORDER_COLOR, 1, true),
                new EmptyBorder(0, 10, 0, 10)));
        combo.setPreferredSize(new Dimension(200, 36));
        combo.setMinimumSize(new Dimension(120, 36));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    }

    private int parsePositiveInteger(String text) {
        try {
            int value = Integer.parseInt(text.trim());
            return value > 0 ? value : 0;
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private DocumentListener createDocumentListener(Runnable action) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                action.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                action.run();
            }
        };
    }

    private static class MedicineOption {
        private final String code;
        private final String name;
        private final String dose;
        private final String unit;
        private final String form;
        private final int price;
        private final int stock;

        private MedicineOption(
                String code,
                String name,
                String dose,
                String unit,
                String form,
                int price,
                int stock) {
            this.code = code;
            this.name = name;
            this.dose = dose;
            this.unit = unit;
            this.form = form;
            this.price = price;
            this.stock = stock;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private JTextField createDialogField(String value) {
        JTextField field = new JTextField(value);
        styleDialogTextField(field);
        return field;
    }

    private void styleDialogTextField(JTextField field) {
        field.setFont(new Font(UIStyle.FONT_FAMILY, Font.PLAIN, 14));
        field.setForeground(UIStyle.TEXT_PRIMARY);
        field.setBackground(new Color(0xFC, 0xFC, 0xFD));
        field.setBorder(new javax.swing.border.CompoundBorder(
                new LineBorder(UIStyle.BORDER_COLOR, 1, true),
                new EmptyBorder(0, 10, 0, 10)));
        field.setPreferredSize(new Dimension(220, 36));
        field.setMinimumSize(new Dimension(0, 36));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    }

    private JLabel createDialogLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(UIStyle.FONT_FAMILY, Font.BOLD, 14));
        label.setForeground(UIStyle.TEXT_PRIMARY);
        return label;
    }

    private JPanel createDialogFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        JLabel label = createDialogLabel(labelText);
        label.setLabelFor(field);
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private String valueAt(int row, int column) {
        return String.valueOf(resepModel.getValueAt(row, column));
    }

    private void updateTotals() {
        int subtotal = 0;
        for (int row = 0; row < resepModel.getRowCount(); row++) {
            subtotal += parseRupiah(valueAt(row, 5));
        }
        int total = subtotal + 5000;
        int dibayar = parseNumber(txtJumlahDibayar.getText());
        lblSubtotal.setText(formatRupiah(subtotal));
        lblTotal.setText(formatRupiah(total));
        boolean cashPayment = "Tunai".equals(cmbMetode.getSelectedItem());
        lblKembalian.setText(dibayar >= total
                ? formatRupiah(dibayar - total)
                : "Kurang " + formatRupiah(total - dibayar));
        lblPaymentError.setVisible(cashPayment && dibayar < total);
        if (cashPaymentFields != null) {
            cashPaymentFields.setVisible(cashPayment);
        }
        if (changeBox != null) {
            changeBox.setVisible(cashPayment);
        }
    }

    private void updatePaymentMethodVisibility() {
        boolean cashPayment = "Tunai".equals(cmbMetode.getSelectedItem());
        cashPaymentFields.setVisible(cashPayment);
        lblPaymentError.setVisible(cashPayment
                && parseNumber(txtJumlahDibayar.getText()) < parseRupiah(lblTotal.getText()));
        cashPaymentFields.revalidate();
        cashPaymentFields.repaint();
    }

    private void prosesPembayaran() {
        int total = parseRupiah(lblTotal.getText());
        int dibayar = parseNumber(txtJumlahDibayar.getText());
        String metode = (String) cmbMetode.getSelectedItem();
        if ("Tunai".equals(metode) && dibayar < total) {
            lblPaymentError.setVisible(true);
            cashPaymentFields.revalidate();
            cashPaymentFields.repaint();
            return;
        }
        Antrean antrean = MainApp.antreanController.getAntreanTerakhirSelesai();
        Pasien pasien = antrean == null ? null : antrean.getPasien();
        if (pasien == null) {
            JOptionPane.showMessageDialog(this,
                    "Belum ada pasien yang selesai diperiksa untuk diproses pembayarannya.",
                    "Pasien Belum Tersedia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Window window = SwingUtilities.getWindowAncestor(this);
        int jumlahDibayar = "Tunai".equals(metode) ? dibayar : total;
        Transaksi transaksi;
        try {
            transaksi = transaksiRepository.simpanPembayaranLunas(
                    antrean.getNomor(),
                    pasien.getId(),
                    total,
                    metode,
                    jumlahDibayar,
                    Math.max(0, jumlahDibayar - total),
                    petugas
            );
        } catch (IOException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Pembayaran tidak dapat disimpan: " + ex.getMessage(),
                    "Gagal Menyimpan Pembayaran", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (window instanceof MainDashboardFrame) {
            ((MainDashboardFrame) window).refreshStatistik();
        }
        new PembayaranBerhasilDialog(
                window,
                transaksi,
                pasien,
                () -> {
                    if (window instanceof MainDashboardFrame) {
                        ((MainDashboardFrame) window).showPage(
                                MainDashboardFrame.PAGE_ANTREAN);
                    }
                }
        ).setVisible(true);
    }

    private int parseRupiah(String value) {
        return parseNumber(value);
    }

    private int parseNumber(String value) {
        try {
            String digits = value.replaceAll("\\D", "");
            return digits.isEmpty() ? 0 : Integer.parseInt(digits);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private void reset() {
        cmbMetode.setSelectedIndex(0);
        txtJumlahDibayar.setText("Rp 50.000");
        updateTotals();
    }

    static String formatRupiah(long value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.of("in", "ID"));
        symbols.setCurrencySymbol("Rp ");
        symbols.setGroupingSeparator('.');
        DecimalFormat format = new DecimalFormat("Rp #,###", symbols);
        format.setGroupingUsed(true);
        return format.format(value);
    }
}