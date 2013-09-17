package org.keycloak.forms;

import org.keycloak.services.models.RealmModel;

public class RealmBeanFm {

    private RealmModel realm;

    private boolean saas;


    public RealmBeanFm(RealmModel realmModel) {
        realm = realmModel;
        saas = RealmModel.DEFAULT_REALM.equals(realmModel.getName());
    }

    public String getId() {
        return realm.getId();
    }

    public String getName() {
        return saas ? "Keycloak" : realm.getName();
    }

    public RealmModel getRealm() {
        return realm;
    }

    public boolean isSaas() {
        return saas;
    }

    public boolean isSocial() {
        return realm.isSocial();
    }

    public boolean isRegistrationAllowed() {
        return realm.isRegistrationAllowed();
    }
    
}
