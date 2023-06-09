package com.example.tutorial.plugins.sync;

import net.java.ao.Entity;
import net.java.ao.Preload;

@Preload
public interface ReplicationTask extends Entity {
    /*
    Query query = Query.select().where(ISSUES + " = ?", issueId);
    ReplicationTask[] tasks = ao.find(ReplicationTask.class, query);
     */
    static final String ISSUES = "ISSUES";
    static final String START_TIME = "START_TIME";
    static final String END_TIME = "END_TIME";
    static final String PLUGIN_ID = "PLUGIN_ID";

    String getIssues();
    void setIssues(String issues);

    long getStartTime();
    void setStartTime(long startTime);

    long getEndTime();
    void setEndTime(long endTime);

    String getPluginId();
    void setPluginId(String pluginId);
}

