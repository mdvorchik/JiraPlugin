package com.example.tutorial.plugins.dao;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.example.tutorial.plugins.entity.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.neo4j.driver.*;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class Neo4jDaoTest {
    private Neo4j embeddedDatabaseServer;
    private List<Issue> mockIssues;
    private Driver driver;

    @Before
    public void initializeNeo4j() {
        this.embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder().withDisabledServer().build();
        this.driver = GraphDatabase.driver(embeddedDatabaseServer.boltURI(), AuthTokens.none());

        // Инициализация mockIssues
        this.mockIssues = new ArrayList<>();
        ApplicationUser user1 = Mockito.mock(ApplicationUser.class);
        when(user1.getName()).thenReturn("User1");

        ApplicationUser user2 = Mockito.mock(ApplicationUser.class);
        when(user2.getName()).thenReturn("User2");

        for (int i = 1; i <= 3; i++) {
            Issue mockIssue = Mockito.mock(Issue.class);
            when(mockIssue.getSummary()).thenReturn("Test Summary " + i);
            when(mockIssue.getDescription()).thenReturn("Test Description " + i);
            when(mockIssue.getId()).thenReturn((long) i);
            when(mockIssue.getKey()).thenReturn("KEY-" + i);
            when(mockIssue.getCreator()).thenReturn(i % 2 == 0 ? user1 : user2);

            mockIssues.add(mockIssue);
        }
    }

    @Test
    public void replicateIssue() {
        // Инициализация Dao с встроенным URI
        Neo4jDao dao = new Neo4jDao(embeddedDatabaseServer.boltURI().toString(), "USERNAME", "PASSWORD");

        // Инициализация правила
        Entity entity = new Entity();
        entity.setType("Issue");

        List<Property> properties = new ArrayList<>();
        properties.add(new Property("summary", "string"));
        properties.add(new Property("description", "string"));
        properties.add(new Property("issueType", "string"));
        properties.add(new Property("status", "string"));
        properties.add(new Property("priority", "string"));
        properties.add(new Property("creator", "User"));
        properties.add(new Property("assignee", "User"));
        entity.setProperties(properties);

        List<Relationship> relationships = new ArrayList<>();
        Relationship rel1 = new Relationship();
        rel1.setSourceEntity("Issue");
        rel1.setTargetEntity("User");
        rel1.setRelationType("CREATED_BY");
        rel1.setDirected(true);
        relationships.add(rel1);

        Relationship rel2 = new Relationship();
        rel2.setSourceEntity("Issue");
        rel2.setTargetEntity("User");
        rel2.setRelationType("ASSIGNED_TO");
        rel2.setDirected(true);
        relationships.add(rel2);

        entity.setRelationships(relationships);

        // Тестирование метода replicateIssue
        for (Issue mockIssue : mockIssues) {
            dao.replicateIssue(mockIssue, entity);
        }

        // Проверка в базе данных
        try (Session session = driver.session()) {
            for (Issue mockIssue : mockIssues) {
                Result result = session.run("MATCH (n:Issue) WHERE n.id = $id RETURN n.summary AS summary, n.description AS description",
                        Values.parameters("id", mockIssue.getId()));
                if (result.hasNext()) {
                    Record record = result.next();
                    assertEquals(mockIssue.getSummary(), record.get("summary").asString());
                    assertEquals(mockIssue.getDescription(), record.get("description").asString());
                } else {
                    throw new AssertionError("No data found in the database for issue " + mockIssue.getKey());
                }
            }

            // Проверка связей между нодами
            Result result = session.run("MATCH (n:Issue)-[:CREATED_BY]->(m:User) RETURN count(*) AS count");
            if (result.hasNext()) {
                Record record = result.next();
                assertEquals(mockIssues.size(), record.get("count").asInt());
            } else {
                throw new AssertionError("No relationships found in the database");
            }
        }
    }

    @After
    public void closeNeo4j() {
        this.embeddedDatabaseServer.close();
    }
}
