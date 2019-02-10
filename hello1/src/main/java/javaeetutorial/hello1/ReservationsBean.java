/**
 * Copyright (c) 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with the terms of the License at:
 * http://java.net/projects/javaeetutorial/pages/BerkeleyLicense
 */
package javaeetutorial.hello1;

import java.util.List;
import javax.ejb.EJB;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

@ManagedBean(name = "reservationsBean", eager = true)
@RequestScoped
public class ReservationsBean {

    @EJB
    private DbAccess dbAccess;

    @ManagedProperty(value = "#{reservations}")
    private List<Reservation> reservations;

    @ManagedProperty(value = "#{searchTerm}")
    private String searchTerm;

    public void searchByGuestName() {
        reservations = dbAccess.searchReservations(SearchTerms.GUEST_NAME, searchTerm);
    }

    public void searchByRoomName() {
        reservations = dbAccess.searchReservations(SearchTerms.ROOM_NAME, searchTerm);
    }

    public List<Reservation> getReservations() {
        if (reservations == null) {
            reservations = dbAccess.getAllReservations();
        }
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}
