package com.example.tutorial.plugins.dsl;

import java.util.Objects;

public class Relationship {
    private final Node from;
    private final Node to;
    private final String type;
    private final boolean directed;

    public Relationship(Node from, Node to, String type, boolean directed) {
        this.from = from;
        this.to = to;
        this.type = type;
        this.directed = directed;
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }

    public String getType() {
        return type;
    }

    public boolean isDirected() {
        return directed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Relationship that = (Relationship) o;
        return directed == that.directed && Objects.equals(from, that.from) && Objects.equals(to, that.to) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, type, directed);
    }
}
