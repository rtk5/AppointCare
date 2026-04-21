package com.appointment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank private String name;
    @Email @NotBlank @Column(unique = true) private String email;
    @NotBlank private String phone;
    @NotBlank private String password;
    @Enumerated(EnumType.STRING) private UserRole role;

    public User() {}

    public User(String name, String email, String phone, String password, UserRole role) {
        this.name = name; this.email = email; this.phone = phone;
        this.password = password; this.role = role;
    }

    public abstract UserRole getDefaultRole();

    public Long getUserId()            { return userId; }
    public void setUserId(Long v)      { this.userId = v; }
    public String getName()            { return name; }
    public void setName(String v)      { this.name = v; }
    public String getEmail()           { return email; }
    public void setEmail(String v)     { this.email = v; }
    public String getPhone()           { return phone; }
    public void setPhone(String v)     { this.phone = v; }
    public String getPassword()        { return password; }
    public void setPassword(String v)  { this.password = v; }
    public UserRole getRole()          { return role; }
    public void setRole(UserRole v)    { this.role = v; }
}
