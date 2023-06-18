package com.example.tutorial.plugins.dao;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.user.ApplicationUser;
import com.example.tutorial.plugins.dsl.StatementBuilder;
import com.example.tutorial.plugins.entity.EntityV;
import com.example.tutorial.plugins.entity.PropertyV;
import com.example.tutorial.plugins.entity.RelationshipV;
import com.example.tutorial.plugins.entity.Rule;
import com.example.tutorial.plugins.enums.StandardJiraTypes;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.example.tutorial.plugins.enums.StandardJiraTypes.*;

public class Neo4jDao {
    private final Driver driver;

    public Neo4jDao(Driver driver) {
        this.driver = driver;
    }

    public void replicateIssue(Object jiraObject, Rule configAll) {
        Map<StandardJiraTypes, Set<String>> typesToUsedIds = new HashMap<>();
        typesToUsedIds.put(ISSUE, new HashSet<>());
        typesToUsedIds.put(USER, new HashSet<>());
        typesToUsedIds.put(PROJECT, new HashSet<>());
        StatementBuilder builder =
                new GraphDbService(configAll).processIssue((Issue) jiraObject, typesToUsedIds, null, new HashMap<>(), "this");
        try (Session session = driver.session()) {
//            session.writeTransaction(tx -> executeQuery(jiraObject, config, tx));
            session.writeTransaction(tx -> tx.run(builder.build().getCypher()));
        }
    }


    private Object executeQuery(Object jiraObject, EntityV config, Transaction tx) {
        String query = buildQuery(jiraObject, config);
        Map<String, Object> parameters = buildParameters(jiraObject, config);
        tx.run(query, parameters);
        return null;
    }

    private String buildQuery(Object jiraObject, EntityV config) {
        if (jiraObject instanceof Issue) {
            return buildEntityQuery((Issue) jiraObject, config, "Issue");
        } else if (jiraObject instanceof ApplicationUser) {
            return buildEntityQuery((ApplicationUser) jiraObject, config, "User");
        } else {
            throw new IllegalArgumentException("Unknown object type: " + jiraObject.getClass().getName());
        }
    }

    private Map<String, Object> buildParameters(Object jiraObject, EntityV config) {
        if (jiraObject instanceof Issue) {
            return buildEntityParameters((Issue) jiraObject, config);
        } else if (jiraObject instanceof ApplicationUser) {
            return buildEntityParameters((ApplicationUser) jiraObject, config);
        } else {
            throw new IllegalArgumentException("Unknown object type: " + jiraObject.getClass().getName());
        }
    }

    private String buildEntityQuery(Object jiraEntity, EntityV config, String entityName) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("MERGE (e:" + entityName + " {id: $entityId, key: $entityKey})");

        for (RelationshipV relationshipV : config.getRelationships()) {
            queryBuilder.append(buildRelationshipQuery(relationshipV));
        }

        for (PropertyV propertyV : config.getProperties()) {
            queryBuilder.append(buildPropertyQuery(propertyV));
        }

        return queryBuilder.toString();
    }

    private Map<String, Object> buildEntityParameters(Object jiraEntity, EntityV config) {
        Map<String, Object> parameters = new HashMap<>();
        try {
            Method getId = jiraEntity.getClass().getMethod("getId");
            Method getKey = jiraEntity.getClass().getMethod("getKey");
            parameters.put("entityId", String.valueOf(getId.invoke(jiraEntity)));
            parameters.put("entityKey", getKey.invoke(jiraEntity));
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        System.err.println("1");
//        for (Node node : config.getNodes()) {
//            parameters.put(node.getName(), getFieldValue(jiraEntity, node.getName()));
//        }

        for (PropertyV propertyV : config.getProperties()) {
            parameters.put(propertyV.getName(), getFieldValue(jiraEntity, propertyV.getName()));
        }

        for (RelationshipV relationshipV : config.getRelationships()) {
            parameters.put(relationshipV.getSourceEntity(), relationshipV.getSourceEntity());
            parameters.put(relationshipV.getTargetEntity(), relationshipV.getTargetEntity());
        }

        return parameters;
    }

    private String buildRelationshipQuery(RelationshipV relationshipV) {
        return String.format(" MERGE (n%s:%s {id: $%s, name: $%s}) MERGE (e)-[:%s]->(n%s)",
                relationshipV.getRelationType(),
                relationshipV.getSourceEntity(),
                relationshipV.getSourceEntity(),
                relationshipV.getTargetEntity(),
                relationshipV.getRelationType(),
                relationshipV.getRelationType());
    }

    private String buildPropertyQuery(PropertyV propertyV) {
        return String.format(" SET e.%s = $%s", propertyV.getName(), propertyV.getName());
    }

    private Object getFieldValue(Object jiraEntity, String fieldName) {
        if (jiraEntity instanceof Issue) {
            return getIssueFieldValue((Issue) jiraEntity, fieldName);
        } else if (jiraEntity instanceof ApplicationUser) {
            return getUserFieldValue((ApplicationUser) jiraEntity, fieldName);
        } else {
            throw new IllegalArgumentException("Unknown object type: " + jiraEntity.getClass().getName());
        }
    }

    private Object getIssueFieldValue(Issue jiraIssue, String fieldName) {
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
                if (jiraIssue.getIssueType() == null) {
                    return "Default";
                } else {
                    return jiraIssue.getIssueType().getName();
                }
            case "creator":
                return jiraIssue.getCreator().getUsername(); // или другой метод, возвращающий уникальное имя пользователя
            case "assignee":
                return jiraIssue.getAssignee().getUsername(); // или другой метод, возвращающий уникальное имя пользователя
            case "customFieldName":
                CustomField customField = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectByName(fieldName);
                return jiraIssue.getCustomFieldValue(customField);
            default:
                throw new IllegalArgumentException("Unknown field: " + fieldName);
        }
    }

    private Object getUserFieldValue(ApplicationUser jiraUser, String fieldName) {
        switch (fieldName) {
            case "id":
                return String.valueOf(jiraUser.getId());
            case "username":
                return jiraUser.getUsername();
            // обработка других полей пользователя...
            default:
                throw new IllegalArgumentException("Unknown field: " + fieldName);
        }
    }
}
