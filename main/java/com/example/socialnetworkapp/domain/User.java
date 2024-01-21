package com.example.socialnetworkapp.domain;
import java.util.*;

public class User extends Entity<Long> {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public String getSalt() {
        return salt;
    }

    private String salt;
    private List<User> friends = new ArrayList<>();

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
    public String getFullName(){ return firstName + " " + lastName; }
    public void addFriend(User u) {
        friends.add(u);
    }

    public void removeFriend(User u) {
        friends.remove(u);
    }

    public String getFirstName() {
        return firstName;
    }
    public String getfirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public String getlastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<User> getFriends() {
        return friends;
    }

    @Override
    public String toString() {
        return "Utilizator: " + "Nume='" + firstName + '\'' + ", prenume='" + lastName + '\'' + ", email=" + email +
                ", password=" + password;
    }
    @Override
    public int hashCode() {
        return super.hashCode() + Objects.hashCode(this.firstName + this.lastName);
    }

    @Override
    public boolean equals(Object o) {
        return this.hashCode() == o.hashCode();
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

}