package com.example.tutorial.plugins.dao;

import com.atlassian.jira.issue.Issue;
import com.example.tutorial.plugins.entity.Node;
import com.example.tutorial.plugins.entity.Property;
import com.example.tutorial.plugins.entity.Relationship;
import com.example.tutorial.plugins.entity.Rule;
import org.neo4j.driver.*;

import javax.transaction.Transaction;
import java.sql.Driver;
import java.util.List;

public class Neo4jDao {
    private Driver driver;


    public void insertIssueToDB(Issue issue, Rule rule) {
        // 1) Получение всех стандартных полей из issue
        String type = issue.ge();
        List<Property> properties = issue.getProperties();
        List<Node> nodes = issue.getNodes();
        List<Relationship> relationships = issue.getRelationships();

        // 2) Получение всех пользовательских полей, которые содержит правило
        List<Property> ruleProperties = rule.getProperties();

        try (Session session = driver.session()) {
            // Открываем транзакцию
            try (Transaction tx = session.beginTransaction()) {
                // 3) Репликация в базу данных Neo4j по правилу

                // Добавление узлов
                for (Node node : nodes) {
                    String name = node.getName();
                    String fieldType = node.getFieldType();

                    String statement = String.format(
                            "MERGE (n:%s {name: '%s', fieldType: '%s'})",
                            type, name, fieldType);

                    tx.run(statement);
                }

                // Добавление свойств
                for (Property property : properties) {
                    String name = property.getName();
                    String fieldType = property.getFieldType();

                    String statement = String.format(
                            "MERGE (p:%s {name: '%s', fieldType: '%s'})",
                            type, name, fieldType);

                    tx.run(statement);
                }

                // Добавление отношений
                for (Relationship relationship : relationships) {
                    String sourceEntity = relationship.getSourceEntity();
                    String targetEntity = relationship.getTargetEntity();
                    String relationType = relationship.getRelationType();
                    boolean isDirected = relationship.getIsDirected();

                    String statement = String.format(
                            "MATCH (a:%s),(b:%s) " +
                                    "MERGE (a)-[r:%s]->(b)",
                            sourceEntity, targetEntity, relationType);

                    if (!isDirected) {
                        statement = String.format(
                                "MATCH (a:%s),(b:%s) " +
                                        "MERGE (a)-[r:%s]-(b)",
                                sourceEntity, targetEntity, relationType);
                    }

                    tx.run(statement);
                }

                // Подтверждение транзакции
                tx.commit();
            }
        }
    }

}
