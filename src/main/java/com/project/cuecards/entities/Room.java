package com.project.cuecards.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Room extends BaseEntity {

    private String name;
    private String password;

    @OneToMany(cascade = CascadeType.ALL)
    private List<CueCard> cueCards = new ArrayList<>();

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

    public List<CueCard> getCueCards() {
        return cueCards;
    }

    public Room setCueCards(List<CueCard> cueCards) {
        this.cueCards = cueCards;
        return this;
    }

    public Set<User> getAllowedUsers() {
        return allowedUsers;
    }

    public Room setAllowedUsers(Set<User> allowedUsers) {
        this.allowedUsers = allowedUsers;
        return this;
    }
}
