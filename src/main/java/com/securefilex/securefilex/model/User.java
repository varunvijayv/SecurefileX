
package com.securefilex.securefilex.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// ✅ Class names in Java should start with uppercase
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role; // ADMIN or USER

    // ✅ Add a no-argument constructor (required by JPA)
    public User() {}

    // ✅ Optionally, add a constructor for convenience
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // ✅ Getters and Setters (required for JPA to read/write fields)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
    

    public void setRole(String role) {
        this.role = role;
    }
    public String getRole() {
        return role;
    }

}
