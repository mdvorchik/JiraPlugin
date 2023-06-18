package com.example.tutorial.plugins.dsl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Node {
    private String label;
    private final String name;
    private final Map<String, Object> properties = new HashMap<>();

    private Node(String label, String name) {
        this.label = label;
        this.name = name;
    }

    public static Node named(String name) {
        return new Node(null, name);
    }

    public Node withLabel(String label) {
        this.label = label;
        return this;
    }

    public Node withProperties(Map<String, Object> properties) {
        this.properties.putAll(properties);
        return this;
    }

    public Relationship relationshipTo(Node node, String relationType) {
        return new Relationship(this, node, relationType, true);
    }

    public Relationship relationshipBetween(Node node, String relationType) {
        return new Relationship(this, node, relationType, false);
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(label, node.label) && Objects.equals(name, node.name) && Objects.equals(properties, node.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, name, properties);
    }
}

