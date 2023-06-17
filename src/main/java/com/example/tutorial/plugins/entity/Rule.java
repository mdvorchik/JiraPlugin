package com.example.tutorial.plugins.entity;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class Rule {
    @JsonProperty("entities")
    private List<EntityV> entities;

    public Rule() {
    }

    public Rule(List<EntityV> entities) {
        this.entities = entities;
    }

    public List<EntityV> getEntities() {
        return entities;
    }

    public void setEntities(List<EntityV> entities) {
        this.entities = entities;
    }
}
