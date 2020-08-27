package com.project.cuecards.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class CardLevel extends BaseEntity {

    @ManyToOne
    private CueCard cueCard;

    @ManyToOne
    private User user;

    private int usersCardLevel;

    public CueCard getCueCard() {
        return cueCard;
    }

    public CardLevel setCueCard(CueCard cueCard) {
        this.cueCard = cueCard;
        return this;
    }

    public User getUser() {
        return user;
    }

    public CardLevel setUser(User user) {
        this.user = user;
        return this;
    }

    public int getUsersCardLevel() {
        return usersCardLevel;
    }

    public CardLevel setUsersCardLevel(int usersCardLevel) {
        this.usersCardLevel = usersCardLevel;
        return this;
    }
}
