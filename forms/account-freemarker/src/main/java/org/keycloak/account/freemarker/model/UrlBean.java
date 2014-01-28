package org.keycloak.account.freemarker.model;

import org.keycloak.services.resources.flows.Urls;

import java.net.URI;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class UrlBean {

    private String realm;
    private URI baseURI;
    private String referrerURI;

    public UrlBean(String realm, URI baseURI, String referrerURI) {
        this.realm = realm;
        this.baseURI = baseURI;
        this.referrerURI = referrerURI;
    }

    public String getAccessUrl() {
        return Urls.accountAccessPage(baseURI, realm).toString();
    }

    public String getAccountUrl() {
        return Urls.accountPage(baseURI, realm).toString();
    }

    public String getPasswordUrl() {
        return Urls.accountPasswordPage(baseURI, realm).toString();
    }

    public String getSocialUrl() {
        return Urls.accountSocialPage(baseURI, realm).toString();
    }

    public String getTotpUrl() {
        return Urls.accountTotpPage(baseURI, realm).toString();
    }

    public String getTotpRemoveUrl() {
        return Urls.accountTotpRemove(baseURI, realm).toString();
    }

    public String getLogoutUrl() {
        return Urls.accountLogout(baseURI, realm).toString();
    }

    public String getReferrerURI() {
        return referrerURI;
    }

}
