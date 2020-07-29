package com.project.cuecards.entities;

import com.project.cuecards.enums.CardType;

import javax.persistence.*;

@Entity
public class CueCard extends BaseEntity {

    private String topic;
    private String question;
    private String answer;
    private int level;

    @ManyToOne(cascade = CascadeType.ALL)
    private Folder set;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Folder getSet() {
        return set;
    }

    public CueCard setSet(Folder set) {
        this.set = set;
        return this;
    }
}
