package com.example.tutorial.plugins.dao;

import com.example.tutorial.plugins.entity.Entity;
import com.example.tutorial.plugins.entity.Rule;

public class EntityDao {
    public static Entity getIssueFromRule(Rule rule) {
        return rule.getEntities().stream()
                .filter(e -> e.getType().equals("Issue"))
                .findFirst()
                .get();
    }

    public static Entity getUserFromRule(Rule rule) {
        return rule.getEntities().stream()
                .filter(e -> e.getType().equals("User"))
                .findFirst()
                .get();
    }
}
