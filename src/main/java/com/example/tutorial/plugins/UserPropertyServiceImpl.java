package com.example.tutorial.plugins;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;

@Scanned
public class UserPropertyServiceImpl implements UserPropertyService {
    // ActiveObjects instance
//    private final ActiveObjects ao;
//
//    public UserPropertyServiceImpl(ActiveObjects ao) {
//        this.ao = ao;
//    }

    @Override
    public void addProperty(String user, String name, String value) {
//        UserPropertyResource userProperty = ao.create(UserPropertyResource.class);
//        userProperty.setUserName(user);
//        userProperty.setPropertyName(name);
//        userProperty.setProperty
//        userProperty.setPropertyValue(value);
//        userProperty.save();
        System.out.println("AAAAAAAAAAAAAAA" + user);
    }
}
