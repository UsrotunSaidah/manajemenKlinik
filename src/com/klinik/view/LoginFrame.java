package com.klinik.view;

import com.klinik.controller.LoginController;
import com.klinik.util.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final LoginController controller;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;
    private JButton btnLogin;

    public LoginFrame() {
        this.controller = new LoginController(this);
        initComponents();
    }

    private void initComponents() {
        setTitle("Login - Klinik Sehat Bersama");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 520);
        setMinimumSize(new Dimension(420, 480));
        setLocationRelativeTo(null);

        JPanel rootPanel = new JPanel(new GridBagLayout());
        rootPanel.setBackground(UIStyle.BACKGROUND_COLOR);
        rootPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel cardPanel = UIStyle.createCardPanel();
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setPreferredSize(new Dimension(380, 420));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Ikon / Logo Klinik
        JLabel lblLogo = new JLabel("+", SwingConstants.CENTER);
        lblLogo.setFont(new Font(UIStyle.FONT_FAMILY, Font.BOLD, 34));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setOpaque(true);
        lblLogo.setBackground(UIStyle.PRIMARY_COLOR);
        lblLogo.setPreferredSize(new Dimension(56, 56));

        JPanel logoWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoWrapper.setBackground(UIStyle.CARD_COLOR);
        logoWrapper.add(lblLogo);

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 8, 0);
        cardPanel.add(logoWrapper, gbc);

        // Nama Klinik
        JLabel lblKlinik = new JLabel("Klinik Sehat Bersama", SwingConstants.CENTER);
        lblKlinik.setFont(UIStyle.FONT_TITLE);
        lblKlinik.setForeground(UIStyle.PRIMARY_COLOR);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 4, 0);
        cardPanel.add(lblKlinik, gbc);

        // Judul Sistem
        JLabel lblSub = new JLabel("Sistem Manajemen Rekam Medis", SwingConstants.CENTER);
        lblSub.setFont(UIStyle.FONT_BODY);
        lblSub.setForeground(UIStyle.TEXT_SECONDARY);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 22, 0);
        cardPanel.add(lblSub, gbc);

        // Username
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UIStyle.FONT_BODY_BOLD);
        lblUser.setForeground(UIStyle.TEXT_PRIMARY);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 6, 0);
        cardPanel.add(lblUser, gbc);

        txtUsername = new JTextField();
        UIStyle.styleTextField(txtUsername);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 14, 0);
        cardPanel.add(txtUsername, gbc);

        // Password
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UIStyle.FONT_BODY_BOLD);
        lblPass.setForeground(UIStyle.TEXT_PRIMARY);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 6, 0);
        cardPanel.add(lblPass, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setFont(UIStyle.FONT_BODY);
        txtPassword.setForeground(UIStyle.TEXT_PRIMARY);
        txtPassword.setBorder(UIStyle.INPUT_BORDER);
        char defaultEchoChar = txtPassword.getEchoChar();
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 8, 0);
        cardPanel.add(txtPassword, gbc);

        // Show / Hide Password
        chkShowPassword = new JCheckBox("Tampilkan password");
        chkShowPassword.setFont(UIStyle.FONT_SMALL);
        chkShowPassword.setForeground(UIStyle.TEXT_SECONDARY);
        chkShowPassword.setBackground(UIStyle.CARD_COLOR);
        chkShowPassword.setFocusPainted(false);
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar(defaultEchoChar);
            }
        });
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 20, 0);
        cardPanel.add(chkShowPassword, gbc);

        // Tombol Login
        btnLogin = UIStyle.createPrimaryButton("Login");
        btnLogin.addActionListener(e -> controller.handleLogin(
                txtUsername.getText(),
                new String(txtPassword.getPassword())
        ));
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 0, 0);
        cardPanel.add(btnLogin, gbc);

        // Tekan Enter untuk login
        getRootPane().setDefaultButton(btnLogin);

        rootPanel.add(cardPanel);
        setContentPane(rootPanel);
    }
}
