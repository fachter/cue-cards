package com.project.cuecards.entities;

import javax.persistence.Entity;

@Entity
public class Room extends BaseEntity {

    private String name;

    public String getName() {
        return name;
    }

    public Room setName(String name) {
        this.name = name;
        return this;
    }
}
