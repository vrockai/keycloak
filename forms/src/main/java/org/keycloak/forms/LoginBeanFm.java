package org.keycloak.forms;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.keycloak.forms.model.RequiredCredential;

public class LoginBeanFm {

    private RealmBeanFm realm;

    private String username;

    private String password;

    private List<RequiredCredential> requiredCredentials;

    public LoginBeanFm(RealmBeanFm realm, MultivaluedMap<String, String> formData){

        this.realm = realm;

        if (formData != null) {
            username = formData.getFirst("username");
            password = formData.getFirst("password");
        }

        requiredCredentials = new LinkedList<RequiredCredential>();
        for (org.keycloak.services.models.RequiredCredentialModel c : realm.getRealm().getRequiredCredentials()) {
            if (c.isInput()) {
                requiredCredentials.add(new RequiredCredential(c.getType(), c.isSecret(), c.getFormLabel()));
            }
        }

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<RequiredCredential> getRequiredCredentials() {
        return requiredCredentials;
    }

    public RealmBeanFm getRealm() {
        return realm;
    }

    public void setRealm(RealmBeanFm realm) {
        this.realm = realm;
    }

}
