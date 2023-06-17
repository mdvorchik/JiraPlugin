package com.example.tutorial.plugins.enums;

public enum StandardJiraTypes {
    ISSUE ("Issue"),
    USER ("User"),
    PROJECT ("Project");

    final String name;

    StandardJiraTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
