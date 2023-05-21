package com.example.tutorial.plugins;

import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ConfigServlet extends HttpServlet {
    private final UserManager userManager;
    private final TransactionTemplate transactionTemplate;

    public ConfigServlet(UserManager userManager, TransactionTemplate transactionTemplate) {
        this.userManager = userManager;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final String username = userManager.getRemoteUsername(req);
        if (username == null || !userManager.isSystemAdmin(username)) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        final String param1 = req.getParameter("param1");
        final String param2 = req.getParameter("param2");

        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction() {
                // Сохраните параметры здесь
                return null;
            }
        });
        resp.sendRedirect(req.getContextPath() + "/plugins/servlet/your-plugin");
    }
}
