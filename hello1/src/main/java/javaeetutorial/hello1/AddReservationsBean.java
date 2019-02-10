/**
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with the terms of the License at:
 * http://java.net/projects/javaeetutorial/pages/BerkeleyLicense
 */
package javaeetutorial.hello1;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name = "addReservationsBean", eager = true)
@RequestScoped
public class AddReservationsBean {

    private static int ROOM_NONE = -1;

    @EJB
    private DbAccess dbAccess;

    @ManagedProperty(value = "#{date}")
    private String date;

    @ManagedProperty(value = "#{time}")
    private String time;

    @ManagedProperty(value = "#{numberOfPersons}")
    private int numberOfPersons;

    @ManagedProperty(value = "#{isSpecialGuest}")
    private boolean isSpecialGuest;

    @ManagedProperty(value = "#{isSpecialWaiter}")
    private boolean isSpecialWaiter;

    @ManagedProperty(value = "#{roomId}")
    private int roomId;

    @ManagedProperty(value = "#{rooms}")
    private List<Room> rooms;

    @ManagedProperty(value = "#{guestName}")
    private String guestName;

    public void addReservation() throws IOException {

        Reservation reservation = new Reservation();
        reservation.setDate(Date.valueOf(date));
        reservation.setIsSpecialGuest(isSpecialGuest);
        reservation.setIsSpecialWaiter(isSpecialWaiter);

        if (roomId != ROOM_NONE) {
            reservation.setRoom(new Room());
            reservation.getRoom().setId(roomId);
        }
        reservation.setTime(Time.valueOf(time));
        reservation.setGuestName(guestName);

        dbAccess.insert(reservation);

        FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getNumberOfPersons() {
        return numberOfPersons;
    }

    public boolean isIsSpecialGuest() {
        return isSpecialGuest;
    }

    public boolean isIsSpecialWaiter() {
        return isSpecialWaiter;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setNumberOfPersons(int numberOfPersons) {
        this.numberOfPersons = numberOfPersons;
    }

    public void setIsSpecialGuest(boolean isSpecialGuest) {
        this.isSpecialGuest = isSpecialGuest;
    }

    public void setIsSpecialWaiter(boolean isSpecialWaiter) {
        this.isSpecialWaiter = isSpecialWaiter;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public List<Room> getRooms() {
        if (rooms == null) {
            rooms = initializeRooms();
        }
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    private List<Room> initializeRooms() {
        List<Room> rooms = new ArrayList<Room>();

        Room roomNone = new Room();
        roomNone.setId(ROOM_NONE);
        roomNone.setName("");
        rooms.add(roomNone);

        rooms.addAll(dbAccess.getAllRooms());

        return rooms;
    }
}
