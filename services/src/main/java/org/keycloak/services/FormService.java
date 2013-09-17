package org.keycloak.services;

import java.net.URI;

import javax.ws.rs.core.MultivaluedMap;

import org.keycloak.services.models.RealmModel;
import org.keycloak.services.models.UserModel;

public interface FormService {

    String getId();

    public String process(String pageId, FormServiceDataBean data);

    public static class FormServiceDataBean {

        private RealmModel realm;
        private UserModel userModel;
        private String error;
        private MultivaluedMap<String, String> formData;
        private URI baseURI;

        public FormServiceDataBean(RealmModel realm, UserModel userModel, MultivaluedMap<String, String> formData, String error){
            this.realm = realm;
            this.userModel = userModel;
            this.formData = formData;
            this.error = error;
        }

        public URI getBaseURI() {
            return baseURI;
        }

        public void setBaseURI(URI baseURI) {
            this.baseURI = baseURI;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public MultivaluedMap<String, String> getFormData() {
            return formData;
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
    }
}
