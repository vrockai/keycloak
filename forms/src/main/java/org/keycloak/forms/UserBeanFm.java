package org.keycloak.forms;

import org.keycloak.services.models.UserModel;

public class UserBeanFm {

    private UserModel user;

    public UserBeanFm(UserModel user){
        this.user = user;
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }

    public String getUsername() {
        return user.getLoginName();
    }

    public String getEmail() {
        return user.getEmail();
    }

    UserModel getUser() {
        return user;
    }

}
