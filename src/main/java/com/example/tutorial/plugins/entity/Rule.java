package com.example.tutorial.plugins.entity;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class Rule {
    @JsonProperty("entities")
    private List<Entity> entities;

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }
}
