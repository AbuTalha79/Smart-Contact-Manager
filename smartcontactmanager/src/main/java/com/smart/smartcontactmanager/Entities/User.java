package com.smart.smartcontactmanager.Entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name="USER")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotBlank(message = "Name field is require")
    @Size(min=5, max=25, message="min 5 and max 25 characters are allowed ")
    private String name;

    @Column(unique= true)
    private String email;
    
    private String password;
    private String role;
    private String imageUrl;

    @Column(length = 500)
    private String about;

    private Boolean  enabled;


    //object of user to store data in the LIST and create  mapping 
    @OneToMany(cascade = CascadeType.ALL, fetch =  FetchType.LAZY, mappedBy = "user", orphanRemoval = true)
    private List <Contact> contacts = new ArrayList<>();


    //default constructor
    public User() {
        super();
        //todo
    }



    //getter and setter
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


    public Boolean getEnabled() {
        return enabled;
    }


    public void setEnabled(boolean b) {
        this.enabled = b;
    }



    public List<Contact> getContacts() {
        return contacts;
    }



    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }



    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", role=" + role
                + ", imageUrl=" + imageUrl + ", about=" + about + ", enabled=" + enabled + ", contacts=" + contacts
                + "]";
    }


    

}
