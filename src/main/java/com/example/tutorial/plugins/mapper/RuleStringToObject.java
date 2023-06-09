package com.example.tutorial.plugins.mapper;

import com.example.tutorial.plugins.entity.Rule;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class RuleStringToObject {

    public static Rule parseJson(String jsonString) {
        ObjectMapper mapper = new ObjectMapper();
        Rule rule = null;
        try {
            rule = mapper.readValue(jsonString, Rule.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rule;
    }

    public static void main(String[] args) {
        String jsonString = "{\n" +
                "  \"entities\": [\n" +
                "    {\n" +
                "      \"type\": \"Issue\",\n" +
                "      \"properties\": [\n" +
                "        {\n" +
                "          \"name\": \"BusinessInfo\",\n" +
                "          \"field_type\": \"drop-down\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"CreationDate\",\n" +
                "          \"field_type\": \"Date\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"nodes\": [\n" +
                "        {\n" +
                "          \"name\": \"BusinessInfo\",\n" +
                "          \"field_type\": \"drop-down\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"Subtype\",\n" +
                "          \"field_type\": \"drop-down\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"Author\",\n" +
                "          \"field_type\": \"User\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"relationships\": [\n" +
                "        {\n" +
                "          \"source_entity\": \"this\",\n" +
                "          \"target_entity\": \"BusinessInfo\",\n" +
                "          \"relation_type\": \"belongs_to\",\n" +
                "          \"is_directed\": false\n" +
                "        },\n" +
                "        {\n" +
                "          \"source_entity\": \"Subtype\",\n" +
                "          \"target_entity\": \"this\",\n" +
                "          \"relation_type\": \"belongs_to\",\n" +
                "          \"is_directed\": true\n" +
                "        },\n" +
                "        {\n" +
                "          \"source_entity\": \"Author\",\n" +
                "          \"target_entity\": \"this\",\n" +
                "          \"relation_type\": \"create\",\n" +
                "          \"is_directed\": true\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"User\",\n" +
                "      \"nodes\": [\n" +
                "        {\n" +
                "          \"name\": \"Role\",\n" +
                "          \"field_type\": \"drop-down\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"properties\": [\n" +
                "        {\n" +
                "          \"name\": \"CreationDate\",\n" +
                "          \"field_type\": \"Date\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"relationships\": [\n" +
                "        {\n" +
                "          \"source_entity\": \"this\",\n" +
                "          \"target_entity\": \"Role\",\n" +
                "          \"relation_type\": \"belongs_to\",\n" +
                "          \"is_directed\": false\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";
        Rule rule = parseJson(jsonString);
        System.out.println(rule.toString());
    }
}
