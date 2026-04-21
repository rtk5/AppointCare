package com.appointment.dto;

import com.appointment.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegisterDto {
    @NotBlank private String name;
    @Email @NotBlank private String email;
    @NotBlank private String phone;
    @NotBlank private String password;
    @NotNull  private UserRole role;

    // Doctor fields
    private String specialization;
    private String qualification;
    private String hospital;
    private double consultationFee;

    // Admin field
    private String department;

    public String getName()                  { return name; }
    public void setName(String v)            { this.name = v; }
    public String getEmail()                 { return email; }
    public void setEmail(String v)           { this.email = v; }
    public String getPhone()                 { return phone; }
    public void setPhone(String v)           { this.phone = v; }
    public String getPassword()              { return password; }
    public void setPassword(String v)        { this.password = v; }
    public UserRole getRole()                { return role; }
    public void setRole(UserRole v)          { this.role = v; }
    public String getSpecialization()        { return specialization; }
    public void setSpecialization(String v)  { this.specialization = v; }
    public String getQualification()         { return qualification; }
    public void setQualification(String v)   { this.qualification = v; }
    public String getHospital()              { return hospital; }
    public void setHospital(String v)        { this.hospital = v; }
    public double getConsultationFee()       { return consultationFee; }
    public void setConsultationFee(double v) { this.consultationFee = v; }
    public String getDepartment()            { return department; }
    public void setDepartment(String v)      { this.department = v; }
}
