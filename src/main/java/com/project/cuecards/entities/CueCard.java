package com.project.cuecards.entities;

import com.project.cuecards.enums.CardType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CueCard extends BaseEntity {

    @Lob
    private String topic;
    @Lob
    private String question;
    @Lob
    private String solution;
    private CardType cardType = CardType.FT;

    @OneToMany(mappedBy = "cueCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Answer> answers = new ArrayList<>();

    @ManyToOne()
    private Folder set;

    @OneToMany(cascade = CascadeType.ALL)
    private List<CardLevel> cardLevels = new ArrayList<>();

    public String getTopic() {
        return topic;
    }

    public CueCard setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public String getQuestion() {
        return question;
    }

    public CueCard setQuestion(String question) {
        this.question = question;
        return this;
    }

    public String getSolution() {
        return solution;
    }

    public CueCard setSolution(String solution) {
        this.solution = solution;
        return this;
    }

    public Folder getSet() {
        return set;
    }

    public CueCard setSet(Folder set) {
        this.set = set;
        return this;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public CardType getCardType() {
        return cardType;
    }

    public CueCard setCardType(CardType cardType) {
        this.cardType = cardType;
        return this;
    }

    public List<CardLevel> getCardLevels() {
        return cardLevels;
    }

    public CueCard setCardLevels(List<CardLevel> cardLevels) {
        this.cardLevels = cardLevels;
        return this;
    }
}
