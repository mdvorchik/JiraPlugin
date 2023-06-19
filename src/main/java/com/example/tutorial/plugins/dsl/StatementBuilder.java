package com.example.tutorial.plugins.dsl;

import java.util.*;

public class StatementBuilder {
    private final Set<Node> nodes = new LinkedHashSet<>();
    private final Set<Relationship> relationships = new LinkedHashSet<>();

    private StatementBuilder() {
    }

    public static StatementBuilder create() {
        return new StatementBuilder();
    }

    public StatementBuilder merge(Node node) {
        nodes.add(node);
        return this;
    }

    public StatementBuilder merge(Relationship relationship) {
        relationships.add(relationship);
        return this;
    }

    public Statement build() {
        StringBuilder builder = new StringBuilder();

        for (Node node : nodes) {
            builder.append("MERGE (`").append(node.getName()).append("`:");
            builder.append(node.getLabel());
            builder.append(" {");

            int propCount = 0;
            for (Map.Entry<String, Object> property : node.getProperties().entrySet()) {
                builder.append("`").append(property.getKey()).append("`");
                builder.append(": ");
                builder.append("'").append(property.getValue()).append("'");
                if (++propCount != node.getProperties().size()) {
                    builder.append(", ");
                }
            }

            builder.append("}) ");
        }

        for (Relationship relationship : relationships) {
            boolean isDirected = relationship.isDirected();
            builder.append("MERGE (`").append(relationship.getFrom().getName()).append("`)");
            builder.append("-[:").append(relationship.getType()).append("]->");
            builder.append("(`").append(relationship.getTo().getName()).append("`) ");
            if (!isDirected) {
                builder.append("MERGE (`").append(relationship.getTo().getName()).append("`)");
                builder.append("-[:").append(relationship.getType()).append("]->");
                builder.append("(`").append(relationship.getFrom().getName()).append("`) ");
            }
        }


        return new Statement(builder.toString());
    }

}

