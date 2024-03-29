package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;

public class App {
    private Connection con = null;

    public void connect(String location, int delay) {
        try {
            // Load Database driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i) {
            System.out.println("Connecting to database...");
            try {
                // Wait a bit for db to start
                Thread.sleep(delay);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://" + location
                                + "/employees?allowPublicKeyRetrieval=true&useSSL=false",
                        "root", "example");
                System.out.println("Successfully connected");
                break;
            } catch (SQLException sqle) {
                System.out.println("Failed to connect to database attempt " +                                  Integer.toString(i));
                System.out.println(sqle.getMessage());
            } catch (InterruptedException ie) {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    public void disconnect() {
        if (con != null) {
            try {
                con.close();
            } catch (Exception e) {
                System.out.println("Error closing connection to database");
            }
        }
    }

    public Employee getEmployee(int ID) {
        try {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT employees.emp_no, salaries.salary, dept_emp.dept_no, departments.dept_name,"
                            + " dept_manager.emp_no AS dept_manager_emp_no,"
                            + " emp_manager.first_name AS manager_first_name,"
                            + " emp_manager.last_name AS manager_last_name"
                            + " FROM employees"
                            + " JOIN salaries ON salaries.emp_no = employees.emp_no"
                            + " JOIN dept_emp ON dept_emp.emp_no = employees.emp_no"
                            + " JOIN departments ON departments.dept_no = dept_emp.dept_no"
                            + " JOIN dept_manager ON dept_manager.dept_no = departments.dept_no"
                            + " JOIN employees AS emp_manager ON emp_manager.emp_no = dept_manager.emp_no"
                            + " WHERE employees.emp_no =" + ID + " AND salaries.to_date = '9999-01-01'";
            ResultSet rset = stmt.executeQuery(strSelect);

            if (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("emp_no");
                emp.first_name = rset.getString("manager_first_name");
                emp.last_name = rset.getString("manager_last_name");
                emp.salary = rset.getInt("salary");
                emp.dept_no = rset.getString("dept_no");
                emp.dept_name = rset.getString("dept_name");
                return emp;
            } else
                return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    public void displayEmployee(Employee emp) {
        if (emp != null) {
            System.out.println(
                    emp.emp_no + " "
                            + "Manager: " + emp.first_name + " "
                            + emp.last_name + "\n"
                            + "Emp Salary:" + emp.salary + "\n"
                            + "Department Name: " + emp.dept_name + "\n"
                            + "Department Num: " + emp.dept_no);
        }
    }

    public ArrayList<Employee> getAllSalaries() {
        try {
            Statement stmt = con.createStatement();
            String strSelect =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary"
                            + " FROM employees, salaries"
                            + " WHERE employees.emp_no = salaries.emp_no AND salaries.to_date = '9999-01-01'"
                            + " ORDER BY employees.emp_no ASC";
            ResultSet rset = stmt.executeQuery(strSelect);

            ArrayList<Employee> employees = new ArrayList<>();
            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }

    public void printSalaries(ArrayList<Employee> employees)
    {
        // Check employees is not null
        if (employees == null)
        {
            System.out.println("No employees");
            return;
        }
        // Print header
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        // Loop over all employees in the list
        for (Employee emp : employees)
        {
            if (emp == null)
                continue;
            String emp_string =
                    String.format("%-10s %-15s %-20s %-8s",
                            emp.emp_no, emp.first_name, emp.last_name, emp.salary);
            System.out.println(emp_string);
        }
    }
    public ArrayList<Employee> getDepartment(String dept_name) {
        try {
            // Create an SQL statement
            Statement stmt_ = con.createStatement();
            // Create string for SQL statement
            String strSelect_ =
                    "SELECT employees.emp_no, employees.first_name, employees.last_name, salaries.salary "
                            + "FROM employees, departments, salaries, dept_emp "
                            + "WHERE employees.emp_no = salaries.emp_no "
                            + "AND employees.emp_no = dept_emp.emp_no "
                            + "AND dept_emp.dept_no = departments.dept_no "
                            + "AND salaries.to_date = '9999-01-01' "
                            + "AND departments.dept_name = '" + dept_name + "'"
                            + "ORDER BY employees.emp_no ASC";
            // Execute SQL statement
            ResultSet rset = stmt_.executeQuery(strSelect_);
            // Extract employee information
            ArrayList<Employee> employees = new ArrayList<Employee>();
            while (rset.next()) {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("employees.emp_no");
                emp.first_name = rset.getString("employees.first_name");
                emp.last_name = rset.getString("employees.last_name");
                emp.salary = rset.getInt("salaries.salary");
                employees.add(emp);
            }
            return employees;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get salary details");
            return null;
        }
    }


    public static void main(String[] args) {
        // Create new Application
        App a = new App();

        // Connect to database
        if(args.length < 1){
            a.connect("localhost:33060", 30000);
        }else{
            a.connect(args[0], Integer.parseInt(args[1]));
        }

        // Get Employee by ID
//        Employee emp = a.getEmployee(255530);
//        // Display results
//        a.displayEmployee(emp);
//
        // Get all employees' salaries
        // ArrayList<Employee> employees = a.getSalariesByRole();
        // Test the size of the returned data - should be 240124
//        System.out.println(employees.size());
//        a.printSalaries(employees);

        // Get salaries by department name
        ArrayList<Employee> allSalary = a.getDepartment("Sales");
        a.printSalaries(allSalary);

        // Disconnect from database
        a.disconnect();
    }
}