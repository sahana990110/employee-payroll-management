# Employee Pay Management GUI

A simple Swing desktop app with a clean backend service for managing employees and their salaries.

## Features
- Add, edit, delete employees
- Search by name
- Sort by ID, Name, Basic
- View detailed payslip
- Total monthly net salary expense

## Requirements
- Java 17+
- Maven 3.8+

## Run
```bash
mvn -q -DskipTests package
java -jar target/employee-pay-gui-1.0.0.jar
```

Or run directly with:
```bash
mvn -q exec:java -Dexec.mainClass="com.example.payroll.App"
```

## Notes
- Data is in-memory for simplicity. Integrate a DB later if needed.

## Login / Sign Up
- On launch, a sign-in dialog appears.
- Click "Sign Up" to create your own username/password (stored locally in `users.properties`).
- Then log in with your new credentials.
- After sign-in, the app loads a pre-seeded list of 100 employees.

## Preloaded data
- On startup, 100 sample employees are added automatically with varied basic salaries.

