package com.example.tutorial.plugins.entity;

import org.codehaus.jackson.annotate.JsonProperty;

public class Property {
    @JsonProperty("name")
    private String name;

    @JsonProperty("field_type")
    private String fieldType;

    public Property() {
    }

    public Property(String name, String fieldType) {
        this.name = name;
        this.fieldType = fieldType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }
}
