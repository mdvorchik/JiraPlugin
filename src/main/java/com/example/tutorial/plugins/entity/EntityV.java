package com.example.tutorial.plugins.entity;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class EntityV {
    @JsonProperty("type")
    private String type;

    @JsonProperty("properties")
    private List<PropertyV> properties;

    @JsonProperty("nodes")
    private List<NodeV> nodeVS;

    @JsonProperty("relationships")
    private List<RelationshipV> relationshipVS;

    public EntityV() {
    }

    public EntityV(String type, List<PropertyV> properties, List<NodeV> nodeVS, List<RelationshipV> relationshipVS) {
        this.type = type;
        this.properties = properties;
        this.nodeVS = nodeVS;
        this.relationshipVS = relationshipVS;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<PropertyV> getProperties() {
        return properties;
    }

    public void setProperties(List<PropertyV> properties) {
        this.properties = properties;
    }

    public List<NodeV> getNodes() {
        return nodeVS;
    }

    public void setNodes(List<NodeV> nodeVS) {
        this.nodeVS = nodeVS;
    }

    public List<RelationshipV> getRelationships() {
        return relationshipVS;
    }

    public void setRelationships(List<RelationshipV> relationshipVS) {
        this.relationshipVS = relationshipVS;
    }
}
