package com.carrental.entity;

import com.carrental.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Admin entity demonstrates OOP Inheritance (Admin is-a User).
 * Overrides getDisplayRole demonstrating Polymorphism.
 */
@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    @Column(name = "admin_department", length = 100)
    private String department;

    @Column(name = "employee_code", length = 50)
    private String employeeCode;

    public Admin() {
        super();
        setRole(Role.ROLE_ADMIN);
    }

    public Admin(String name, String email, String phone, String password, String department, String employeeCode) {
        super(name, email, phone, password, Role.ROLE_ADMIN);
        this.department = department;
        this.employeeCode = employeeCode;
    }

    // Demonstrates Polymorphic method overriding
    @Override
    public String getDisplayRole() {
        return "System Administrator (" + (department != null ? department : "Operations") + ")";
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }
}
