package com.project.cuecards.entities;

import com.project.cuecards.enums.CardType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CueCard extends BaseEntity {

    private String topic;
    private String question;
    private String solution;
    private int level;
    private CardType cardType = CardType.FT;

    @OneToMany(mappedBy = "cueCard", cascade = CascadeType.ALL)
    private List<Answer> answers = new ArrayList<>();

    @ManyToOne()
    private Folder set;

    @ManyToOne
    private Room room;

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

    public int getLevel() {
        return level;
    }

    public CueCard setLevel(int level) {
        this.level = level;
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

    public CueCard setAnswers(List<Answer> answers) {
        this.answers = answers;
        return this;
    }

    public CardType getCardType() {
        return cardType;
    }

    public CueCard setCardType(CardType cardType) {
        this.cardType = cardType;
        return this;
    }
}
