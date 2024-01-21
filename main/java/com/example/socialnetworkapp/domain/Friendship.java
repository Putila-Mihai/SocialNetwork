package com.example.socialnetworkapp.domain;

import com.example.socialnetworkapp.service.UserService;

import java.time.LocalDateTime;


public class Friendship extends Entity<Tuple<Long,Long>> {

    private  LocalDateTime date;


    public Friendship() {
        date = LocalDateTime.now();
    }
    /**
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime time){date = time;}
    public String  getSDate(){
        return date.toString();
    }
    public long getUser1(){
        return this.id.getLeft();
    }
    public long getUser2(){
        return this.id.getRight();

    }
    public long getFriendOf(Long id) throws IllegalArgumentException {
        if (id == getUser1())
            return getUser2();
        else if (id == getUser2())
            return getUser1();
        else throw new IllegalArgumentException();
    }
    public String getUser1Name() {
        return UserService.getInstance().findUser(this.getUser1()).getFullName();
    }

    public String getUser2Name() {
        return UserService.getInstance().findUser(this.getUser2()).getFullName();
    }


}
