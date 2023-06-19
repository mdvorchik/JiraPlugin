package com.example.tutorial.plugins;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.example.tutorial.plugins.dao.Neo4jDao;
import com.example.tutorial.plugins.entity.*;
import org.neo4j.driver.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IssueCreatedResolvedListener implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(IssueCreatedResolvedListener.class);

    @JiraImport
    private final EventPublisher eventPublisher;
    @JiraImport
    private final ActiveObjects activeObjects;
    private final Driver driver;
    private final Neo4jDao neo4jDao;
    private final Rule rule;

    @Autowired
    public IssueCreatedResolvedListener(EventPublisher eventPublisher, ActiveObjects activeObjects) {
        this.eventPublisher = eventPublisher;
        this.activeObjects = activeObjects;

        driver = GraphDatabase.driver("neo4j://localhost:7687", AuthTokens.none());
        neo4jDao = new Neo4jDao(driver);

        // Инициализация правила
        rule = new Rule();
        List<EntityV> entities = new ArrayList<>();
        EntityV entityV = new EntityV();
        entityV.setType("Issue");

        EntityV entityVUser = new EntityV();
        entityVUser.setType("User");

        List<PropertyV> properties = new ArrayList<>();
        properties.add(new PropertyV("key", "string"));
        properties.add(new PropertyV("summary", "string"));
        properties.add(new PropertyV("description", "string"));
        properties.add(new PropertyV("issueType", "string"));
        properties.add(new PropertyV("created", "string"));
        properties.add(new PropertyV("updated", "string"));
//        properties.add(new Property("status", "string"));
//        properties.add(new Property("priority", "string"));
//        properties.add(new Property("creator", "User"));
//        properties.add(new Property("assignee", "User"));
        entityV.setProperties(properties);

        List<PropertyV> propertiesUser = new ArrayList<>();
        propertiesUser.add(new PropertyV("id", "string"));
        propertiesUser.add(new PropertyV("username", "string"));

        List<RelationshipV> relationshipVS = new ArrayList<>();
        RelationshipV rel1 = new RelationshipV();
        rel1.setSourceEntity("this");
        rel1.setTargetEntity("creator");
        rel1.setRelationType("CREATED_BY");
        rel1.setDirected(true);
        relationshipVS.add(rel1);

        RelationshipV rel2 = new RelationshipV();
        rel2.setSourceEntity("this");
        rel2.setTargetEntity("assignee");
        rel2.setRelationType("ASSIGNED_TO");
        rel2.setDirected(true);
        relationshipVS.add(rel2);

        RelationshipV rel3 = new RelationshipV();
        rel3.setSourceEntity("this");
        rel3.setTargetEntity("issueType");
        rel3.setRelationType("BELONGS_TO");
        rel3.setDirected(false);
        relationshipVS.add(rel3);

        RelationshipV rel4 = new RelationshipV();
        rel4.setTargetEntity("this");
        rel4.setSourceEntity("reporter");
        rel4.setRelationType("REPORTED_BY");
        rel4.setDirected(true);
        relationshipVS.add(rel4);

        entityV.setRelationships(relationshipVS);

        List<NodeV> nodeVS = new ArrayList<>();
        nodeVS.add(new NodeV("assignee", "User"));
        nodeVS.add(new NodeV("issueType", "string"));
        nodeVS.add(new NodeV("creator", "User"));
        nodeVS.add(new NodeV("reporter", "User"));
        entityV.setNodes(nodeVS);

        entityVUser.setNodes(new ArrayList<>());
        entityVUser.setProperties(propertiesUser);
        entityVUser.setRelationships(new ArrayList<>());
        System.out.println("Complete entity");

        entities.add(entityV);
        entities.add(entityVUser);
        System.out.println("Complete entity user15");
        rule.setEntities(entities);
    }

    /**
     * Called when the plugin has been enabled.
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Enabling plugin");
        eventPublisher.register(this);
    }

    /**
     * Called when the plugin is being disabled or removed.
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        log.info("Disabling plugin");
        eventPublisher.unregister(this);
    }

    @EventListener
    public void onIssueEvent(IssueEvent issueEvent) {
        Long eventTypeId = issueEvent.getEventTypeId();
        Issue issue = issueEvent.getIssue();

        neo4jDao.replicateIssue(issue, rule);
        if (eventTypeId.equals(EventType.ISSUE_CREATED_ID)) {
            log.info("Issue {} has been ABOBA created at {}.", issue.getKey(), issue.getCreated());
        } else if (eventTypeId.equals(EventType.ISSUE_RESOLVED_ID)) {
            log.info("Issue {} has been resolved at {}.", issue.getKey(), issue.getResolutionDate());
        } else if (eventTypeId.equals(EventType.ISSUE_CLOSED_ID)) {
            log.info("Issue {} has been closed at {}.", issue.getKey(), issue.getUpdated());
        }
    }

}