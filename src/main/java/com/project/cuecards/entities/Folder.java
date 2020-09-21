package com.project.cuecards.entities;

import com.project.cuecards.enums.AccessType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Folder extends BaseEntity {

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String name;
    private AccessType accessType = AccessType.PRIVATE;
    private boolean isSet = false;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Folder rootFolder = null;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Room room = null;

    @OneToMany(mappedBy = "rootFolder", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Folder> subFolders = new ArrayList<>();

    @OneToMany(mappedBy = "set", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<CueCard> cueCards = new ArrayList<>();

    public String getName() {
        return name;
    }

    public Folder setName(String name) {
        this.name = name;
        return this;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public Folder setAccessType(AccessType accessType) {
        this.accessType = accessType;
        return this;
    }

    public boolean isSet() {
        return isSet;
    }

    public Folder setSet(boolean set) {
        isSet = set;
        return this;
    }

    public Folder getRootFolder() {
        return rootFolder;
    }

    public Folder setRootFolder(Folder rootFolder) {
        this.rootFolder = rootFolder;
        return this;
    }

    public List<Folder> getSubFolders() {
        return subFolders;
    }

    public List<CueCard> getCueCards() {
        return cueCards;
    }

    public Room getRoom() {
        return room;
    }

    public Folder setRoom(Room room) {
        this.room = room;
        return this;
    }
}
