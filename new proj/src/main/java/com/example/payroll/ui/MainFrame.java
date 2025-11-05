package com.example.payroll.ui;

import com.example.payroll.model.Employee;
import com.example.payroll.service.EmployeeService;
import com.example.payroll.data.SampleData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class MainFrame extends JFrame {
    private final EmployeeService service;
    private final EmployeeTableModel tableModel;
    private final JTable table;
    private JScrollPane tableScrollPane;
    private final JTextField nameField;
    private final JTextField salaryField;
    private final JTextField workingDaysField;
    private final JTextField daysPresentField;
    private final JTextField searchField;
    private final JComboBox<String> sortCombo;
    private final Runnable onLogout;
    private JButton viewDetailsBtn;
    private JButton payslipBtn;

    public MainFrame(EmployeeService service, Runnable onLogout) {
        super("Employee Pay Management");
        this.service = service;
        this.onLogout = onLogout;

        tableModel = new EmployeeTableModel();
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        table.setGridColor(new Color(230, 230, 230));
        table.getTableHeader().setBackground(new Color(35, 70, 140));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(255, 242, 204));
        table.setSelectionForeground(Color.BLACK);
        table.setAutoCreateRowSorter(true);
        table.setPreferredScrollableViewportSize(new Dimension(860, 380));

        // Enable/disable action buttons based on selection
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Keep buttons enabled regardless of selection
                if (viewDetailsBtn != null) viewDetailsBtn.setEnabled(true);
                if (payslipBtn != null) payslipBtn.setEnabled(true);
            }
        });

        // Double-click a row to open details
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    onViewDetails(new ActionEvent(table, ActionEvent.ACTION_PERFORMED, "row-double-click"));
                }
            }
        });

        nameField = new JTextField(28);
        // Restrict name input to letters and spaces only at typing time
        ((AbstractDocument) nameField.getDocument()).setDocumentFilter(new LettersAndSpacesFilter());
        salaryField = new JTextField(10);
        workingDaysField = new JTextField(5);
        daysPresentField = new JTextField(5);
        searchField = new JTextField(24);
        sortCombo = new JComboBox<>(new String[]{"ID", "Name", "Basic"});

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(8, 8, 8, 8));
        getContentPane().setBackground(new Color(245, 248, 255));

        add(buildTopPanel(), BorderLayout.NORTH);
        tableScrollPane = new JScrollPane(table);
        add(tableScrollPane, BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        // Ensure sample data is available if empty
        if (service.listAll().isEmpty()) {
            SampleData.populate(service);
            service.saveToFile();
        }
        
        refreshTable(service.listAll());
        if (tableModel.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
            if (payslipBtn != null) payslipBtn.setEnabled(true);
        }
        setSize(900, 520);
        setLocationRelativeTo(null);
    }

    private JPanel buildTopPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx = 0; gc.gridy = 0; panel.add(coloredLabel("Name:"), gc);
        gc.gridx = 1; gc.gridy = 0; panel.add(nameField, gc);
        gc.gridx = 2; gc.gridy = 0; panel.add(coloredLabel("Basic:"), gc);
        gc.gridx = 3; gc.gridy = 0; panel.add(salaryField, gc);
        gc.gridx = 4; gc.gridy = 0; panel.add(coloredLabel("Working Days:"), gc);
        gc.gridx = 5; gc.gridy = 0; panel.add(workingDaysField, gc);
        gc.gridx = 6; gc.gridy = 0; panel.add(coloredLabel("Days Present:"), gc);
        gc.gridx = 7; gc.gridy = 0; panel.add(daysPresentField, gc);

        JButton addBtn = primaryButton("Add");
        addBtn.addActionListener(this::onAdd);
        gc.gridx = 8; gc.gridy = 0; panel.add(addBtn, gc);

        JButton editBtn = secondaryButton("Edit Selected");
        editBtn.addActionListener(this::onEdit);
        gc.gridx = 9; gc.gridy = 0; panel.add(editBtn, gc);

        JButton delBtn = dangerButton("Delete Selected");
        delBtn.addActionListener(this::onDelete);
        gc.gridx = 10; gc.gridy = 0; panel.add(delBtn, gc);

        gc.gridx = 0; gc.gridy = 1; panel.add(coloredLabel("Search name:"), gc);
        gc.gridx = 1; gc.gridy = 1; panel.add(searchField, gc);

        JButton searchBtn = secondaryButton("Search");
        searchBtn.addActionListener(this::onSearch);
        gc.gridx = 2; gc.gridy = 1; panel.add(searchBtn, gc);

        JButton resetBtn = secondaryButton("Reset");
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            sortCombo.setSelectedIndex(0);
            refreshTable(service.listAll());
        });
        gc.gridx = 3; gc.gridy = 1; panel.add(resetBtn, gc);

        gc.gridx = 4; gc.gridy = 1; panel.add(coloredLabel("Sort by:"), gc);
        gc.gridx = 5; gc.gridy = 1; panel.add(sortCombo, gc);
        JButton sortBtn = secondaryButton("Sort");
        sortBtn.addActionListener(e -> refreshTable(service.sortBy((String) sortCombo.getSelectedItem())));
        gc.gridx = 6; gc.gridy = 1; panel.add(sortBtn, gc);

        viewDetailsBtn = primaryButton("View Employee Details");
        viewDetailsBtn.addActionListener(this::onViewDetails);
        viewDetailsBtn.setEnabled(true);
        gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 2; panel.add(viewDetailsBtn, gc);

        payslipBtn = primaryButton("View Payslip");
        payslipBtn.addActionListener(this::onPayslip);
        payslipBtn.setEnabled(true);
        gc.gridx = 2; gc.gridy = 2; gc.gridwidth = 2; panel.add(payslipBtn, gc);

        JButton totalBtn = secondaryButton("Total Net Expense");
        totalBtn.addActionListener(e -> JOptionPane.showMessageDialog(this,
                String.format("Total Monthly Salary Expense: %.2f", service.totalNetExpense()),
                "Expense", JOptionPane.INFORMATION_MESSAGE));
        gc.gridx = 4; gc.gridy = 2; gc.gridwidth = 2; panel.add(totalBtn, gc);

        // Logout button aligned to the far right of controls
        JButton logoutBtn = dangerButton("Logout");
        logoutBtn.addActionListener(e -> doLogout());
        gc.gridx = 10; gc.gridy = 2; gc.gridwidth = 1; panel.add(logoutBtn, gc);

        return panel;
    }

    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel hint = new JLabel("Hint: Select a row to Edit/Delete/View Details/View Payslip");
        hint.setForeground(Color.DARK_GRAY);
        panel.add(hint);
        return panel;
    }

    private JLabel coloredLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(35, 70, 140));
        return l;
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(35, 140, 90));
        b.setForeground(Color.WHITE);
        return b;
    }

    private JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(35, 70, 140));
        b.setForeground(Color.WHITE);
        return b;
    }

    private JButton dangerButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(200, 60, 60));
        b.setForeground(Color.WHITE);
        return b;
    }

    private void onAdd(ActionEvent e) {
        String name = nameField.getText().trim();
        String salaryText = salaryField.getText().trim();
        String wdText = workingDaysField.getText().trim();
        String dpText = daysPresentField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Validate name - only letters and spaces allowed
        if (!name.matches("^[a-zA-Z\\s]+$")) {
            JOptionPane.showMessageDialog(this, "Name must contain only letters and spaces (no numbers)", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double basic;
        try {
            basic = Double.parseDouble(salaryText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Basic must be a number", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (basic <= 0) {
            JOptionPane.showMessageDialog(this, "Basic must be > 0", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int wd = 30;
        int dp = 30;
        try { wd = Integer.parseInt(wdText); } catch (Exception ignored) {}
        try { dp = Integer.parseInt(dpText); } catch (Exception ignored) {}
        
        // Validate: working days should not exceed total days present
        if (wd > 0 && dp > 0 && wd > dp) {
            JOptionPane.showMessageDialog(this, "Working days cannot exceed days present", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        var emp = service.addEmployee(name, basic);
        service.updateEmployee(emp.getEmpId(), null, null, dp, wd);
        service.saveToFile();
        nameField.setText("");
        salaryField.setText("");
        workingDaysField.setText("");
        daysPresentField.setText("");
        refreshTable(service.listAll());
    }

    private void onEdit(ActionEvent e) {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a row to edit", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        Employee selected = tableModel.getAt(modelRow);
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a row to edit", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTextField newName = new JTextField(selected.getName(), 15);
        JTextField newSalary = new JTextField(String.valueOf(selected.getBasicSalary()), 10);
        JTextField newWorkingDays = new JTextField(String.valueOf(selected.getWorkingDaysInMonth()), 6);
        JTextField newDaysPresent = new JTextField(String.valueOf(selected.getDaysPresent()), 6);
        JPanel panel = new JPanel(new GridLayout(0, 2, 6, 6));
        panel.add(new JLabel("Name:")); panel.add(newName);
        panel.add(new JLabel("Basic:")); panel.add(newSalary);
        panel.add(new JLabel("Working Days:")); panel.add(newWorkingDays);
        panel.add(new JLabel("Days Present:")); panel.add(newDaysPresent);
        int res = JOptionPane.showConfirmDialog(this, panel, "Edit Employee #" + selected.getEmpId(), JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            String nm = newName.getText().trim();
            // Validate name - only letters and spaces allowed
            if (!nm.isEmpty() && !nm.matches("^[a-zA-Z\\s]+$")) {
                JOptionPane.showMessageDialog(this, "Name must contain only letters and spaces (no numbers)", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Double sal = null;
            try { sal = Double.parseDouble(newSalary.getText().trim()); } catch (NumberFormatException ignored) {}
            Integer wd = null, dp = null;
            try { wd = Integer.parseInt(newWorkingDays.getText().trim()); } catch (Exception ignored2) {}
            try { dp = Integer.parseInt(newDaysPresent.getText().trim()); } catch (Exception ignored3) {}
            
            // Validate: working days should not exceed total days present
            if (wd != null && dp != null && wd > 0 && dp > 0 && wd > dp) {
                JOptionPane.showMessageDialog(this, "Working days cannot exceed days present", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            service.updateEmployee(selected.getEmpId(), nm, sal, dp, wd);
            service.saveToFile();
            refreshTable(service.listAll());
        }
    }

    private void onDelete(ActionEvent e) {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a row to delete", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        Employee selected = tableModel.getAt(modelRow);
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a row to delete", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int res = JOptionPane.showConfirmDialog(this, "Delete employee #" + selected.getEmpId() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            service.deleteEmployee(selected.getEmpId());
            service.saveToFile();
            refreshTable(service.listAll());
        }
    }

    private void onSearch(ActionEvent e) {
        String q = searchField.getText().trim();
        refreshTable(service.searchByName(q));
    }

    private void onViewDetails(ActionEvent e) {
        // Ensure sample data is available if empty
        if (tableModel.getRowCount() == 0) {
            SampleData.populate(service);
            service.saveToFile();
            List<Employee> employees = service.listAll();
            refreshTable(employees);
            
            // Force immediate UI update
            table.revalidate();
            table.repaint();
            tableScrollPane.revalidate();
            tableScrollPane.repaint();
            
            if (tableModel.getRowCount() > 0) {
                table.setRowSelectionInterval(0, 0);
            }
        }

        // Show input dialog to get Employee ID or Name
        String input = JOptionPane.showInputDialog(
            this,
            "Enter Employee ID or Name to view details:",
            "View Employee Details",
            JOptionPane.QUESTION_MESSAGE
        );

        // Check if input is empty or cancelled
        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter a valid Employee ID or Name.",
                "Invalid Input",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String searchTerm = input.trim();
        Employee selected = null;
        List<Employee> searchResults = null;

        // Try to parse as Employee ID (numeric)
        try {
            int empId = Integer.parseInt(searchTerm);
            selected = service.getById(empId);
        } catch (NumberFormatException ex) {
            // Not a number, search by name
            searchResults = service.searchByName(searchTerm);
            if (searchResults != null && !searchResults.isEmpty()) {
                // If multiple matches, use the first one
                selected = searchResults.get(0);
                // If multiple results, show a message
                if (searchResults.size() > 1) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Found " + searchResults.size() + " employees matching \"" + searchTerm + "\".\nShowing details for: " + selected.getName(),
                        "Multiple Matches",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        }

        // Check if employee was found
        if (selected == null) {
            JOptionPane.showMessageDialog(
                this,
                "No employee found with ID or Name: " + searchTerm + "\nPlease enter a valid Employee ID or Name.",
                "Employee Not Found",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Display employee details in a formatted dialog
        displayEmployeeDetails(selected);
    }

    private void displayEmployeeDetails(Employee employee) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setText(String.join("\n",
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "        EMPLOYEE DETAILS",
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "",
                "Employee ID      : " + employee.getEmpId(),
                "Employee Name    : " + employee.getName(),
                "",
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "        ATTENDANCE INFORMATION",
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "",
                "Working Days     : " + employee.getWorkingDaysInMonth(),
                "Days Present     : " + employee.getDaysPresent(),
                "",
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "        SALARY BREAKDOWN",
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                "",
                "Basic Salary     : ₹ " + String.format("%.2f", employee.getBasicSalary()),
                "Prorated Basic   : ₹ " + String.format("%.2f", employee.getEffectiveBasic()),
                "",
                "HRA (20%)        : ₹ " + String.format("%.2f", employee.getHra()),
                "DA  (10%)        : ₹ " + String.format("%.2f", employee.getDa()),
                "",
                "Gross Salary     : ₹ " + String.format("%.2f", employee.getGrossSalary()),
                "PF  (8%)         : ₹ " + String.format("%.2f", employee.getPf()),
                "",
                "Net Salary       : ₹ " + String.format("%.2f", employee.getNetSalary()),
                "",
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        ));
        
        area.setCaretPosition(0); // Scroll to top
        
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(new Dimension(500, 450));
        
        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "Employee Details - ID: " + employee.getEmpId(),
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void onPayslip(ActionEvent e) {
        int viewRow = table.getSelectedRow();
        Employee selected = null;
        if (viewRow >= 0) {
            int modelRow = table.convertRowIndexToModel(viewRow);
            selected = tableModel.getAt(modelRow);
        }
        if (selected == null && tableModel.getRowCount() > 0) {
            // Auto-select the first row
            table.setRowSelectionInterval(0, 0);
            selected = tableModel.getAt(0);
        }
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a row to view payslip", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setText(String.join("\n",
                "------ Employee Payslip ------",
                "Employee ID   : " + selected.getEmpId(),
                "Employee Name : " + selected.getName(),
                "Working Days  : " + selected.getWorkingDaysInMonth(),
                "Days Present  : " + selected.getDaysPresent(),
                "Basic Salary  : " + selected.getBasicSalary(),
                "Prorated Basic: " + String.format("%.2f", selected.getEffectiveBasic()),
                "HRA (20%)     : " + selected.getHra(),
                "DA  (10%)     : " + selected.getDa(),
                "PF  (8%)      : " + selected.getPf(),
                "Gross Salary  : " + selected.getGrossSalary(),
                "Net Salary    : " + selected.getNetSalary(),
                "------------------------------"));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Payslip", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshTable(List<Employee> rows) {
        tableModel.setRows(rows);
    }

    private void doLogout() {
        int res = JOptionPane.showConfirmDialog(this, "Do you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {
            dispose();
            if (onLogout != null) onLogout.run();
        }
    }

    // Allows only letters and spaces in the name field
    private static class LettersAndSpacesFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string != null && string.matches("[a-zA-Z\\s]+")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) {
                super.replace(fb, offset, length, null, attrs);
                return;
            }
            if (text.matches("[a-zA-Z\\s]+")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
}


