package com.example.payroll.ui;

import com.example.payroll.auth.CredentialsStore;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginDialog extends JDialog {
    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);
    private boolean authenticated = false;
    private final CredentialsStore credentialsStore;

    public LoginDialog(Frame owner, CredentialsStore credentialsStore) {
        super(owner, "Login", true);
        this.credentialsStore = credentialsStore;
        buildUi();
        pack();
        setLocationRelativeTo(owner);
        getRootPane().setDefaultButton(getLoginButton());
    }

    private JButton getLoginButton() {
        for (Component c : ((JPanel) getContentPane()).getComponents()) {
            if (c instanceof JPanel panel) {
                for (Component cc : panel.getComponents()) {
                    if (cc instanceof JButton btn && "Login".equals(btn.getText())) return btn;
                }
            }
        }
        return null;
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(new Color(245, 248, 255));

        JLabel title = new JLabel("Employee Pay - Sign In", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setForeground(new Color(35, 70, 140));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx = 0; gc.gridy = 0; form.add(new JLabel("Username:"), gc);
        gc.gridx = 1; gc.gridy = 0; form.add(usernameField, gc);
        gc.gridx = 0; gc.gridy = 1; form.add(new JLabel("Password:"), gc);
        gc.gridx = 1; gc.gridy = 1; form.add(passwordField, gc);

        root.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton loginBtn = new JButton("Login");
        JButton cancelBtn = new JButton("Cancel");
        JButton signUpBtn = new JButton("Sign Up");

        loginBtn.setBackground(new Color(35, 140, 90));
        loginBtn.setForeground(Color.WHITE);
        cancelBtn.setBackground(new Color(200, 60, 60));
        cancelBtn.setForeground(Color.WHITE);

        actions.add(cancelBtn);
        actions.add(loginBtn);
        actions.add(signUpBtn);
        root.add(actions, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> tryLogin());
        cancelBtn.addActionListener(e -> {
            authenticated = false;
            dispose();
        });
        signUpBtn.addActionListener(e -> onSignUp());

        KeyAdapter enterKey = new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) tryLogin();
            }
        };
        usernameField.addKeyListener(enterKey);
        passwordField.addKeyListener(enterKey);

        setContentPane(root);
    }

    private void tryLogin() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());
        if (authenticate(user, pass)) {
            authenticated = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean authenticate(String user, String pass) {
        return credentialsStore.authenticate(user, pass);
    }

    private void onSignUp() {
        JTextField u = new JTextField(15);
        JPasswordField p = new JPasswordField(15);
        JPasswordField c = new JPasswordField(15);
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("New Username:")); form.add(u);
        form.add(new JLabel("Password:")); form.add(p);
        form.add(new JLabel("Confirm Password:")); form.add(c);
        int res = JOptionPane.showConfirmDialog(this, form, "Create Account", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            String user = u.getText().trim();
            String pass = new String(p.getPassword());
            String conf = new String(c.getPassword());
            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!pass.equals(conf)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                if (credentialsStore.userExists(user)) {
                    JOptionPane.showMessageDialog(this, "User already exists.", "Sign Up", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                credentialsStore.addUser(user, pass);
                JOptionPane.showMessageDialog(this, "Account created. You can now log in.", "Sign Up", JOptionPane.INFORMATION_MESSAGE);
                usernameField.setText(user);
                passwordField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to create account: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public boolean isAuthenticated() { return authenticated; }
}


