package com.project.cuecards.entities;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Room extends BaseEntity {

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String name;
    private String password;
    private int pictureNumber;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Folder> folders = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "room_allowed_users",
            joinColumns = @JoinColumn(name = "allowed_users_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id")
    )
    private Set<User> allowedUsers = new HashSet<>();

    public String getName() {
        return name;
    }

    public Room setName(String name) {
        this.name = name;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Room setPassword(String password) {
        this.password = password;
        return this;
    }

    public List<Folder> getFolders() {
        return folders;
    }

    public Set<User> getAllowedUsers() {
        return allowedUsers;
    }

    public Room setAllowedUsers(Set<User> allowedUsers) {
        this.allowedUsers = allowedUsers;
        return this;
    }

    public int getPictureNumber() {
        return pictureNumber;
    }

    public Room setPictureNumber(int pictureNumber) {
        this.pictureNumber = pictureNumber;
        return this;
    }
}
