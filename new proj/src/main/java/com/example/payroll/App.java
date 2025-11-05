package com.example.payroll;

import com.example.payroll.auth.CredentialsStore;
import com.example.payroll.data.SampleData;
import com.example.payroll.service.EmployeeService;
import com.example.payroll.ui.LoginDialog;
import com.example.payroll.ui.MainFrame;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) {}
            java.io.File credFile = new java.io.File("users.properties");
            CredentialsStore store = new CredentialsStore(credFile);

            final Runnable[] openLoginRef = new Runnable[1];
            final Runnable[] openMainRef = new Runnable[1];

            openMainRef[0] = () -> {
                EmployeeService service = new EmployeeService();
                java.io.File dataFile = new java.io.File("employees.dat");
                if (dataFile.exists()) {
                    service.loadFromFile();
                } else {
                    SampleData.populate(service);
                    service.saveToFile();
                }
                new MainFrame(service, openLoginRef[0]).setVisible(true);
            };

            openLoginRef[0] = () -> {
                LoginDialog login = new LoginDialog(null, store);
                login.setVisible(true);
                if (!login.isAuthenticated()) {
                    System.exit(0);
                    return;
                }
                openMainRef[0].run();
            };

            openLoginRef[0].run();
        });
    }
}


