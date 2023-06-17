package com.example.tutorial.plugins.entity;

import org.codehaus.jackson.annotate.JsonProperty;

public class RelationshipV {
    @JsonProperty("source_entity")
    private String sourceEntity;

    @JsonProperty("target_entity")
    private String targetEntity;

    @JsonProperty("relation_type")
    private String relationType;

    @JsonProperty("is_directed")
    private Boolean isDirected;

    public RelationshipV() {
    }

    public RelationshipV(String sourceEntity, String targetEntity, String relationType, Boolean isDirected) {
        this.sourceEntity = sourceEntity;
        this.targetEntity = targetEntity;
        this.relationType = relationType;
        this.isDirected = isDirected;
    }

    public String getSourceEntity() {
        return sourceEntity;
    }

    public void setSourceEntity(String sourceEntity) {
        this.sourceEntity = sourceEntity;
    }

    public String getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(String targetEntity) {
        this.targetEntity = targetEntity;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public Boolean getDirected() {
        return isDirected;
    }

    public void setDirected(Boolean directed) {
        isDirected = directed;
    }
}
