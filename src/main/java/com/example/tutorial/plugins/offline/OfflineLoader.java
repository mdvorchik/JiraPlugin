package com.example.tutorial.plugins.offline;


import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.parser.JqlQueryParser;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
import com.example.tutorial.plugins.configurer.ConnectionConfig;
import com.example.tutorial.plugins.dao.Neo4jDao;
import com.example.tutorial.plugins.entity.Rule;

public class OfflineLoader {

    public static void loadSomeIssue(String jql) throws Exception {
        Neo4jDao neo4jDao = ConnectionConfig.getNeo4jDao();
        Rule rule = ConnectionConfig.getRule();
        JqlQueryParser jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser.class);
        SearchService searchService = ComponentAccessor.getComponent(SearchService.class);

        // Get ApplicationUser
        ApplicationUser user = ComponentAccessor.getUserManager().getUserByName("admin");

        SearchService.ParseResult parseResult = searchService.parseQuery(user, jql);

        if (parseResult.isValid()) {
            Query query = parseResult.getQuery();
            // Get total count of issues without loading them
            long totalIssueCount = searchService.searchCount(user, query);
            System.out.println("Total count of issues: " + totalIssueCount);
            int startAt = 0;
            int pageSize = 100; // Set your desired page size
            PagerFilter pagerFilter;

            while (true) {
                pagerFilter = PagerFilter.newPageAlignedFilter(startAt, pageSize);
                SearchResults searchResults = searchService.search(user, parseResult.getQuery(), pagerFilter);
                //todo get all issues count without pageSize limit, i dont want load all issues, but i want know there total count
                System.out.println("Total: " + searchResults.getTotal());

                if (searchResults.getIssues().size() == 0) {
                    break;
                }

                for (Issue issue : searchResults.getIssues()) {
                    neo4jDao.replicateIssue(issue, rule);
//                    System.out.println("Issue key: " + issue.getKey());
//
//                    // Here we'll check if issue's key equals "MYP-7" and print its "BusinessProcess" field value
//                    if (issue.getKey().equals("MYP-7")) {
//                        System.out.println("Field Value: " + issue.getCustomFieldValue(ComponentAccessor.getCustomFieldManager().getCustomFieldObjectByName("BusinessProcess")));
//                    }
                }

                startAt += pageSize;
            }
        } else {
            System.out.println("Invalid JQL query.");
        }
    }
}
