package com.example.tutorial.plugins.dao;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.example.tutorial.plugins.dsl.Node;
import com.example.tutorial.plugins.dsl.Relationship;
import com.example.tutorial.plugins.dsl.Statement;
import com.example.tutorial.plugins.dsl.StatementBuilder;
import com.example.tutorial.plugins.entity.*;
import com.example.tutorial.plugins.enums.StandardJiraTypes;

import java.util.*;
import java.util.Set;

import static com.example.tutorial.plugins.enums.StandardJiraTypes.*;

public class GraphDbService {

    private final Rule ruleMapping;

    public GraphDbService(Rule ruleMapping) {
        this.ruleMapping = ruleMapping;
    }

    public StatementBuilder processIssue(Issue jiraIssue, Map<StandardJiraTypes, Set<String>> typesToUsedIds, StatementBuilder builder) {
        if (typesToUsedIds.get(ISSUE).contains(jiraIssue.getKey())) {
            return builder;
        }
        Map<String, Node> fieldNameToNode = new HashMap<>();
        typesToUsedIds.get(ISSUE).add(jiraIssue.getKey());
        EntityV ruleEntity = EntityDao.getIssueFromRule(ruleMapping);

        Node issueNode = Node.named(jiraIssue.getKey()).withLabel(ISSUE.getName());
        fieldNameToNode.put("this", issueNode);

        issueNode = getNodeWithProperties(jiraIssue, ruleEntity, issueNode);

        if (builder == null) {
            builder = StatementBuilder.create().merge(issueNode);
        }

        builder = createNodes(jiraIssue, typesToUsedIds, fieldNameToNode, ruleEntity, builder);

        builder = createRelates(fieldNameToNode, ruleEntity, builder);

        Statement statement = builder.build();
        String cypherQuery = statement.getCypher();
        System.out.println(cypherQuery);
        return builder;
    }

    private static StatementBuilder createRelates(Map<String, Node> fieldNameToNode, EntityV ruleEntity, StatementBuilder builder) {
        for (RelationshipV relationship : ruleEntity.getRelationships()) {
            String relationType = relationship.getRelationType();
            String sourceEntity = relationship.getSourceEntity();
            String targetEntity = relationship.getTargetEntity();

            Node sourceNode = fieldNameToNode.get(sourceEntity);
            Node targetNode = fieldNameToNode.get(targetEntity);
            if (relationship.getDirected()) {
                Relationship relates = sourceNode.relationshipTo(targetNode, relationType);
                builder = builder.merge(relates);
            } else {
                Relationship relates = sourceNode.relationshipBetween(targetNode, relationType);
                builder = builder.merge(relates);
            }
        }
        return builder;
    }

    private StatementBuilder createNodes(Object jiraIssue, Map<StandardJiraTypes, Set<String>> typesToUsedIds, Map<String, Node> fieldNameToNode, EntityV ruleEntity, StatementBuilder builder) {
        for (NodeV nodeV : ruleEntity.getNodes()) {
            Object fieldValue = getFieldValue(jiraIssue, nodeV.getName());
            if (fieldValue instanceof ApplicationUser) {
                builder = processUser((ApplicationUser) fieldValue, typesToUsedIds, builder);
                continue;
            }
            if (fieldValue instanceof Issue) {
                builder = processIssue((Issue) fieldValue, typesToUsedIds, builder);
                continue;
            }
            if (fieldValue instanceof Project) {
                builder = processProject((Project) fieldValue, typesToUsedIds, builder);
                continue;
            }
            Node nodeFromRule = Node.named(fieldValue.toString()).withLabel(nodeV.getFieldType());
            fieldNameToNode.put(nodeV.getName(), nodeFromRule);

            // Add creation of nodeFromRule to StatementBuilder.OngoingMerge builder
            builder = builder.merge(nodeFromRule);
        }
        return builder;
    }


    private StatementBuilder processProject(Project project, Map<StandardJiraTypes, Set<String>> typesToUsedIds, StatementBuilder builder) {
        if (typesToUsedIds.get(PROJECT).contains(project.getKey())) {
            return builder;
        }
        Map<String, Node> fieldNameToNode = new HashMap<>();
        typesToUsedIds.get(PROJECT).add(project.getKey());
        EntityV ruleEntity = EntityDao.getProjectFromRule(ruleMapping);

        Node issueNode = Node.named(project.getKey()).withLabel(PROJECT.getName());
        fieldNameToNode.put("this", issueNode);

        issueNode = getNodeWithProperties(project, ruleEntity, issueNode);

        builder = builder.merge(issueNode);

        builder = createNodes(project, typesToUsedIds, fieldNameToNode, ruleEntity, builder);

        builder = createRelates(fieldNameToNode, ruleEntity, builder);

        return builder;
    }

    private StatementBuilder processUser(ApplicationUser user, Map<StandardJiraTypes, Set<String>> typesToUsedIds, StatementBuilder builder) {
        if (typesToUsedIds.get(USER).contains(user.getKey())) {
            return builder;
        }
        Map<String, Node> fieldNameToNode = new HashMap<>();
        typesToUsedIds.get(USER).add(user.getKey());
        EntityV ruleEntity = EntityDao.getIssueFromRule(ruleMapping);

        Node issueNode = Node.named(user.getKey()).withLabel(USER.getName());
        fieldNameToNode.put("this", issueNode);


        issueNode = getNodeWithProperties(user, ruleEntity, issueNode);

        builder = builder.merge(issueNode);

        builder = createNodes(user, typesToUsedIds, fieldNameToNode, ruleEntity, builder);

        builder = createRelates(fieldNameToNode, ruleEntity, builder);

        return builder;
    }

    private Node getNodeWithProperties(Object jiraIssue, EntityV ruleEntity, Node issueNode) {
        Map<String, Object> properties = new HashMap<>();
        for (PropertyV property : ruleEntity.getProperties()) {
            Object fieldValue = getFieldValue(jiraIssue, property.getName());
            properties.put(property.getName(), fieldValue.toString());
        }
        return issueNode.withProperties(properties);
    }

    private Object getFieldValue(Object jiraObject, String fieldName) {
        if (jiraObject instanceof Issue) {
            return getIssueFieldValue((Issue) jiraObject, fieldName);
        } else if (jiraObject instanceof ApplicationUser) {
            return getUserFieldValue((ApplicationUser) jiraObject, fieldName);
        }
        else if (jiraObject instanceof Project) {
            return getProjectFieldValue((Project) jiraObject, fieldName);
        }
        throw new IllegalArgumentException("Unknown object type: " + jiraObject.getClass());
    }

    private Object getIssueFieldValue(Issue jiraIssue, String fieldName) {

        switch (fieldName) {
            case "id":
                return String.valueOf(jiraIssue.getId());
            case "key":
                return jiraIssue.getKey();
            case "summary":
                return jiraIssue.getSummary();
            case "created":
                return jiraIssue.getCreated();
            case "updated":
                return jiraIssue.getUpdated();
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
                return jiraIssue.getCreator().getName(); // или другой метод, возвращающий уникальное имя пользователя
            case "assignee":
                return jiraIssue.getAssignee().getName(); // или другой метод, возвращающий уникальное имя пользователя
            default:
                CustomField customField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(fieldName);
                return jiraIssue.getCustomFieldValue(customField);
        }
    }

    private Object getUserFieldValue(ApplicationUser jiraUser, String fieldName) {
        switch (fieldName) {
            case "id":
                return String.valueOf(jiraUser.getKey());
            case "username":
                return jiraUser.getName();
            // обработка других полей пользователя...
            default:
                throw new IllegalArgumentException("Unknown field: " + fieldName);
        }
    }

    private Object getProjectFieldValue(Project jiraProject, String fieldName) {
        switch (fieldName) {
            case "id":
                return String.valueOf(jiraProject.getId());
            case "name":
                return jiraProject.getName();
            case "description":
                return jiraProject.getDescription();
            case "key":
                return jiraProject.getKey();
            // обработка других полей пользователя...
            default:
                throw new IllegalArgumentException("Unknown field: " + fieldName);
        }
    }
}
