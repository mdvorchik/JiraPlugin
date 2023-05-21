package com.example.tutorial.plugins;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class PropertyServlet extends HttpServlet {

    private final UserPropertyService userPropertyService;


    public PropertyServlet(@ComponentImport UserPropertyService userPropertyService) {
        this.userPropertyService = userPropertyService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Fetch the property details from the request
        String user = req.getParameter("user");
        String name = req.getParameter("name");
        String value = req.getParameter("value");

        // Use the user property service to add the property
        userPropertyService.addProperty(user, name, value);

        // Send a response back to the client
        resp.setContentType("text/plain");
        resp.getWriter().println("Property added successfully!");
    }
}
