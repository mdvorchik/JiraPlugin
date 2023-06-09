package com.example.tutorial.plugins.entity;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class Entity {
    @JsonProperty("type")
    private String type;

    @JsonProperty("properties")
    private List<Property> properties;

    @JsonProperty("nodes")
    private List<Node> nodes;

    @JsonProperty("relationships")
    private List<Relationship> relationships;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<Relationship> relationships) {
        this.relationships = relationships;
    }
}
