package com.smart.smartcontactmanager.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USER")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @NotBlank(message = "Name field is required!!")
    @Size(min = 2, max = 20, message = "min 2 and max 20 character required")
    private String name;

    @Column(unique = true)
    @NotBlank(message = "Email can't be empty!")
    @Pattern(regexp = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "invalid email address")
    private String email;
    @NotBlank(message = "Password field is required !!")
    @Size(min = 6, message = "password must be minimum 6 character")
    private String password;
    private  String role;
    private boolean enabled;
    private String imageUrl;

    @Column (length = 800)
    private String about;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "user")
    private List<contactDetails> contacts =new ArrayList<>();

    public User() {
        super();
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public List<contactDetails> getContacts() {
        return contacts;
    }

    public void setContacts(List<contactDetails> contacts) {
        this.contacts = contacts;
    }

//    @Override
//    public String toString() {
//        return "User{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                ", email='" + email + '\'' +
//                ", password='" + password + '\'' +
//                ", role='" + role + '\'' +
//                ", enabled=" + enabled +
//                ", imageUrl='" + imageUrl + '\'' +
//                ", about='" + about + '\'' +
//                ", contacts=" + contacts +
//                '}';
//    }
}
