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

    @OneToMany(mappedBy = "rootFolder")
    private List<Folder> subFolders = new ArrayList<>();

    @OneToMany(mappedBy = "set", cascade = CascadeType.ALL)
    private List<CueCard> cueCards = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public boolean isSet() {
        return isSet;
    }

    public void setSet(boolean set) {
        isSet = set;
    }

    public Folder getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(Folder rootFolder) {
        this.rootFolder = rootFolder;
    }

    public List<Folder> getSubFolders() {
        return subFolders;
    }

    public void setSubFolders(List<Folder> subFolders) {
        this.subFolders = subFolders;
    }

    public List<CueCard> getCueCards() {
        return cueCards;
    }

    public void setCueCards(List<CueCard> cueCards) {
        this.cueCards = cueCards;
    }
}
