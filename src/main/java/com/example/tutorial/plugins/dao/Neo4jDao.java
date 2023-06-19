package com.example.tutorial.plugins.dao;

import com.atlassian.jira.issue.Issue;
import com.example.tutorial.plugins.dsl.StatementBuilder;
import com.example.tutorial.plugins.entity.Rule;
import com.example.tutorial.plugins.enums.StandardJiraTypes;
import org.neo4j.driver.*;

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
            session.writeTransaction((TransactionWork<Void>) tx -> {
                tx.run(builder.build().getCypher());
                return null;
            });
        }
    }
}
