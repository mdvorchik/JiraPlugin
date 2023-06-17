package com.example.tutorial.plugins.dao;

import com.example.tutorial.plugins.entity.EntityV;
import com.example.tutorial.plugins.entity.Rule;

import static com.example.tutorial.plugins.enums.StandardJiraTypes.*;

public class EntityDao {
    public static EntityV getIssueFromRule(Rule rule) {
        return rule.getEntities().stream()
                .filter(e -> e.getType().equals(ISSUE.getName()))
                .findFirst()
                .get();
    }

    public static EntityV getUserFromRule(Rule rule) {
        return rule.getEntities().stream()
                .filter(e -> e.getType().equals(USER.getName()))
                .findFirst()
                .get();
    }

    public static EntityV getProjectFromRule(Rule rule) {
        return rule.getEntities().stream()
                .filter(e -> e.getType().equals(PROJECT.getName()))
                .findFirst()
                .get();
    }
}
