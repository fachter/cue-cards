package com.project.cuecards.entities;

import com.project.cuecards.enums.AccessType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Folder extends BaseEntity {

    private String name;
    private AccessType accessType = AccessType.PRIVATE;
    private boolean isSet = false;

    @ManyToOne(cascade = CascadeType.ALL)
    private Folder rootFolder = null;

    @ManyToOne()
    private Room room = null;

    @OneToMany(mappedBy = "rootFolder", cascade = CascadeType.ALL)
    private List<Folder> subFolders = new ArrayList<>();

    @OneToMany(mappedBy = "set", cascade = CascadeType.ALL)
    private List<CueCard> cueCards = new ArrayList<>();

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

    public Folder setSubFolders(List<Folder> subFolders) {
        this.subFolders = subFolders;
        return this;
    }

    public List<CueCard> getCueCards() {
        return cueCards;
    }

    public Folder setCueCards(List<CueCard> cueCards) {
        this.cueCards = cueCards;
        return this;
    }

    public Room getRoom() {
        return room;
    }

    public Folder setRoom(Room room) {
        this.room = room;
        return this;
    }
}
