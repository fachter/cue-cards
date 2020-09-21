package com.project.cuecards.entities;

import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class Answer extends BaseEntity {

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String text;

    @ManyToOne
    private CueCard cueCard;

    public String getText() {
        return text;
    }

    public Answer setText(String text) {
        this.text = text;
        return this;
    }

    public CueCard getCueCard() {
        return cueCard;
    }

    public Answer setCueCard(CueCard cueCard) {
        this.cueCard = cueCard;
        return this;
    }
}
