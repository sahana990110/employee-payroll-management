package com.example.payroll.ui;

import com.example.payroll.model.Employee;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class EmployeeTableModel extends AbstractTableModel {
    private final String[] columns = {
            "ID", "Name", "Working Days", "Days Present", "Basic",
            "Prorated Basic", "HRA (20%)", "DA (10%)", "PF (8%)", "Gross", "Net"
    };
    private List<Employee> rows = new ArrayList<>();

    public void setRows(List<Employee> rows) {
        this.rows = rows == null ? new ArrayList<>() : new ArrayList<>(rows);
        fireTableDataChanged();
    }

    public Employee getAt(int row) {
        if (row < 0 || row >= rows.size()) return null;
        return rows.get(row);
    }

    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int column) { return columns[column]; }

    @Override public Object getValueAt(int rowIndex, int columnIndex) {
        Employee e = rows.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> e.getEmpId();
            case 1 -> e.getName();
            case 2 -> e.getWorkingDaysInMonth();
            case 3 -> e.getDaysPresent();
            case 4 -> String.format("%.2f", e.getBasicSalary());
            case 5 -> String.format("%.2f", e.getEffectiveBasic());
            case 6 -> String.format("%.2f", e.getHra());
            case 7 -> String.format("%.2f", e.getDa());
            case 8 -> String.format("%.2f", e.getPf());
            case 9 -> String.format("%.2f", e.getGrossSalary());
            case 10 -> String.format("%.2f", e.getNetSalary());
            default -> "";
        };
    }
}


