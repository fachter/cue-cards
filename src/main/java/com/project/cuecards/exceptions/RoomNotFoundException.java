package com.project.cuecards.exceptions;

public class RoomNotFoundException extends Exception {

    public RoomNotFoundException() {
        super("Kein Raum zu dieser Id gefunden");
    }
}
