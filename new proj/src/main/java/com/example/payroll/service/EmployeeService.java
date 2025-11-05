package com.example.payroll.service;

import com.example.payroll.model.Employee;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class EmployeeService {
    private final Map<Integer, Employee> idToEmployee = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1001);
    private static final String DATA_FILE = "employees.dat";

    public Employee addEmployee(String name, double basicSalary) {
        int id = nextId.getAndIncrement();
        Employee employee = new Employee(id, name, basicSalary);
        idToEmployee.put(id, employee);
        return employee;
    }

    public boolean deleteEmployee(int empId) {
        return idToEmployee.remove(empId) != null;
    }

    public Employee updateEmployee(int empId, String newName, Double newBasicSalary) {
        Employee employee = idToEmployee.get(empId);
        if (employee == null) return null;
        if (newName != null && !newName.isBlank()) employee.setName(newName);
        if (newBasicSalary != null && newBasicSalary > 0) employee.setBasicSalary(newBasicSalary);
        return employee;
    }

    public Employee updateEmployee(int empId, String newName, Double newBasicSalary, Integer newDaysPresent, Integer newWorkingDays) {
        Employee employee = idToEmployee.get(empId);
        if (employee == null) return null;
        if (newName != null && !newName.isBlank()) employee.setName(newName);
        if (newBasicSalary != null && newBasicSalary > 0) employee.setBasicSalary(newBasicSalary);
        if (newWorkingDays != null && newWorkingDays > 0) employee.setWorkingDaysInMonth(newWorkingDays);
        if (newDaysPresent != null && newDaysPresent >= 0) employee.setDaysPresent(newDaysPresent);
        return employee;
    }

    public Employee getById(int empId) {
        return idToEmployee.get(empId);
    }

    public List<Employee> searchByName(String namePart) {
        String needle = namePart == null ? "" : namePart.toLowerCase();
        return idToEmployee.values().stream()
                .filter(e -> e.getName().toLowerCase().contains(needle))
                .sorted(Comparator.comparingInt(Employee::getEmpId))
                .collect(Collectors.toList());
    }

    public List<Employee> listAll() {
        return idToEmployee.values().stream()
                .sorted(Comparator.comparingInt(Employee::getEmpId))
                .collect(Collectors.toList());
    }

    public List<Employee> sortBy(String field) {
        List<Employee> list = new ArrayList<>(idToEmployee.values());
        switch (field) {
            case "ID" -> list.sort(Comparator.comparingInt(Employee::getEmpId));
            case "Name" -> list.sort(Comparator.comparing(e -> e.getName().toLowerCase()));
            case "Basic" -> list.sort(Comparator.comparingDouble(Employee::getBasicSalary));
            default -> list.sort(Comparator.comparingInt(Employee::getEmpId));
        }
        return list;
    }

    public double totalNetExpense() {
        return idToEmployee.values().stream().mapToDouble(Employee::getNetSalary).sum();
    }

    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeInt(nextId.get());
            oos.writeInt(idToEmployee.size());
            for (Employee emp : idToEmployee.values()) {
                oos.writeObject(emp);
            }
        } catch (IOException e) {
            System.err.println("Error saving employee data: " + e.getMessage());
        }
    }

    public void loadFromFile() {
        Path path = Paths.get(DATA_FILE);
        if (!Files.exists(path)) {
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            nextId.set(ois.readInt());
            int count = ois.readInt();
            idToEmployee.clear();
            for (int i = 0; i < count; i++) {
                Employee emp = (Employee) ois.readObject();
                idToEmployee.put(emp.getEmpId(), emp);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading employee data: " + e.getMessage());
        }
    }
}


