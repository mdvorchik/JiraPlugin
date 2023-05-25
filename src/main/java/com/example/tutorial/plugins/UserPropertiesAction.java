package com.example.tutorial.plugins;

import com.atlassian.jira.web.action.JiraWebActionSupport;

public class UserPropertiesAction extends JiraWebActionSupport {

    private String ruleSchema;
    private String info;


    @Override
    public String doDefault() throws Exception {
        info = "{\n" +
                "  \"entities\": [\n" +
                "    {\n" +
                "      \"name\": \"entity_name\",\n" +
                "      \"type\": \"type_name\",\n" +
                "      \"nodes\": [\n" +
                "        {\n" +
                "          \"name\": \"field_name\",\n" +
                "          \"id_field\": \"id_field_name\",\n" +
                "          \"properties\": [\n" +
                "            {\n" +
                "              \"name\": \"field_name\",\n" +
                "              \"value_field\": \"value_field_name\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ],\n" +
                "      \"relationships\": [\n" +
                "        {\n" +
                "          \"source_entity\": \"source_entity/node_name\",\n" +
                "          \"target_entity\": \"target_entity/node_name\",\n" +
                "          \"relation_type\": \"relation_name\",\n" +
                "          \"is_directed\": true\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";
        return INPUT;
    }

    @Override
    protected String doExecute() throws Exception {
        System.out.println("ruleSchema: " + ruleSchema);
        return SUCCESS;
    }

    public void setRuleSchema(String meh) {
        this.ruleSchema = meh;
    }

    public String getRuleSchema() {
        return ruleSchema;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}