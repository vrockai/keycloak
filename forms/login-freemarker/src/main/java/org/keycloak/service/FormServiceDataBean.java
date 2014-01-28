package org.keycloak.service;

import org.keycloak.forms.freemarker.FreeMarkerForms;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.social.SocialLoader;
import org.keycloak.social.SocialProvider;

import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class FormServiceDataBean {

    private RealmModel realm;
    private UserModel userModel;
    private String message;

    private FreeMarkerForms.MessageType messageType;

    private MultivaluedMap<String, String> formData;
    private Map<String, String> queryParams;
    private URI baseURI;

    private List<SocialProvider> socialProviders;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String code;

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    private String contextPath;

    public FormServiceDataBean(RealmModel realm, UserModel userModel, MultivaluedMap<String, String> formData, Map<String, String> queryParams, String message) {
        this.realm = realm;
        this.userModel = userModel;
        this.formData = formData;
        this.queryParams = queryParams;
        this.message = message;

        socialProviders = new LinkedList<SocialProvider>();
        if (realm != null) {
            Map<String, String> socialConfig = realm.getSocialConfig();
            if (socialConfig != null) {
                for (SocialProvider p : SocialLoader.load()) {
                    if (socialConfig.containsKey(p.getId() + ".key") && socialConfig.containsKey(p.getId() + ".secret")) {
                        socialProviders.add(p);
                    }
                }
            }
        }
    }

    public URI getBaseURI() {
        return baseURI;
    }

    public void setBaseURI(URI baseURI) {
        this.baseURI = baseURI;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MultivaluedMap<String, String> getFormData() {
        return formData;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }


    public String getQueryParam(String key) {
        return queryParams != null ? queryParams.get(key) : null;
    }


    public void setFormData(MultivaluedMap<String, String> formData) {
        this.formData = formData;
    }

    public RealmModel getRealm() {
        return realm;
    }

    public RealmModel setRealm(RealmModel realm) {
        return realm;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public List<SocialProvider> getSocialProviders() {
        return socialProviders;
    }

    public FreeMarkerForms.MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(FreeMarkerForms.MessageType messageType) {
        this.messageType = messageType;
    }

    /* OAuth Part */
    private MultivaluedMap<String, RoleModel> oAuthResourceRolesRequested;
    private List<RoleModel> oAuthRealmRolesRequested;
    private UserModel oAuthClient;
    private String oAuthCode;
    private String oAuthAction;

    public String getOAuthAction() {
        return oAuthAction;
    }

    public void setOAuthAction(String action) {
        this.oAuthAction = action;
    }

    public MultivaluedMap<String, RoleModel> getOAuthResourceRolesRequested() {
        return oAuthResourceRolesRequested;
    }

    public void setOAuthResourceRolesRequested(MultivaluedMap<String, RoleModel> resourceRolesRequested) {
        this.oAuthResourceRolesRequested = resourceRolesRequested;
    }

    public List<RoleModel> getOAuthRealmRolesRequested() {
        return oAuthRealmRolesRequested;
    }

    public void setOAuthRealmRolesRequested(List<RoleModel> realmRolesRequested) {
        this.oAuthRealmRolesRequested = realmRolesRequested;
    }

    public UserModel getOAuthClient() {
        return oAuthClient;
    }

    public void setOAuthClient(UserModel client) {
        this.oAuthClient = client;
    }

    public String getOAuthCode() {
        return oAuthCode;
    }

    public void setOAuthCode(String oAuthCode) {
        this.oAuthCode = oAuthCode;
    }

}
