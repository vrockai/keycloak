package org.keycloak.forms;

import java.net.URI;

import org.keycloak.services.resources.flows.Urls;

public class UrlBeanFm {

    private URI baseURI;

    private RealmBeanFm realm;

    public UrlBeanFm(RealmBeanFm realm, URI baseURI){
        this.realm = realm;
        this.baseURI = baseURI;
    }

    public RealmBeanFm getRealm() {
        return realm;
    }

    public void setRealm(RealmBeanFm realm) {
        this.realm = realm;
    }

    public String getAccessUrl() {
        return Urls.accountAccessPage(baseURI, realm.getId()).toString();
    }

    public String getAccountUrl() {
        return Urls.accountPage(baseURI, realm.getId()).toString();
    }

    URI getBaseURI() {
        return baseURI;
    }

    public String getLoginAction() {
        if (realm.isSaas()) {
            return Urls.saasLoginAction(baseURI).toString();
        } else {
            return Urls.realmLoginAction(baseURI, realm.getId()).toString();
        }
    }

    public String getLoginUrl() {
        if (realm.isSaas()) {
            return Urls.saasLoginPage(baseURI).toString();
        } else {
            return Urls.realmLoginPage(baseURI, realm.getId()).toString();
        }
    }

    public String getPasswordUrl() {
        return Urls.accountPasswordPage(baseURI, realm.getId()).toString();
    }

    public String getRegistrationAction() {
        if (realm.isSaas()) {
            return Urls.saasRegisterAction(baseURI).toString();
        } else {
            return Urls.realmRegisterAction(baseURI, realm.getId()).toString();
        }
    }

    public String getRegistrationUrl() {
        if (realm.isSaas()) {
            return Urls.saasRegisterPage(baseURI).toString();
        } else {
            return Urls.realmRegisterPage(baseURI, realm.getId()).toString();
        }
    }

    public String getSocialUrl() {
        return Urls.accountSocialPage(baseURI, realm.getId()).toString();
    }

    public String getTotpUrl() {
        return Urls.accountTotpPage(baseURI, realm.getId()).toString();
    }

}
