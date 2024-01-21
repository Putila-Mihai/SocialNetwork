package com.example.socialnetworkapp.domain;

import java.util.Objects;
import java.util.Optional;

public class Cerere extends Entity<Long> {
    private User User1;
    private User User2;
    private Status status;
    private static long idCounter = 0;

    public Cerere(User User1, User User2, Status status) {
        this.setId(selfid());
        this.User1 = User1;
        this.User2 = User2;
        this.status=status;
    }

    public Cerere(Optional<User> u1, Optional<User> u2, Status status) {
        if (u1.isPresent() && u2.isPresent()) {
            this.User1 = u1.get();
            this.User2 = u2.get();
            this.status=status;
            this.setId(selfid());
        }
    }

    private long selfid() {
        return idCounter++;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser1() {
        return User1;
    }

    public void setUser1(User User1) {
        this.User1 = User1;
    }

    public User getUser2() {
        return User2;
    }

    public void setUser2(User User2) {
        this.User2 = User2;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Cerere cerere = (Cerere) o;
        return Objects.equals(User1, cerere.User1) && Objects.equals(User2, cerere.User2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), User1, User2);
    }

    public String toString(){
        return "id="+id+", Useri: "+User1.getFirstName()+" "+User1.getLastName()+", "+User2.getFirstName()
                +" "+User2.getLastName()+", status: "+status;
    }
}

