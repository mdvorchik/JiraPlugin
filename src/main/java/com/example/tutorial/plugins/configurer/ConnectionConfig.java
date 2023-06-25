package com.example.tutorial.plugins.configurer;

import com.example.tutorial.plugins.dao.Neo4jDao;
import com.example.tutorial.plugins.entity.*;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import java.util.ArrayList;
import java.util.List;

public class ConnectionConfig {
    private static Driver driver = null;
    private static Neo4jDao neo4jDao = null;
    private static Rule rule = null;


    public static Driver getDriver() {
        if (driver == null) {
            driver = GraphDatabase.driver("neo4j://46.17.250.8:7687", AuthTokens.none());
        }
        return driver;
    }

    public static Neo4jDao getNeo4jDao() {
        if (neo4jDao == null) {
            neo4jDao = new Neo4jDao(getDriver());
        }
        return neo4jDao;
    }

    public static Rule getRule() {
        if (rule == null) {
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
        return rule;
    }
}
