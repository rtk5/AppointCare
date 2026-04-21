package com.appointment.model;

import jakarta.persistence.*;

@Entity
@Table(name = "admins")
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    private String department;

    public Admin() {}

    public Admin(String name, String email, String phone, String password, String department) {
        super(name, email, phone, password, UserRole.ADMIN);
        this.department = department;
    }

    @Override
    public UserRole getDefaultRole() { return UserRole.ADMIN; }

    public String getDepartment()       { return department; }
    public void setDepartment(String v) { this.department = v; }
}
