package com.example.tutorial.plugins.dao;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.example.tutorial.plugins.entity.*;
import org.neo4j.driver.*;

import java.util.HashMap;
import java.util.Map;

public class Neo4jDao {
    private final Driver driver;

    public Neo4jDao(String uri, String username, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }

    public void replicateIssue(Issue jiraIssue, Entity config) {
        try (Session session = driver.session()) {
            session.writeTransaction(tx -> executeQuery(jiraIssue, config, tx));
        }
    }

    private Object executeQuery(Issue jiraIssue, Entity config, Transaction tx) {
        String query = buildQuery(jiraIssue, config);
        Map<String, Object> parameters = buildParameters(jiraIssue, config);
        tx.run(query, parameters);
        return null;
    }

    private String buildQuery(Issue jiraIssue, Entity config) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("MERGE (i:Issue {id: $issueId, key: $issueKey})");

        for (Relationship relationship : config.getRelationships()) {
            queryBuilder.append(buildRelationshipQuery(relationship));
        }

        for (Property property : config.getProperties()) {
            queryBuilder.append(buildPropertyQuery(property));
        }

        return queryBuilder.toString();
    }

    private String buildRelationshipQuery(Relationship relationship) {
        return String.format(" MERGE (n:%s {id: $%s, name: $%s}) MERGE (i)-[:%s]->(n)",
                relationship.getSourceEntity(),
                relationship.getSourceEntity(),
                relationship.getTargetEntity(),
                relationship.getRelationType());
    }

    private String buildPropertyQuery(Property property) {
        return String.format(" SET i.%s = $%s", property.getName(), property.getName());
    }

    private Map<String, Object> buildParameters(Issue jiraIssue, Entity config) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("issueId", String.valueOf(jiraIssue.getId()));
        parameters.put("issueKey", jiraIssue.getKey());

        for (Node node : config.getNodes()) {
            parameters.put(node.getName(), getFieldValue(jiraIssue, node.getName()));
        }

        for (Property property : config.getProperties()) {
            parameters.put(property.getName(), getFieldValue(jiraIssue, property.getName()));
        }

        return parameters;
    }

    private Object getFieldValue(Issue jiraIssue, String fieldName) {
        switch (fieldName) {
            case "id":
                return String.valueOf(jiraIssue.getId());
            case "key":
                return jiraIssue.getKey();
            case "summary":
                return jiraIssue.getSummary();
            case "description":
                return jiraIssue.getDescription();
            case "status":
                return jiraIssue.getStatus().getName();
            case "issueType":
                return jiraIssue.getIssueType().getName();
            // Fetching a custom field value
            case "customFieldName":
                CustomField customField = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectByName(fieldName);
                return jiraIssue.getCustomFieldValue(customField);
            // additional fields...
            default:
                throw new IllegalArgumentException("Unknown field: " + fieldName);
        }
    }
}

