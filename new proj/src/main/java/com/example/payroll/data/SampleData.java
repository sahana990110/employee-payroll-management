package com.example.payroll.data;

import com.example.payroll.service.EmployeeService;

import java.util.Random;

public final class SampleData {
    private SampleData() {}

    public static void populate(EmployeeService service) {
        String[] firstNames = {
                "Aarav","Vivaan","Aditya","Vihaan","Arjun","Reyansh","Muhammad","Sai","Arnav","Ayaan",
                "Isha","Anaya","Diya","Ira","Aadhya","Meera","Saanvi","Anika","Navya","Pari"
        };
        String[] lastNames = {
                "Sharma","Verma","Gupta","Iyer","Reddy","Patel","Khan","Singh","Das","Nair"
        };
        Random random = new Random(42); // deterministic
        for (int i = 1; i <= 100; i++) {
            String name = firstNames[random.nextInt(firstNames.length)] + " " + lastNames[random.nextInt(lastNames.length)];
            double basic = 20000 + random.nextInt(60001); // 20k - 80k
            var emp = service.addEmployee(name, basic);
            // Ensure workingDays <= daysPresent (validation rule)
            int present = 22 + random.nextInt(9); // 22 - 30 (days present)
            int workingDays = 20 + random.nextInt(present - 19); // 20 to present (working days <= days present)
            service.updateEmployee(emp.getEmpId(), null, null, present, workingDays);
        }
    }
}


