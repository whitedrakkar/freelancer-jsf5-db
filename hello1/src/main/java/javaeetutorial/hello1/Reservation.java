/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaeetutorial.hello1;

import java.sql.Time;
import java.sql.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 * Defines a reservation for the restaurant.
 */
@ManagedBean(name = "reservation", eager = true)
@RequestScoped
public class Reservation {

    private int id;
    private Date date;
    private Time time;
    private int numberOfPersons;
    private Room room;
    private boolean isSpecialGuest;
    private boolean isSpecialWaiter;
    private String guestName;

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public Time getTime() {
        return time;
    }

    public int getNumberOfPersons() {
        return numberOfPersons;
    }

    public Room getRoom() {
        return room;
    }

    public boolean isIsSpecialGuest() {
        return isSpecialGuest;
    }

    public boolean isIsSpecialWaiter() {
        return isSpecialWaiter;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setNumberOfPersons(int numberOfPersons) {
        this.numberOfPersons = numberOfPersons;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setIsSpecialGuest(boolean isSpecialGuest) {
        this.isSpecialGuest = isSpecialGuest;
    }

    public void setIsSpecialWaiter(boolean isSpecialWaiter) {
        this.isSpecialWaiter = isSpecialWaiter;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

}
