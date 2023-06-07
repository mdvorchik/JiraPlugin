package com.example.tutorial.plugins;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.example.tutorial.plugins.offline.OfflineLoader;
import org.apache.commons.lang.StringUtils;

public class UserPropertyFormAction extends JiraWebActionSupport {

    private String user;
    private String name;
    private String value;


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String doDefault() throws Exception {
        System.out.println("BBBB" + user);

        return INPUT;
    }

    @Override
    protected String doExecute() throws Exception {
        // Validate the form data
        System.out.println("BBBBAAAA" + user);
        OfflineLoader.loadSomeIssue();
        if (StringUtils.isBlank(user) || StringUtils.isBlank(name) || StringUtils.isBlank(value)) {
            System.err.println("All fields are required.");
            return INPUT;
        }

        // Add the property using your UserPropertyService
//        UserPropertyService userPropertyService = ComponentAccessor.getOSGiComponentInstanceOfType(UserPropertyService.class);
//        userPropertyService.addProperty(user, name, value);

        // Return a success view
        return SUCCESS;
    }
}
