package com.example.payroll.model;

import java.io.Serializable;

public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    private int empId;
    private String name;
    private double basicSalary;
    private int workingDaysInMonth = 30;
    private int daysPresent = 30;
    private double effectiveBasic;
    private double hra;
    private double da;
    private double pf;
    private double grossSalary;
    private double netSalary;

    public Employee(int empId, String name, double basicSalary) {
        this.empId = empId;
        this.name = name;
        this.basicSalary = basicSalary;
        calculateSalary();
    }

    public final void calculateSalary() {
        double ratio = (workingDaysInMonth > 0) ? Math.max(0.0, Math.min(1.0, (double) daysPresent / (double) workingDaysInMonth)) : 0.0;
        effectiveBasic = basicSalary * ratio;
        hra = 0.20 * effectiveBasic;
        da  = 0.10 * effectiveBasic;
        pf  = 0.08 * effectiveBasic;
        grossSalary = effectiveBasic + hra + da;
        netSalary   = grossSalary - pf;
    }

    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; calculateSalary(); }
    public int getWorkingDaysInMonth() { return workingDaysInMonth; }
    public void setWorkingDaysInMonth(int workingDaysInMonth) { this.workingDaysInMonth = workingDaysInMonth; calculateSalary(); }
    public int getDaysPresent() { return daysPresent; }
    public void setDaysPresent(int daysPresent) { this.daysPresent = daysPresent; calculateSalary(); }
    public double getEffectiveBasic() { return effectiveBasic; }
    public double getHra() { return hra; }
    public double getDa() { return da; }
    public double getPf() { return pf; }
    public double getGrossSalary() { return grossSalary; }
    public double getNetSalary() { return netSalary; }
}


