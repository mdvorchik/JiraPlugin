package com.example.tutorial.plugins.dsl;

public class Statement {
    private final String cypherQuery;

    public Statement(String cypherQuery) {
        this.cypherQuery = cypherQuery;
    }

    public String getCypher() {
        return this.cypherQuery;
    }
}
